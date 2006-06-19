/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMcKoiImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/06/19 16:25:42 $
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
import java.io.FileReader;
import java.rmi.RemoteException;
import java.sql.Connection;

import de.willuhn.datasource.db.EmbeddedDatabase;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.ScriptExecutor;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

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
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#create(de.willuhn.util.ProgressMonitor)
   */
  public void create(ProgressMonitor monitor) throws RemoteException, ApplicationException
  {
    String username = getUsername();

    // Wir checken nur den Usernamen. Das Passwort darf leer sein.
    if (username == null || username.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Benutzernamen an"));

    PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();

    File create = new File(res.getPath() + File.separator + "sql" + File.separator + "create.sql");
    File init   = new File(res.getPath() + File.separator + "sql" + File.separator + "init.sql");
    
    Connection conn = null;
    try
    {
      File dbDir = new File(res.getWorkPath(),"db");
      if (!dbDir.exists())
        dbDir.mkdirs();
      
      EmbeddedDatabase db = new EmbeddedDatabase(dbDir.getAbsolutePath(),username,getPassword());
      if (db.exists())
      {
        Logger.warn("database allready exists, asking user to skip this step");
        String text = i18n.tr("Datenbank existiert bereits.\nMöchten Sie die Erstellung überspringen?");
        if (Application.getCallback().askUser(text))
        {
          Logger.info("creation of database skipped");
          monitor.setStatusText(i18n.tr("Erstellung der Datenbank übersprungen"));
          return;
        }
      }

      conn = db.getConnection();
      ScriptExecutor.execute(new FileReader(create),conn, monitor);
      monitor.setPercentComplete(0);
      ScriptExecutor.execute(new FileReader(init),conn, monitor);
      monitor.setStatusText(i18n.tr("Datenbank erfolgreich eingerichtet"));
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Throwable t)
    {
      Logger.error("unable to execute sql scripts",t);
      throw new ApplicationException(i18n.tr("Fehler beim Initialisieren der Datenbank. {0}",t.getLocalizedMessage()),t);
    }
    finally
    {
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (Throwable t)
        {
          Logger.error("unable to close connection",t);
        }
      }
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getName()
   */
  public String getName() throws RemoteException
  {
    return i18n.tr("Embedded Datenbank (McKoi)");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsPassword()
   */
  public boolean needsPassword() throws RemoteException
  {
    return true;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsUsername()
   */
  public boolean needsUsername() throws RemoteException
  {
    return true;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getJdbcUrl()
   */
  public String getJdbcUrl() throws RemoteException
  {
    PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
    File dbDir = new File(res.getWorkPath(),"db");
    if (!dbDir.exists())
      dbDir.mkdirs();
    return ":jdbc:mckoi:local://" + dbDir.getAbsolutePath() + "/db.conf";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getJdbcDriver()
   */
  public String getJdbcDriver() throws RemoteException
  {
    return "com.mckoi.JDBCDriver";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getSQLTimestamp(java.lang.String)
   */
  public String getSQLTimestamp(String content) throws RemoteException
  {
    return "tonumber({0})".replaceAll("\\{0\\}",content);
  }
}


/*********************************************************************
 * $Log: DBSupportMcKoiImpl.java,v $
 * Revision 1.3  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/