/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungsEngineImpl.java,v $
 * $Revision: 1.9 $
 * $Date: 2007/03/06 15:22:36 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.util.CustomDateFormat;
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
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung der Buchungsengine.
 */
public class BuchungsEngineImpl extends UnicastRemoteObject implements BuchungsEngine
{

  private I18N i18n = null;
  private boolean started = false;

  /**
   * ct.
   * @throws RemoteException
   */
  public BuchungsEngineImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
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
          monitor.setStatusText(i18n.tr("Schliesse Geschäftsjahr " + jahr.getAttribute(jahr.getPrimaryAttribute())));

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

          monitor.log(i18n.tr("Buche Abschreibungen für Anlagevermögen"));monitor.addPercentComplete(1);
          DBIterator list = m.getAnlagevermoegen();
          while (list.hasNext())
          {
            Anlagevermoegen av = (Anlagevermoegen) list.next();

            AbschreibungsBuchung buchung = schreibeAb(monitor,av,jahr);

            if (buchung == null)
            {
              monitor.log(i18n.tr("  Überspringe " + av.getName() + " - bereits abgeschrieben"));
              continue;
            }
            buchung.store();
            
            monitor.log(i18n.tr("  Abschreibung für " + av.getName()));monitor.addPercentComplete(1);
            Abschreibung afa = (Abschreibung) db.createObject(Abschreibung.class,null);
            afa.setAnlagevermoegen(av);
            afa.setBuchung(buchung);
            afa.store();
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

          monitor.log(i18n.tr("  Beginn: " + jahrNeu.getBeginn().toString()));
          monitor.log(i18n.tr("  Ende  : " + jahrNeu.getEnde().toString()));

          // Checken, ob sich in diesem Zeitraum schon ein Geschaeftsjahr befindet
          monitor.log(i18n.tr("  Prüfe, ob neues Geschäftsjahr bereits existiert"));monitor.addPercentComplete(1);
          DBIterator check = m.getGeschaeftsjahre();
          check.addFilter(jahrNeu.getBeginn().getTime() + " < " + db.getSQLTimestamp("ende"));
          check.addFilter(jahrNeu.getEnde().getTime() + " > " + db.getSQLTimestamp("beginn"));
          if (check.hasNext())
            throw new ApplicationException(i18n.tr("Es existiert bereits ein Geschäftsjahr, welches sich mit dem Zeitraum {0}-{1} überschneidet", new String[]{jahrNeu.getBeginn().toString(),jahrNeu.getEnde().toString()}));

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
            Kontoart ka = k.getKontoArt();
            if (! (ka.getKontoArt() == Kontoart.KONTOART_ANLAGE || ka.getKontoArt() == Kontoart.KONTOART_GELD))
            {
              monitor.log(i18n.tr("Überspringe Konto " + k.getKontonummer()));
              continue;
            }
              
            double saldo = k.getSaldo(jahr);
            
            // TODO: Falls hier 0.000000123 rauskommt, wuerde das fehlschlagen
            if (saldo == 0.0)
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
    double betrag      = anschaffung / (double) av.getNutzungsdauer();
    double restwert    = av.getRestwert(jahr);
    boolean gwg        = false;

    if (restwert == 0.0d)
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
      Konto gwgKonto = Settings.getAbschreibunsgKonto(jahr,true);
      
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
        if (cal.get(Calendar.MONTH) < Calendar.JULY)
          months = 12;
        else
          months = 6;
      }
      else
      {
        months = 12 - cal.get(Calendar.MONTH); // Anschaffungsmonat wird mit abgeschrieben
      }
      
      monitor.log(i18n.tr("    Berechne anteilige Abschreibung für " + months + " Monate"));monitor.addPercentComplete(1);
      name = i18n.tr("Anteilige Abschreibung für {0} Monate",""+months);
      betrag = (betrag / 12) * months; // Klammern nur der Optik wegen ;)
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
    Date end = CustomDateFormat.startOfDay(jahr.getEnde());

