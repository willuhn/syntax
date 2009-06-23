/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMySqlImpl.java,v $
 * $Revision: 1.9.2.1 $
 * $Date: 2009/06/23 10:08:29 $
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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
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

    String appdir  = Application.getPluginLoader().getManifest(Fibu.class).getPluginDir();
    File create = new File(appdir + File.separator + "sql" + File.separator + "create_mysql.sql");
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
        Logger.info("file encoding to use for sql import: " + Settings.ENCODING);
        Reader r = new InputStreamReader(new FileInputStream(create),Settings.ENCODING);
        monitor.setStatusText(i18n.tr("Erstelle Datenbank"));
        ScriptExecutor.execute(r,conn, monitor);
        
        // Monitor zurueckgesetzt
        monitor.setPercentComplete(0);

        r = new InputStreamReader(new FileInputStream(init),Settings.ENCODING);
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
}


/*********************************************************************
 * $Log: DBSupportMySqlImpl.java,v $
 * Revision 1.9.2.1  2009/06/23 10:08:29  willuhn
 * @C kleinere Todos
 *
 * Revision 1.9  2007/11/05 01:02:27  willuhn
 * @C Transaction-Isolation-Level in SynTAX
 *
 * Revision 1.8  2006/12/27 15:58:08  willuhn
 * @R removed unused method
 *
 * Revision 1.7  2006/12/27 14:42:23  willuhn
 * @N Update fuer MwSt.-Erhoehung
 *
 * Revision 1.6  2006/11/21 13:17:56  willuhn
 * @B merged encoding bug into mysql support
 *
 * Revision 1.5  2006/11/17 00:11:20  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2006/06/29 15:11:31  willuhn
 * @N Setup-Wizard fertig
 * @N Auswahl des Geschaeftsjahres
 *
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