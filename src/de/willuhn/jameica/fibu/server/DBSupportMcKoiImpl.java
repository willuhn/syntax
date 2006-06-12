/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMcKoiImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 23:05:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Connection;

import de.willuhn.datasource.db.EmbeddedDatabase;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des McKoi-DB-Supports.
 */
public class DBSupportMcKoiImpl extends AbstractDBSupportImpl implements
    DBSupport
{

  /**
   * @throws RemoteException
   */
  public DBSupportMcKoiImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getConnection()
   */
  Connection getConnection() throws ApplicationException
  {
    try
    {
      PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
      
      String username = "syntax";
      String password = "syntax";
      File dbDir = new File(res.getWorkPath(),"db");
      if (!dbDir.exists())
        dbDir.mkdirs();
      
      EmbeddedDatabase db = new EmbeddedDatabase(dbDir.getAbsolutePath(),username,password);
      if (db.exists())
        throw new ApplicationException(i18n.tr("Datenbank existiert bereits"));
      return db.getConnection();
    }
    catch (Throwable t)
    {
      Logger.error("unable to connect to database",t);

      Throwable tOrig = t.getCause();
      
      String msg = t.getLocalizedMessage();
      if (tOrig != null & tOrig != t)
        msg += ". " + tOrig.getLocalizedMessage();
      throw new ApplicationException(msg);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getCreateScript()
   */
  String getCreateScript()
  {
    return "create.sql";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getName()
   */
  public String getName() throws RemoteException
  {
    return i18n.tr("Embedded Datenbank (McKoi)");
  }

}


/*********************************************************************
 * $Log: DBSupportMcKoiImpl.java,v $
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/