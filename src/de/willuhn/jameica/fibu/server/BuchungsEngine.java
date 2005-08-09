/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/BuchungsEngine.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/09 23:53:34 $
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

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;

/**
 * Diese Klasse uebernimmt alle Buchungen.
 * ACHTUNG: Sie nimmt keine Aenderungen an der Datenbank vor
 * sondern praepariert lediglich die Buchungs-Objekte. Das Schreiben
 * in die Datenbank muss der Aufrufer selbst.
 * @author willuhn
 */
public class BuchungsEngine
{

  /**
   * Bucht die uebergebene Buchung.
   * Die Funktion erkennt selbstaendig, ob weitere Hilfs-Buchungen noetig sind
   * und liefert diese ungespeichert als Array zurueck.
   * @param buchung die zu buchende Buchung.
   * @return Liste der noch zu speichernden Hilfsbuchungen oder null wenn keine Hilfsbuchungen noetig sind.
   * @throws RemoteException
   */
  public static HilfsBuchung[] buche(Buchung buchung) throws RemoteException
  {
    if (buchung instanceof HilfsBuchung)
      return null; // Das ist schon 'ne Hilfe-Buchung.
    
    // Wir checken ob die Buchung neu ist. Nur neue Buchungen duerfen gebucht
    // werden. Alle anderen muessen storniert werden.
    if (!buchung.isNewObject())
      throw new RemoteException("this is not a new buchung.");

    Konto konto   = buchung.getKonto();
    GeldKonto gk  = buchung.getGeldKonto();

    // checken, ob alle Daten da sind.
    if (konto == null || gk == null)
    {
      throw new RemoteException("Konto or Geldkonto missing.");
    }

    // Der zu verwendende Steuersatz
    Steuer s = konto.getSteuer();
    
    if (s == null)
    {
      // kein Steuer-Konto. Also muessen wir auch nichts netto buchen
      // und brauchen keine Hilfs-Buchungen
      return null;
    }

    double steuer  = buchung.getSteuer();
    double brutto  = buchung.getBetrag();
    double netto   = Math.netto(brutto,steuer);
    double sBetrag = Math.steuer(brutto,steuer);

    buchung.setBetrag(netto); // wir buchen nur den Netto-Betrag

    // Hilfs-Buchung erstellen
    HilfsBuchung hb = (HilfsBuchung) Settings.getDBService().createObject(HilfsBuchung.class,null);
    hb.setBelegnummer(buchung.getBelegnummer());
    hb.setBetrag(sBetrag); // Steuer-Betrag
    hb.setDatum(buchung.getDatum());        // Datum
    hb.setGeldKonto(gk);                    // Geld-Konto
    hb.setMandant(buchung.getMandant());    // Mandant
    hb.setText(buchung.getText());          // Text identisch mit Haupt-Buchung
    hb.setKonto(s.getSteuerKonto());        // Das Steuer-Konto
     
    return new HilfsBuchung[]{hb};
  }

}

/*********************************************************************
 * $Log: BuchungsEngine.java,v $
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