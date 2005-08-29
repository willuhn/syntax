/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/BuchungsEngine.java,v $
 * $Revision: 1.12 $
 * $Date: 2005/08/29 17:46:14 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
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
      Buchung buchung  = null;
      Abschreibung afa = null;

      DBIterator list = m.getAnlagevermoegen();
      while (list.hasNext())
      {
        Anlagevermoegen av = (Anlagevermoegen) list.next();
        
        double betrag      = av.getAnschaffungskosten() / (double) av.getLaufzeit();
        double restwert    = av.getRestwert();
        boolean rest       = false;
        
        if (betrag > restwert)
        {
          betrag = restwert;
          rest = true;
        }
        
        buchung = (Buchung) db.createObject(Buchung.class,null);
        buchung.setDatum(jahr.getEnde());
        buchung.setGeschaeftsjahr(jahr);
        buchung.setSollKonto(jahr.getKontenrahmen().getAbschreibungskonto());
        buchung.setHabenKonto(av.getKonto());
        if (rest)
          buchung.setText(i18n.tr("Abschreibung {0}",av.getName()));
        else
          buchung.setText(i18n.tr("Restwertbuchung {0}",av.getName()));
        buchung.setBetrag(betrag);
        buchung.store();
        
        afa = (Abschreibung) db.createObject(Abschreibung.class,null);
        afa.setAnlagevermoegen(av);
        afa.setBuchung(buchung);
        afa.store();
      }
      
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