/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/DBService.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/10/18 23:28:55 $
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

/**
 * Datenbank-Service des Fibu-Plugins.
 */
public interface DBService extends de.willuhn.datasource.rmi.DBService
{
  /**
   * Teilt dem Server mit, welche Geschaeftsjahr der Client gerade bearbeitet.
   * @param jahr das Geschaeftsjahr des Clients.
   * @throws RemoteException
   */
  public void setActiveGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException;
  
  /**
   * Liefert das Geschaeftsjahr, welches der Client gerade bearbeitet.
   * @return Geschaeftsjahr.
   * @throws RemoteException
   */
  public Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException;
}


/*********************************************************************
 * $Log: DBService.java,v $
 * Revision 1.2  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/