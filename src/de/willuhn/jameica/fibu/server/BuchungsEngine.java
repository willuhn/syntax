/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/BuchungsEngine.java,v $
 * $Revision: 1.17 $
 * $Date: 2005/09/01 23:28:15 $
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
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
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
      return;
    }
    
    try
    {
      jahr.transactionBegin();
      
      Mandant m        = jahr.getMandant();
      DBService db     = Settings.getDBService();

      // Abschreibungen buchen
      Calendar cal1 = Calendar.getInstance();
      
      // Wir setzen das Datum an den Anfang des letzten Tages damit immer noch _vor_ dem Ende gdes Geschaeftsjahres liegt
      cal1.setTime(jahr.getEnde());
      cal1.set(Calendar.HOUR,0);
      cal1.set(Calendar.MINUTE,0);
      cal1.set(Calendar.SECOND,1);
      Date end = cal1.getTime();

      Logger.info("Buche Abschreibungen für Anlagevermögen");
      DBIterator list = m.getAnlagevermoegen();
      while (list.hasNext())
      {
        Anlagevermoegen av = (Anlagevermoegen) list.next();
        
        double betrag      = av.getAnschaffungskosten() / (double) av.getNutzungsdauer();
        double restwert    = av.getRestwert();
        boolean rest       = false;

        if (restwert == 0.0d)
          continue;
        
        if (betrag > restwert)
        {
          betrag = restwert;
          rest = true;
        }
        
        Logger.info("  Abschreibung fuer " + av.getName());
        Buchung buchung = (Buchung) db.createObject(Buchung.class,null);
        buchung.setDatum(end);
        buchung.setGeschaeftsjahr(jahr);
        buchung.setSollKonto(av.getAbschreibungskonto());
        buchung.setHabenKonto(av.getKonto());
        buchung.setBelegnummer(buchung.getBelegnummer());
        if (rest)
          buchung.setText(i18n.tr("Restwertbuchung {0}",av.getName()));
        else
          buchung.setText(i18n.tr("Abschreibung {0}",av.getName()));
        buchung.setBetrag(betrag);
        buchung.store();
        
        Logger.info("  Abschreibung fuer " + av.getName());
        Abschreibung afa = (Abschreibung) db.createObject(Abschreibung.class,null);
        afa.setAnlagevermoegen(av);
        afa.setBuchung(buchung);
        afa.setGeschaeftsjahr(jahr);
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

      jahrNeu.setVorjahr(jahr);
      jahrNeu.store();
      Settings.setActiveGeschaeftsjahr(jahrNeu);
      
      // Anfangsbestaende erzeugen
      // Existierende Anfangsbestaende aus dem Vorjahr brauchen wir nicht
      // beruecksichtigen, weil sie in k.getSaldo bereits enthalten sind.
      Logger.info("Erzeuge neue Anfangsbestaende");
      list = jahr.getKontenrahmen().getKonten();
      while (list.hasNext())
      {
        Konto k = (Konto) list.next();
        double saldo = k.getSaldo();
        if (saldo == 0.0)
          continue;
        
        Anfangsbestand ab = (Anfangsbestand) db.createObject(Anfangsbestand.class,null);
        ab.setBetrag(saldo);
        ab.setKonto(k);
        ab.setGeschaeftsjahr(jahrNeu);
        ab.store();
      }
      
      Logger.info("Schliesse altes Geschaeftsjahr");
      jahr.close();
      jahr.store();

      jahr.transactionCommit();
    }
    catch (RemoteException e)
    {
      jahr.transactionRollback();
      Settings.setActiveGeschaeftsjahr(jahr);
      throw e;
    }
    catch (ApplicationException ae)
    {
      jahr.transactionRollback();
      Settings.setActiveGeschaeftsjahr(jahr);
      throw ae;
    }
    
    
  }
  
  /**
   * Bucht die uebergebene Buchung.
   * Die Funktion erkennt selbstaendig, ob weitere Hilfs-Buchungen noetig sind
   * und liefert diese ungespeichert als Array zurueck.
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
      throw new RemoteException("Haben- oder Soll-Konto fehlt.");
    }

    // Der zu verwendende Steuersatz
    Steuer s = sKonto.getSteuer();
    
    if (s == null)
    {
      // kein Steuer-Konto. Also muessen wir auch nichts netto buchen
      // und brauchen keine Hilfs-Buchungen
      return null;
    }

    Math math = new Math();

    double steuer  = buchung.getSteuer();
    double brutto  = buchung.getBetrag();
    double netto   = math.netto(brutto,steuer);
    double sBetrag = math.steuer(brutto,steuer);
    
    if (steuer == 0.0 || brutto == netto)
      return null; // keine Steuer zu buchen

    buchung.setBetrag(netto); // wir buchen nur den Netto-Betrag

    // Hilfs-Buchung erstellen
    HilfsBuchung hb = (HilfsBuchung) Settings.getDBService().createObject(HilfsBuchung.class,null);
    hb.setBelegnummer(buchung.getBelegnummer());
    hb.setBetrag(sBetrag);                              // Steuer-Betrag
    hb.setDatum(buchung.getDatum());                    // Datum
    hb.setHabenKonto(hKonto);                           // Haben-Konto
    hb.setGeschaeftsjahr(buchung.getGeschaeftsjahr());  // Geschaeftsjahr
    hb.setText(buchung.getText());                      // Text identisch mit Haupt-Buchung
    hb.setSollKonto(s.getSteuerKonto());                // Das Steuer-Konto
     
    return new HilfsBuchung[]{hb};
  }

}

/*********************************************************************
 * $Log: BuchungsEngine.java,v $
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