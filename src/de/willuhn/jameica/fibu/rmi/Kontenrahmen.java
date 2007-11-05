/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Kontenrahmen.java,v $
 * $Revision: 1.10 $
 * $Date: 2007/11/05 01:04:49 $
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

import de.willuhn.datasource.rmi.DBIterator;

/**
 * Bildet die verschiedenen Kontenrahmen ab.
 * @author willuhn
 */
public interface Kontenrahmen extends UserObject
{
  /**
   * Liefert den Namen des Kontenrahmens.
   * @return Liefert den Namen des Kontenrahmens.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
  
  /**
   * Speichert den Namen des Kontenrahmen.
   * @param name Name des Kontenrahmen.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;
  
  /**
   * Liefert eine Liste aller Konten in diesem Kontenrahmen.
   * @return Konten.
   * @throws RemoteException
   */
  public DBIterator getKonten() throws RemoteException;
  
  /**
   * Sucht ein Konto anhand der Kontonummer.
   * @param kto Kontonummer.
   * @return das gefundene Konto oder <code>null</code>.
   * @throws RemoteException
   */
  public Konto findByKontonummer(String kto) throws RemoteException;
}


/*********************************************************************
 * $Log: Kontenrahmen.java,v $
 * Revision 1.10  2007/11/05 01:04:49  willuhn
 * @N Beim Speichern testen, ob fuer den Mandanten schon ein gleichnamiger Kontenrahmen existiert
 * @N findByKontonummer
 *
 * Revision 1.9  2007/10/08 22:54:47  willuhn
 * @N Kopieren eines kompletten Kontenrahmen auf einen Mandanten
 *
 * Revision 1.8  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.7  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.6  2005/09/01 21:08:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.4  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.3  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/