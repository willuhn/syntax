/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Kontenrahmen.java,v $
 * $Revision: 1.8 $
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

import de.willuhn.datasource.rmi.DBIterator;

/**
 * Bildet die verschiedenen Kontenrahmen in Fibu ab.
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
   * Liefert eine Liste aller Konten in diesem Kontenrahmen.
   * @return Konten.
   * @throws RemoteException
   */
  public DBIterator getKonten() throws RemoteException;
}


/*********************************************************************
 * $Log: Kontenrahmen.java,v $
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