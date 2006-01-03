/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/BuchungsEngine.java,v $
 * $Revision: 1.37 $
 * $Date: 2006/01/03 17:55:53 $
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
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Diese Klasse uebernimmt alle Buchungen.
 * ACHTUNG: Sie nimmt keine Aenderungen an der Datenbank vor
 * sondern praepariert lediglich die Buchungs-Objekte. Das Schreiben
 * in die Datenbank muss der Aufrufer selbst.
 * @author willuhn
 */
public class BuchungsEngine
{
  private static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Schliesst das Geschaeftsjahr ab.
   * @param jahr das zu schliessende Geschaeftsjahr.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static void close(Geschaeftsjahr jahr) throws RemoteException, ApplicationException
  {
    Logger.info("closing geschaeftsjahr " + jahr.getAttribute(jahr.getPrimaryAttribute()));

    if (jahr.isClosed())
    {
      Logger.warn("geschaeftsjahr allready closed");
      throw new ApplicationException(i18n.tr("Geschäftsjahr wurde bereits abgeschlossen"));
    }
    
    try
    {
      jahr.transactionBegin();
      
      Mandant m        = jahr.getMandant();
      DBService db     = Settings.getDBService();

      Logger.info("Buche Abschreibungen für Anlagevermögen");
      DBIterator list = m.getAnlagevermoegen();
      while (list.hasNext())
      {
        Anlagevermoegen av = (Anlagevermoegen) list.next();

        AbschreibungsBuchung buchung = schreibeAb(av,jahr);

        buchung.store();
        
        Logger.info("  Abschreibung fuer " + av.getName());
        Abschreibung afa = (Abschreibung) db.createObject(Abschreibung.class,null);
        afa.setAnlagevermoegen(av);
        afa.setBuchung(buchung);
        afa.store();
      }
      
      // Neues geschaeftsjahr erzeugen
      Logger.info("Erzeuge neues Geschaeftsjahr");
      Geschaeftsjahr jahrNeu = (Geschaeftsjahr) db.createObject(Geschaeftsjahr.class,null);
      
      jahrNeu.setMandant(m);
      jahrNeu.setKontenrahmen(jahr.getKontenrahmen());
      
      Logger.info("  Berechne Dauer des Geschaeftsjahres");
      
      Calendar cal = Calendar.getInstance();

      // Beginn
      cal.setTime(jahr.getEnde());
      cal.add(Calendar.DATE,1); // Ein Tag drauf.
      jahrNeu.setBeginn(cal.getTime());
      
      // Ende
      cal.add(Calendar.MONTH,jahr.getMonate());
      cal.add(Calendar.DATE,-1); // Ein Tag wieder abziehen
      jahrNeu.setEnde(cal.getTime());

      Logger.info("  Beginn: " + jahrNeu.getBeginn().toString());
      Logger.info("  Ende  : " + jahrNeu.getEnde().toString());

      // Checken, ob sich in diesem Zeitraum schon ein Geschaeftsjahr befindet
      Logger.info("  Pruefe, ob neues Geschaeftsjahr bereits existiert");
      DBIterator check = db.createList(Geschaeftsjahr.class);
      check.addFilter(jahrNeu.getBeginn().getTime() + " < TONUMBER(ende)");
      check.addFilter(jahrNeu.getEnde().getTime() + " > TONUMBER(beginn)");
      check.addFilter("mandant_id = " + m.getID());
      if (check.hasNext())
        throw new ApplicationException(i18n.tr("Es existiert bereits ein Geschäftsjahr, welches sich mit dem Zeitraum {0}-{1} überschneidet", new String[]{jahrNeu.getBeginn().toString(),jahrNeu.getEnde().toString()}));

      jahrNeu.setVorjahr(jahr);
      jahrNeu.store();
      
      // Anfangsbestaende erzeugen
      // Existierende Anfangsbestaende aus dem Vorjahr brauchen wir nicht
      // beruecksichtigen, weil sie in k.getSaldo bereits enthalten sind.
      Logger.info("Erzeuge neue Anfangsbestaende");
      list = jahr.getKontenrahmen().getKonten();
      while (list.hasNext())
      {
        // Wir wollen den Saldo des alten Jahres
        Konto k = (Konto) list.next();
        Kontoart ka = k.getKontoArt();
        if (ka.getKontoArt() == Kontoart.KONTOART_PRIVAT)
        {
          Logger.debug("Überspringe Konto " + k.getKontonummer() + " da Privat-Konto");
          continue;
        }
          
        double saldo = k.getSaldo(jahr);
        if (saldo == 0.0)
          continue;
        
        // Erzeugen aber einen Anfangsbestand fuers neue Jahr
        Anfangsbestand ab = (Anfangsbestand) db.createObject(Anfangsbestand.class,null);
        ab.setBetrag(saldo);
        ab.setKonto(k);
        ab.setGeschaeftsjahr(jahrNeu);
        ab.store();
        Logger.info("  [" + k.getKontonummer() + "] " + k.getName() + ": " + saldo);
      }
      
      Logger.info("Schliesse altes Geschaeftsjahr");
      jahr.setClosed(true);
      jahr.store();

      jahr.transactionCommit();
    }
    catch (RemoteException e)
    {
      jahr.transactionRollback();
      throw e;
    }
    catch (ApplicationException ae)
    {
      jahr.transactionRollback();
      throw ae;
    }
    catch (Throwable t)
    {
      jahr.transactionRollback();
      Logger.error("error while closing gj");
      throw new RemoteException(i18n.tr("Fehler beim Schliessen des Geschäftsjahres"));
    }
    finally
    {
      Settings.setActiveGeschaeftsjahr(jahr);
    }
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
   * @param av Anlage-Gut.
   * @param jahr Geschaeftsjahr.
   * @return Die erzeugte Abschreibungs-Buchung oder <code>null</code> wenn das Anlage-Gut
   * bereits abgeschrieben ist und keine Abschreibungs-Buchung noetig ist.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static AbschreibungsBuchung schreibeAb(Anlagevermoegen av, Geschaeftsjahr jahr) throws RemoteException, ApplicationException
  {
    double anschaffung = av.getAnschaffungskosten();
    double betrag      = anschaffung / (double) av.getNutzungsdauer();
    double restwert    = av.getRestwert(jahr);
    boolean gwg        = false;

    if (restwert == 0.0d)
      return null; // bereits abgeschrieben

    String name = i18n.tr("Abschreibung");
    Date datum = av.getAnschaffungsdatum();

    Logger.info("  Abschreibungsbuchung fuer " + av.getName());

    
    // GWGs voll abschreiben
    if (anschaffung <= Settings.getGwgWert(jahr))
    {
      Logger.info("    GWG: Schreibe voll ab");
      name = i18n.tr("GWG-Abschreibung");
      betrag = anschaffung;
      if (betrag > restwert)
        betrag = restwert;
      gwg = true;
    }

    // Anteilig abschreiben, wenn wir uns im Anschaffungsjahr befinden
    if (!gwg && jahr.check(datum))
    {
      Logger.info("    Anschaffungsjahr: Schreibe anteilig ab");
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(datum);

      Logger.info("    Pruefe vereinfachte Abschreibungsregel");
      int soll = Settings.getGeaenderteHalbjahresAbschreibung();
      int ist = cal.get(Calendar.YEAR);

      int months = 0;

      if (ist < soll)
      {
        Logger.info("    Anlagegut wurde vor " + soll + " angeschafft. Es gilt die Halbjahresregel");
        if (cal.get(Calendar.MONTH) < Calendar.JULY)
          months = 12;
        else
          months = 6;
      }
      else
      {
        months = 12 - cal.get(Calendar.MONTH); // Anschaffungsmonat wird mit abgeschrieben
      }
      
      Logger.info("    Berechne anteilige Abschreibung fuer " + months + " Monate");
      name = i18n.tr("Anteilige Abschreibung für {0} Monate",""+months);
      betrag = (betrag / 12) * months; // Klammern nur der Optik wegen ;)
    }
    
    // Abzuschreibender Betrag >= Restwert -> Restwertbuchung
    if (!gwg && betrag >= restwert)
    {
      Logger.info("    Restwertbuchung");
      name = i18n.tr("Restwertbuchung");
      betrag = restwert;
    }
    
    // Wir setzen das Datum an den Anfang des letzten Tages damit immer noch
    // _vor_ dem Ende des Geschaeftsjahres liegt
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(jahr.getEnde());
    cal1.set(Calendar.HOUR,0);
    cal1.set(Calendar.MINUTE,0);
    cal1.set(Calendar.SECOND,1);
    Date end = cal1.getTime();

    AbschreibungsBuchung buchung = (AbschreibungsBuchung) Settings.getDBService().createObject(AbschreibungsBuchung.class,null);
    buchung.setDatum(end);
    buchung.setGeschaeftsjahr(jahr);
    buchung.setSollKonto(av.getAbschreibungskonto());
    buchung.setHabenKonto(av.getKonto());
    buchung.setBelegnummer(buchung.getBelegnummer());
    buchung.setText(name + ": " + av.getName());
    buchung.setBetrag(betrag);
    return buchung; 
  }
  
  /**
   * Bucht die uebergebene Buchung.
   * Die Funktion erkennt selbstaendig, ob weitere Hilfs-Buchungen noetig sind
   * und liefert diese ungespeichert als Array zurueck.
   * Hinweis: Die Funktion speichert die Hilfsbuchungen nicht in der Datenbank sondern erzeugt
   * nur die Objekte. Das Speichern ist Sache des Aufrufers.
   * @param buchung die zu buchende Buchung.
   * @return Liste der noch zu speichernden Hilfsbuchungen oder null wenn keine Hilfsbuchungen noetig sind.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static HilfsBuchung[] buche(Buchung buchung) throws RemoteException, ApplicationException
  {
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
    Steuer s = sKonto.getSteuer();
    boolean steuerSoll = true;

    // wir nehmen das Steuerkonto vom Haben-Konto
    if (s == null)
    {
      s = hKonto.getSteuer();
      steuerSoll = false;
    }
    
    if (s == null)
    {
      // kein Steuer-Konto. Also muessen wir auch nichts netto buchen
      // und brauchen keine Hilfs-Buchungen
      return null;
    }

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
    hb.setSollKonto(steuerSoll ? s.getSteuerKonto() : hKonto);    // Das Steuer-Konto
    hb.setHabenKonto(steuerSoll ? hKonto : sKonto);               // Haben-Konto
    hb.setGeschaeftsjahr(buchung.getGeschaeftsjahr());            // Geschaeftsjahr
    hb.setText(buchung.getText());                                // Text identisch mit Haupt-Buchung
     
    return new HilfsBuchung[]{hb};
  }

}

/*********************************************************************
 * $Log: BuchungsEngine.java,v $
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