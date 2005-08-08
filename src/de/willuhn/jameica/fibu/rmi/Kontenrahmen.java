/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Kontenrahmen.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/08 22:54:16 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBObject;

/**
 * Bildet die verschiedenen Kontenrahmen in Fibu ab.
 * @author willuhn
 */
public interface Kontenrahmen extends DBObject
{
  /**
   * Liefert den Namen des Kontenrahmens.
   * @return
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
}


/*********************************************************************
 * $Log: Kontenrahmen.java,v $
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