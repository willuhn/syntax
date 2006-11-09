/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMcKoiImpl.java,v $
 * $Revision: 1.6 $
 * $Date: 2006/11/09 16:56:09 $
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

import de.willuhn.datasource.db.EmbeddedDatabase;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.messaging.StatusBarMessage;
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
    ResultSet rs    = null;
    try
    {
      
      // Verzeichnisse und leere DB ggf. erzeugen
      File dbDir = new File(res.getWorkPath(),"db");
      if (!dbDir.exists())
        dbDir.mkdirs();
      new EmbeddedDatabase(dbDir.getAbsolutePath(),username,getPassword());

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
      rs = conn.getMetaData().getTables(null,"APP",null,null);
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
 * Revision 1.6  2006/11/09 16:56:09  willuhn
 * @B Beruecksichtigung des Encodings beim Import der SQL-Files.
 *
 * Revision 1.5  2006/07/03 14:19:30  willuhn
 * @B Fehler bei Erstellung der McKoi-Datenbank
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