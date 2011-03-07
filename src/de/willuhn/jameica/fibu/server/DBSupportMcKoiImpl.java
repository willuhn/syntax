/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMcKoiImpl.java,v $
 * $Revision: 1.15 $
 * $Date: 2011/03/07 09:07:37 $
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
public class DBSupportMcKoiImpl extends AbstractDBSupportImpl implements DBSupport
{
  
  /**
   * @see de.willuhn.datasource.GenericObject#getID()
   */
  public String getID() throws RemoteException
  {
    return "mckoi";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#create(de.willuhn.util.ProgressMonitor)
   */
  public void create(ProgressMonitor monitor) throws RemoteException, ApplicationException
  {
    String workdir = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getWorkPath();
    String appdir  = Application.getPluginLoader().getManifest(Fibu.class).getPluginDir();

    File create = new File(appdir + File.separator + "sql" + File.separator + "create_" + this.getID() + ".sql");
    File init   = new File(appdir + File.separator + "sql" + File.separator + "init.sql");
    
    Connection conn = null;
    ResultSet rs    = null;
    try
    {
      
      // Verzeichnisse und leere DB ggf. erzeugen
      File dbDir = new File(workdir,"db");
      if (!dbDir.exists())
        dbDir.mkdirs();

      String username = getUsername();
      if (username == null || username.length() == 0)
      {
        setUsername("syntax");
        store();
        username = getUsername();
      }
      String password = getPassword();
      if (password == null || password.length() == 0)
      {
        setPassword("syntax");
        store();
        password = getPassword();
      }
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
    return i18n.tr("Integrierte Datenbank (McKoi)");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsPassword()
   */
  public boolean needsPassword() throws RemoteException
  {
    return false;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsUsername()
   */
  public boolean needsUsername() throws RemoteException
  {
    return false;
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
  
  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getOrder()
   */
  int getOrder()
  {
    return 1;
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#checkConnection(java.sql.Connection)
   */
  public void checkConnection(Connection conn) throws RemoteException
  {
    // brauchen wir bei nicht, da Embedded
  }
}


/*********************************************************************
 * $Log: DBSupportMcKoiImpl.java,v $
 * Revision 1.15  2011/03/07 09:07:37  willuhn
 * @N Datenbank-Verbindung checken, bevor sie verwendet wird (aus Hibiscus uebernommen). Siehe Mail von Simon vom 05.03.2011
 *
 * Revision 1.14  2010-07-26 14:08:37  willuhn
 * @B Passwort wurde nicht gesetzt
 *
 * Revision 1.13  2010/06/07 15:45:15  willuhn
 * @N Erste Version der neuen UST-Voranmeldung mit Kennziffern aus der DB
 *
 * Revision 1.12  2010/06/02 15:47:42  willuhn
 * @N Separierte SQL-Scripts fuer McKoi und MySQL - dann brauchen wir nicht dauernd eine extra Update-Klasse sondern koennen Plain-SQL-Scripts nehmen
 *
 * Revision 1.11  2010/06/01 17:42:03  willuhn
 * @N Neues Update-Verfahren via UpdateProvider
 *
 * Revision 1.10  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.9  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.8.2.1  2009/06/23 10:08:29  willuhn
 * @C kleinere Todos
 *
 * Revision 1.8  2006/12/27 15:58:08  willuhn
 * @R removed unused method
 *
 * Revision 1.7  2006/11/17 00:11:20  willuhn
 * *** empty log message ***
 *
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