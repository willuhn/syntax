/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/15 23:38:27 $
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
import java.sql.Connection;

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

  /**
   * Liefert die Connection.
   * TODO: Boeser Hack, ich weiss. ;)
   * @see de.willuhn.datasource.db.DBServiceImpl#getConnection()
   */
  public Connection getConnection()
  {
    return super.getConnection();
  }
}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/