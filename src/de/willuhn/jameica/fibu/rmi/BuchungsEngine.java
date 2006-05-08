/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/BuchungsEngine.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/05/08 22:44:18 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.Service;
import de.willuhn.util.ApplicationException;

/**
 * Diese Klasse uebernimmt alle Buchungen.
 * Sie nimmt keine Aenderungen an der Datenbank vor sondern praepariert lediglich
 * die Buchungs-Objekte. Das Schreiben in die Datenbank muss der Aufrufer selbst.
 * @author willuhn
 */
public interface BuchungsEngine extends Service
{
  /**
   * Schliesst das Geschaeftsjahr ab.
   * @param jahr das zu schliessende Geschaeftsjahr.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public void close(Geschaeftsjahr jahr) throws RemoteException, ApplicationException;

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
  public HilfsBuchung[] buche(Buchung buchung) throws RemoteException, ApplicationException;
}

/*********************************************************************
 * $Log: BuchungsEngine.java,v $
 * Revision 1.1  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 **********************************************************************/