    AbschreibungsBuchung buchung = (AbschreibungsBuchung) Settings.getDBService().createObject(AbschreibungsBuchung.class,null);
    buchung.setDatum(end);
    buchung.setGeschaeftsjahr(jahr);
    buchung.setSollKonto(afaKonto);
    buchung.setHabenKonto(av.getKonto());
    Buchung b = av.getBuchung();
    if (b != null)
    {
      // Wenn die Buchung zur Erzeugung des Anlagegutes noch existiert, uebernehmen wir die Belegnummer.
      buchung.setBelegnummer(b.getBelegnummer());
    }
    else
    {
      // TODO: Die erzeugten Belegnummern sind Unfug
      buchung.setBelegnummer(buchung.getBelegnummer());
    }
    buchung.setText(name + ": " + av.getName());
    buchung.setBetrag(betrag);
    return buchung; 
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BuchungsEngine#buche(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public HilfsBuchung[] buche(Buchung buchung) throws RemoteException, ApplicationException
  {
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

    // Der zu verwendende Steuersatz
    Steuer sSteuer = sKonto.getSteuer();
    Steuer hSteuer = hKonto.getSteuer();

    if (sSteuer == null && hSteuer == null)
      return null; // Keine Steuerkonten vorhanden
    
    Math math = new Math();

    double steuer  = buchung.getSteuer();
    
    if (steuer == 0.0d)
      return null; // keine Steuer zu buchen
    
    double netto   = buchung.getBetrag();
    double brutto  = math.brutto(netto,steuer);
    double sBetrag = math.steuer(brutto,steuer);
    
    if (brutto == netto || sBetrag == 0.0d)
      return null; // keine Steuer zu buchen
    
    if (buchung.getDatum() == null)
      buchung.setDatum(new Date());
    

    // Hilfs-Buchung erstellen
    HilfsBuchung hb = (HilfsBuchung) Settings.getDBService().createObject(HilfsBuchung.class,null);
    hb.setBelegnummer(buchung.getBelegnummer());
    hb.setBetrag(sBetrag);                                        // Steuer-Betrag
    hb.setDatum(buchung.getDatum());                              // Datum
    hb.setSollKonto(sSteuer != null ? sSteuer.getSteuerKonto() : sKonto);   // Das Steuer-Konto
    hb.setHabenKonto(hSteuer != null ? hSteuer.getSteuerKonto() : hKonto);  // Haben-Konto
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
      Logger.warn("engine allready started, skipping request");
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

/*********************************************************************
 * $Log: BuchungsEngineImpl.java,v $
 * Revision 1.9  2007/03/06 15:22:36  willuhn
 * @C Anlagevermoegen in Auswertungen ignorieren, wenn Anfangsbestand bereits 0
 * @B Formatierungsfehler bei Betraegen ("-0,00")
 * @C Afa-Buchungen werden nun auch als GWG gebucht, wenn Betrag zwar groesser als GWG-Grenze aber Afa-Konto=GWG-Afa-Konto (laut Einstellungen)
 *
 * Revision 1.8  2007/02/27 18:53:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2007/02/27 18:06:20  willuhn
 * @N Ueberspringe alle Konten ausser Geld und Anlage beim Erstellen der Anfangsbestaende
 *
 * Revision 1.6  2007/02/27 15:46:17  willuhn
 * @N Anzeige des vorherigen Kontostandes im Kontoauszug
 *
 * Revision 1.5  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 * Revision 1.4  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.2  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.1  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.40  2006/01/09 01:40:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.39  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.38  2006/01/03 23:58:36  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.37  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.36  2006/01/03 13:48:14  willuhn
 * @N Halbjahresregel bei Abschreibungen
 *
 * Revision 1.35  2006/01/03 11:29:03  willuhn
 * @N Erzeugen der Abschreibungs-Buchung in eine separate Funktion ausgelagert
 *
 * Revision 1.34  2005/10/14 17:24:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.33  2005/10/13 21:29:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.32  2005/10/13 21:10:59  willuhn
 * @B bug 137
 *
 * Revision 1.31  2005/10/13 16:00:35  willuhn
 * *** empty log message ***
 *
 * Revision 1.30  2005/10/13 15:53:15  willuhn
 * @B bug 138
 *
 * Revision 1.29  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.28  2005/10/06 14:48:40  willuhn
 * @N Sonderregelung fuer Abschreibunsgbuchungen
 *
 * Revision 1.27  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.26  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.25  2005/09/25 22:18:23  willuhn
 * @B bug 122
 *
 * Revision 1.24  2005/09/05 15:00:43  willuhn
 * *** empty log message ***
 *
 * Revision 1.23  2005/09/05 14:32:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2005/09/05 14:19:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2005/09/05 13:47:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.20  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.19  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2005/09/02 13:27:35  willuhn
 * @C transaction behavior
 *
 * Revision 1.17  2005/09/01 23:28:15  willuhn
 * *** empty log message ***
 *
 * Revision 1.16  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.15  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.14  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.13  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.11  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.10  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.8  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.7  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.3  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.2  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/