/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMySqlImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/06/13 22:52:10 $
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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.ScriptExecutor;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung des MySQL-Supports.
 */
public class DBSupportMySqlImpl extends AbstractDBSupportImpl implements
    DBSupport
{

  /**
   * @throws RemoteException
   */
  public DBSupportMySqlImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#create(de.willuhn.util.ProgressMonitor)
   */
  public void create(ProgressMonitor monitor) throws RemoteException, ApplicationException
  {
    String dbname   = getDatabaseName();
    String username = getUsername();
    String hostname = getHostname();
    int port        = getTcpPort();

    if (dbname == null || dbname.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie den Namen der Datenbank an"));

    if (username == null || username.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Benutzernamen an"));

    if (hostname == null || hostname.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Hostnamen für die Datenbank an"));

    if (port <= 0 || port > 65535)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen gültigen TCP-Port ein"));


    PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
    File create = new File(res.getPath() + File.separator + "sql" + File.separator + "create_mysql.sql");
    File init   = new File(res.getPath() + File.separator + "sql" + File.separator + "init.sql");

    Connection conn = null;
    ResultSet rs    = null;
    try
    {
      try
      {
        Class.forName("com.mysql.jdbc.Driver");
      }
      catch (Throwable t)
      {
        Logger.error("unable to load jdbc driver",t);
        throw new ApplicationException(i18n.tr("Fehler beim Laden des JDBC-Treibers. {0}",t.getLocalizedMessage()));
      }
      
      String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port +
                       "/" + dbname + "?dumpQueriesOnException=true&amp;useUnicode=true&amp;characterEncoding=ISO8859_1";
      Logger.info("using jdbc url: " + jdbcUrl);

      try
      {
        conn = DriverManager.getConnection(jdbcUrl,username,getPassword());
      }
      catch (SQLException se)
      {
        Logger.error("unable to open sql connection",se);
        throw new ApplicationException(i18n.tr("Fehler beim Aufbau der Datenbankverbindung. {0}",se.getLocalizedMessage()));
      }
      
      // Wir schauen mal, ob vielleicht schon Tabellen existieren
      rs = conn.getMetaData().getTables(null,null,null,null);
      if (rs.next())
      {
        Logger.warn("database seems to exist, asking user to skip this step");
        String text = i18n.tr("Die Datenbank-Tabellen scheinen bereits zu existieren.\nMöchten Sie die Erstellung überspringen?");
        if (Application.getCallback().askUser(text))
        {
          Logger.info("creation of database skipped");
          monitor.setStatusText(i18n.tr("Erstellung der Datenbank übersprungen"));
          return;
        }
      }

      ScriptExecutor.execute(new FileReader(create),conn, monitor);
      monitor.setPercentComplete(0);
      ScriptExecutor.execute(new FileReader(init),conn, monitor);
      monitor.setStatusText(i18n.tr("Datenbank erfolgreich eingerichtet"));
    }
    catch (Throwable t)
    {
      Logger.error("unable to execute sql scripts",t);
      throw new ApplicationException(i18n.tr("Fehler beim Initialisieren der Datenbank. {0}", t.getLocalizedMessage()),t);
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (Throwable t)
        {
          Logger.error("unable to close resultset",t);
        }
      }
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
    return i18n.tr("MySQL 4.0 oder höher");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsDatabaseName()
   */
  public boolean needsDatabaseName() throws RemoteException
  {
    return true;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsHostname()
   */
  public boolean needsHostname() throws RemoteException
  {
    return true;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsPassword()
   */
  public boolean needsPassword() throws RemoteException
  {
    return true;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsTcpPort()
   */
  public boolean needsTcpPort() throws RemoteException
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
}


/*********************************************************************
 * $Log: DBSupportMySqlImpl.java,v $
 * Revision 1.2  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/