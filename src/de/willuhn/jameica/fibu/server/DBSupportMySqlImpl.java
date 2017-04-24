/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.ScriptExecutor;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung des MySQL-Supports.
 */
public class DBSupportMySqlImpl extends AbstractDBSupportImpl implements DBSupport
{
  /**
   * @see de.willuhn.datasource.GenericObject#getID()
   */
  public String getID() throws RemoteException
  {
    return "mysql";
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

    String appdir  = Application.getPluginLoader().getManifest(Fibu.class).getPluginDir();
    File create = new File(appdir + File.separator + "sql" + File.separator + "create_" + this.getID() + ".sql");
    File init   = new File(appdir + File.separator + "sql" + File.separator + "init.sql");

    Connection conn = null;
    ResultSet rs    = null;
    try
    {
      try
      {
        Class.forName(getJdbcDriver());
      }
      catch (Throwable t)
      {
        Logger.error("unable to load jdbc driver",t);
        throw new ApplicationException(i18n.tr("Fehler beim Laden des JDBC-Treibers. {0}",t.getLocalizedMessage()));
      }
      
      String jdbcUrl = getJdbcUrl();
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
        Logger.warn("database seems to exist, skip database creation");
        String msg = i18n.tr("Datenbank existiert bereits. Überspringe Erstellung");
        monitor.setStatusText(msg);
        monitor.setPercentComplete(100);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(msg, StatusBarMessage.TYPE_SUCCESS));
      }
      else
      {
        Reader r = new InputStreamReader(new FileInputStream(create),ENCODING_SQL);
        monitor.setStatusText(i18n.tr("Erstelle Datenbank"));
        ScriptExecutor.execute(r,conn, monitor);
        
        // Monitor zurueckgesetzt
        monitor.setPercentComplete(0);

        r = new InputStreamReader(new FileInputStream(init),ENCODING_SQL);
        monitor.setStatusText(i18n.tr("Erstelle Kontenrahmen"));
        ScriptExecutor.execute(r,conn, monitor);
        monitor.setStatusText(i18n.tr("Datenbank erfolgreich eingerichtet"));
      }

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
    return i18n.tr("Externe Datenbank (MySQL 4.0 oder höher)");
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

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getJdbcUrl()
   */
  public String getJdbcUrl() throws RemoteException
  {
    return "jdbc:mysql://" + getHostname() + ":" + getTcpPort() + "/" + getDatabaseName() + "?dumpQueriesOnException=true&amp;useUnicode=true&amp;characterEncoding=ISO8859_1";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getJdbcDriver()
   */
  public String getJdbcDriver() throws RemoteException
  {
    return "com.mysql.jdbc.Driver";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getSQLTimestamp(java.lang.String)
   */
  public String getSQLTimestamp(String content) throws RemoteException
  {
    return "(UNIX_TIMESTAMP({0})*1000)".replaceAll("\\{0\\}",content);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getTransactionIsolationLevel()
   */
  public int getTransactionIsolationLevel() throws RemoteException
  {
    // damit sehen wir Datenbank-Updates durch andere
    // ohne vorher ein COMMIT machen zu muessen
    // Insbesondere bei MySQL sinnvoll.
    return Connection.TRANSACTION_READ_COMMITTED;
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getOrder()
   */
  int getOrder()
  {
    return 10;
  }
  
}
