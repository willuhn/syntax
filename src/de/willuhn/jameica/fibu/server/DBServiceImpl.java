/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/08 21:35:46 $
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

import de.willuhn.datasource.db.EmbeddedDBServiceImpl;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.system.Application;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends EmbeddedDBServiceImpl implements DBService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl()
      throws RemoteException
  {
    super(Application.getPluginLoader().getPlugin(Fibu.class).getResources().getWorkPath() + "/db/db.conf",
        "fibu","fibu");
  this.setClassFinder(Application.getClassLoader().getClassFinder());
  }

}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/