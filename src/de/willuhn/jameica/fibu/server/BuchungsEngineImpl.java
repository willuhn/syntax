/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.BuchungsEngine;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.util.GeschaeftsjahrUtil;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung der Buchungsengine.
 */
public class BuchungsEngineImpl extends UnicastRemoteObject implements BuchungsEngine
{

  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private boolean started = false;

  /**
   * ct.
   * @throws RemoteException
   */
  public BuchungsEngineImpl() throws RemoteException
  {
    super();
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BuchungsEngine#close(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void close(final Geschaeftsjahr jahr) throws RemoteException, ApplicationException
  {
    if (jahr == null)
      throw new ApplicationException(i18n.tr("Kein Geschäftsjahr angegeben"));

    Application.getController().start(new BackgroundTask() {

      /**
       * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
       */
      public void run(ProgressMonitor monitor) throws ApplicationException
      {
        try
        {
          monitor.setStatusText(i18n.tr("Schliesse Geschäftsjahr {0}",BeanUtil.toString(jahr)));

          if (jahr.isClosed())
            throw new ApplicationException(i18n.tr("Geschäftsjahr wurde bereits abgeschlossen"));
        }
        catch (RemoteException e)
        {
          Logger.error("unable to close gj",e);
          throw new ApplicationException(i18n.tr("Fehler beim Schliessen des Geschäftsjahres"));
        }
        
        
        
        try
        {
          monitor.log(i18n.tr("Starte Transaktion"));monitor.addPercentComplete(1);
          jahr.transactionBegin();
          
          Mandant m        = jahr.getMandant();
          DBService db     = Settings.getDBService();
          DBIterator list  = null;

          if (!Settings.SETTINGS.getBoolean("afa.skip",false))
          {
            monitor.log(i18n.tr("Buche Abschreibungen für Anlagevermögen"));monitor.addPercentComplete(1);
            list = m.getAnlagevermoegen();
            while (list.hasNext())
            {
              Anlagevermoegen av = (Anlagevermoegen) list.next();
              
              if (av.getNutzungsdauer() == 0)
              {
                monitor.log(i18n.tr("  Überspringe {0} - Nutzungsdauer 0 Jahre",av.getName()));
                continue;
              }
              
              AbschreibungsBuchung buchung = schreibeAb(monitor,av,jahr);

              if (buchung == null)
              {
                monitor.log(i18n.tr("  Überspringe {0} - bereits abgeschrieben",av.getName()));
                continue;
              }
              buchung.store();
              
              monitor.log(i18n.tr("  Abschreibung für {0}",av.getName()));monitor.addPercentComplete(1);
              Abschreibung afa = (Abschreibung) db.createObject(Abschreibung.class,null);
              afa.setAnlagevermoegen(av);
              afa.setBuchung(buchung);
              afa.store();
            }
          }
          else
          {
            monitor.log(i18n.tr("Überspringe Abschreibungsbuchungen"));monitor.addPercentComplete(1);
          }
          
          // Neues geschaeftsjahr erzeugen
          monitor.setStatusText(i18n.tr("Erzeuge neues Geschäftsjahr"));monitor.addPercentComplete(1);
          Geschaeftsjahr jahrNeu = (Geschaeftsjahr) db.createObject(Geschaeftsjahr.class,null);
          
          jahrNeu.setMandant(m);
          jahrNeu.setKontenrahmen(jahr.getKontenrahmen());
          
          monitor.log(i18n.tr("  Berechne Dauer des Geschäftsjahres"));monitor.addPercentComplete(1);
          
          Calendar cal = Calendar.getInstance();

          // Beginn
          cal.setTime(jahr.getEnde());
          cal.add(Calendar.DATE,1); // Ein Tag drauf.
          jahrNeu.setBeginn(cal.getTime());
          
          // Ende
          cal.add(Calendar.MONTH,jahr.getMonate());
          cal.add(Calendar.DATE,-1); // Ein Tag wieder abziehen
          jahrNeu.setEnde(cal.getTime());
          
          Date beginn = jahrNeu.getBeginn();
          Date ende   = jahrNeu.getEnde();

          monitor.log(i18n.tr("  Beginn: {0}", Settings.DATEFORMAT.format(beginn)));
          monitor.log(i18n.tr("  Ende  : {0}", Settings.DATEFORMAT.format(ende)));

          // Checken, ob sich in diesem Zeitraum schon ein Geschaeftsjahr befindet
          monitor.log(i18n.tr("  Prüfe, ob neues Geschäftsjahr bereits existiert"));monitor.addPercentComplete(1);
          DBIterator check = m.getGeschaeftsjahre();
          check.addFilter(beginn.getTime() + " < " + db.getSQLTimestamp("ende")); // TODO Der Check der Ueberschneidung ist nicht sauber
          check.addFilter(ende.getTime() + " > " + db.getSQLTimestamp("beginn"));
          if (check.hasNext())
          {
            if (!Settings.getUseExistingGjOnClose())
              throw new ApplicationException(i18n.tr("Es existiert bereits ein Geschäftsjahr, welches sich mit dem Zeitraum {0}-{1} überschneidet",Settings.DATEFORMAT.format(beginn),Settings.DATEFORMAT.format(ende)));
            jahrNeu = (Geschaeftsjahr) check.next();
            monitor.log(i18n.tr("  Verwende bereits existierendes Geschäftsjahr {0}",(String) jahrNeu.getAttribute(jahrNeu.getPrimaryAttribute())));
          }

          jahrNeu.setVorjahr(jahr);
          jahrNeu.store();
          
          // Anfangsbestaende erzeugen
          // Existierende Anfangsbestaende aus dem Vorjahr brauchen wir nicht
          // beruecksichtigen, weil sie in k.getSaldo bereits enthalten sind.
          monitor.log(i18n.tr("Erzeuge neue Anfangsbestände"));monitor.addPercentComplete(1);
          list = jahr.getKontenrahmen().getKonten();
          while (list.hasNext())
          {
            // Wir wollen den Saldo des alten Jahres
            Konto k = (Konto) list.next();
            int ka = k.getKontoArt().getKontoArt();
            if (ka != Kontoart.KONTOART_ANLAGE && ka != Kontoart.KONTOART_GELD)
            {
              monitor.log(i18n.tr("Überspringe Konto {0} - weder Anlage- noch Geldkonto",k.getKontonummer()));
              continue;
            }
              
            double saldo = k.getSaldo(jahr);

            // Wenn der Saldo 0 ist, brauchen wir keinen Anfangsbestand
            if (java.lang.Math.abs(saldo) < 0.01d)
              continue;

            // BUGZILLA 1153 - Negativen Anfangsbestand gibts nur bei Geldkonten
            if (ka == Kontoart.KONTOART_ANLAGE && saldo < 0d)
              continue;
            
            monitor.addPercentComplete(1);

            // Erzeugen aber einen Anfangsbestand fuers neue Jahr
            Anfangsbestand ab = (Anfangsbestand) db.createObject(Anfangsbestand.class,null);
            ab.setBetrag(saldo);
            ab.setKonto(k);
            ab.setGeschaeftsjahr(jahrNeu);
            ab.store();
            monitor.log(i18n.tr("  [" + k.getKontonummer() + "] " + k.getName() + ": " + saldo));
          }
          
          monitor.log(i18n.tr("Schliesse altes Geschäftsjahr"));monitor.addPercentComplete(1);
          jahr.setClosed(true);
          jahr.store();

          jahr.transactionCommit();
          monitor.log(i18n.tr("Schliesse Transaktion"));monitor.addPercentComplete(1);
          monitor.setStatusText(i18n.tr("Geschäftsjahr abgeschlossen"));
          monitor.setPercentComplete(100);
          monitor.setStatus(ProgressMonitor.STATUS_DONE);
        }
        catch (Throwable t)
        {
          Logger.error("error while closing gj",t);
          monitor.log(i18n.tr("Rolle Transaktion zurück"));
          try
          {
            jahr.setClosed(false);
          }
          catch (RemoteException re)
          {
            Logger.error("FATAL, unable to reopen old gj",re);
          }
          try
          {
            jahr.transactionRollback();
          }
          catch (RemoteException re)
          {
            Logger.error("FATAL, unable to rollback transaction",re);
          }
          
          if (t instanceof ApplicationException)
            throw (ApplicationException) t;

          throw new ApplicationException(i18n.tr("Fehler beim Schliessen des Geschäftsjahres"),t);
        }
        finally
        {
          Settings.setActiveGeschaeftsjahr(jahr);
        }
      }

      /**
       * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
       */
      public void interrupt()
      {
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Der Vorgang kann nicht unterbrochen werden"), StatusBarMessage.TYPE_ERROR));
      }

      /**
       * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
       */
      public boolean isInterrupted()
      {
        return false;
      }
    });
  }
  
  /**
   * Erzeugt eine Abschreibungsbuchung fuer das Anlage-Gut zum angegebenen Geschaeftsjahr.
   * Hinweis: Die Funktion speichert die Buchung nicht in der Datenbank sondern erzeugt
   * nur das Objekt. Das Speichern ist Sache des Aufrufers.
   * Zusaetzlich zur AbschreibungsBuchung muss der Aufrufer durch Erzeugen eines
   * Abschreibungs-Objektes die Verknuepfung zum Anlagegut herstellen.
   * <code>
   *   Abschreibung afa = (Abschreibung) Settings.getDBService().createObject(Abschreibung.class,null);
   *   afa.setAnlagevermoegen(av);
   *   afa.setBuchung(buchung);
   *   afa.store();
   * </code>
   * @param monitor
   * @param av Anlage-Gut.
   * @param jahr Geschaeftsjahr.
   * @return Die erzeugte Abschreibungs-Buchung oder <code>null</code> wenn das Anlage-Gut
   * bereits abgeschrieben ist und keine Abschreibungs-Buchung noetig ist.
   * @throws RemoteException
   */
  private AbschreibungsBuchung schreibeAb(ProgressMonitor monitor, Anlagevermoegen av, Geschaeftsjahr jahr) throws RemoteException
  {
    double anschaffung = av.getAnschaffungskosten();
    double restwert    = av.getRestwert(jahr);
    int rnd            = GeschaeftsjahrUtil.getRestnutzungsdauer(av.getAnschaffungsdatum(),av.getNutzungsdauer(),jahr.getBeginn(),jahr.getEnde()); // in Monaten
    double betrag      = restwert / (rnd / 12d); // BUGZILLA 958
    boolean gwg        = false;

    if (restwert < 0.01d)
      return null; // bereits abgeschrieben

    String name    = i18n.tr("Abschreibung");
    Date datum     = av.getAnschaffungsdatum();
    Konto afaKonto = av.getAbschreibungskonto();

    monitor.log(i18n.tr("  Abschreibungsbuchung für " + av.getName()));monitor.addPercentComplete(1);

    
    // GWGs voll abschreiben
    // Das geschieht unter folgenden Bedingungen
    // 1) Nutzungsdauer ist 1 Jahr
    // und 2a)  Anschaffungskosten < GWG-Wert
    // oder 2b) Das Abschreibungskonto ist das in den Einstellungen hinterlegte GWG-Abschreibungskonto
    if (av.getNutzungsdauer() == 1)
    {
      Konto gwgKonto = Settings.getAbschreibungsKonto(jahr,true);
      
      gwg  = (gwgKonto != null && gwgKonto.getKontonummer().equals(afaKonto.getKontonummer()));
      gwg |=  anschaffung <= Settings.getGwgWert(jahr);

      if (gwg)
      {
        monitor.log(i18n.tr("    GWG: Schreibe voll ab"));
        name = i18n.tr("GWG-Abschreibung");
        betrag = anschaffung;
        if (betrag > restwert)
          betrag = restwert;
      }
    }

    // Anteilig abschreiben, wenn wir uns im Anschaffungsjahr befinden
    if (!gwg && jahr.check(datum))
    {
      monitor.log(i18n.tr("    Anschaffungsjahr: Schreibe anteilig ab"));monitor.addPercentComplete(1);
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(datum);

      monitor.log(i18n.tr("    Prüfe vereinfachte Abschreibungsregel"));monitor.addPercentComplete(1);
      int soll = Settings.getGeaenderteHalbjahresAbschreibung();
      int ist = cal.get(Calendar.YEAR);

      int months = 0;

      if (ist < soll)
      {
        monitor.log(i18n.tr("    Anlagegut wurde vor " + soll + " angeschafft. Es gilt die Halbjahresregel"));monitor.addPercentComplete(1);
        if (GeschaeftsjahrUtil.beforeMiddle(jahr.getBeginn(),jahr.getEnde(),datum))
          months = 12;
        else
          months = 6;
      }
      else
      {
        months = GeschaeftsjahrUtil.getMonths(datum,jahr.getEnde());
      }
      
      monitor.log(i18n.tr("    Berechne anteilige Abschreibung für " + months + " Monate"));monitor.addPercentComplete(1);
      name = i18n.tr("Anteilige Abschreibung für {0} Monate",""+months);
      betrag = new Math().round((betrag / 12d) * months); // Klammern nur der Optik wegen ;)
    }
    
    // Abzuschreibender Betrag >= Restwert -> Restwertbuchung
    if (!gwg && betrag >= restwert)
    {
      monitor.log(i18n.tr("    Restwertbuchung"));monitor.addPercentComplete(1);
      name = i18n.tr("Restwertbuchung");
      betrag = restwert;
    }
    
    // Wir setzen das Datum an den Anfang des letzten Tages damit immer noch
    // _vor_ dem Ende des Geschaeftsjahres liegt
    Date end = DateUtil.startOfDay(jahr.getEnde());

    AbschreibungsBuchung buchung = (AbschreibungsBuchung) Settings.getDBService().createObject(AbschreibungsBuchung.class,null);
    buchung.setDatum(end);
    buchung.setGeschaeftsjahr(jahr);
    buchung.setSollKonto(afaKonto);
    buchung.setHabenKonto(av.getKonto());
    
    // Forciert das Erzeugen der Belegnnummer
    // In vorherigen SynTAX-Versionen wurde die Belegnummer der Anschaffungsbuchung
    // fuer die Abschreibungsbuchung verwendet. Das sah im Anschaffungsjahr schoen
    // aus, weil die Abschreibung dann optisch der Anschaffung zugeordnet werden
    // konnte. In den Folgejahren passte das dann aber nicht mehr, weil dort die
    // Belegnummern ja wieder bei 1 anfangen
    buchung.setBelegnummer(buchung.getBelegnummer());
    buchung.setText(name + ": " + av.getName());
    buchung.setBetrag(betrag);
    return buchung; 
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BuchungsEngine#buche(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public HilfsBuchung[] buche(Buchung buchung) throws RemoteException, ApplicationException
  {
    double netto   = buchung.getBetrag();
    double brutto  = buchung.getBruttoBetrag();
    double sBetrag = brutto - netto;

    // Hilfsbuchungen loeschen
    if (!buchung.isNewObject())
    {
      DBIterator existing = buchung.getHilfsBuchungen();
      while (existing.hasNext())
      {
        ((HilfsBuchung)existing.next()).delete();
      }
    }

    Konto sKonto = buchung.getSollKonto();
    Konto hKonto = buchung.getHabenKonto();

    // checken, ob alle Daten da sind.
    if (sKonto == null || hKonto == null)
    {
      throw new ApplicationException(i18n.tr("Haben- oder Soll-Konto fehlt."));
    }
    
    Steuer steuer = buchung.getSteuerObject();
    if (steuer == null)
        return null;
    
    if (new Math().round(java.lang.Math.abs(sBetrag)) < 0.01d) // Achtung, kann negativ sein. Daher Math.abs
      return null; // keine Steuer zu buchen
    
    if (buchung.getDatum() == null)
      buchung.setDatum(new Date());

    // Hilfs-Buchung erstellen
    HilfsBuchung hb = (HilfsBuchung) Settings.getDBService().createObject(HilfsBuchung.class,null);
    hb.setBelegnummer(buchung.getBelegnummer());
    hb.setBetrag(sBetrag);                                        // Steuer-Betrag
    hb.setDatum(buchung.getDatum());                              // Datum
    hb.setSollKonto(sKonto.getKontoArt().getKontoArt() == Kontoart.KONTOART_GELD || sKonto.getKontoArt().getKontoArt() == Kontoart.KONTOART_PRIVAT?sKonto:steuer.getSteuerKonto());   // Das Steuer-Konto
    hb.setHabenKonto(hKonto.getKontoArt().getKontoArt() == Kontoart.KONTOART_GELD || hKonto.getKontoArt().getKontoArt() == Kontoart.KONTOART_PRIVAT?hKonto:steuer.getSteuerKonto());  // Haben-Konto
    hb.setGeschaeftsjahr(buchung.getGeschaeftsjahr());            // Geschaeftsjahr
    hb.setText(buchung.getText());                                // Text identisch mit Haupt-Buchung
     
    return new HilfsBuchung[]{hb};
  }
  
  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    if (Settings.isFirstStart())
    {
      Logger.info("first start: skipping engine start");
      return;
    }
    if (isStarted())
    {
      Logger.warn("engine already started, skipping request");
      return;
    }
    this.started = true;
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    return this.started;
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    return !isStarted();
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!isStarted())
    {
      Logger.warn("engine not started, skipping request");
      return;
    }
    this.started = false;
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return i18n.tr("Buchungs-Engine");
  }
}
