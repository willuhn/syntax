package de.willuhn.jameica.fibu.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

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

public class DBSupportH2Impl  extends AbstractDBSupportImpl implements DBSupport{
	
	  public DBSupportH2Impl()
	  {
	    this(false,null);
	  }
	  
	  public DBSupportH2Impl(boolean create, ProgressMonitor monitor)
	  {
		//Nur wenn auch H2 verwendet wird, sonst wird es bei FirstStart durch die Initialisierung für alle DBs gesetzt
		if("de.willuhn.jameica.fibu.server.DBSupportH2Impl".equals(Settings.SETTINGS.getString("database.support.class",null)))
		{
		    // H2-Datenbank verwendet uppercase Identifier
		    Logger.info("switching dbservice to uppercase");
		    System.setProperty(DBServiceImpl.class.getName() + ".uppercase",
		        "true");
		}

	    try
	    {
	      Method m = Application.getClassLoader().load("org.h2.engine.Constants")
	          .getMethod("getVersion", (Class<?>[]) null);
	      Logger.info("h2 version: " + m.invoke(null, (Object[]) null));
	    }
	    catch (Throwable t)
	    {
	      Logger.warn("unable to determine h2 version");
	    }
	    if(create)
			try {
				create(monitor,false);
			} catch (Throwable e) {
				Logger.warn("unable to create h2 database");
			}
	  }
	  
	@Override
	public String getID() throws RemoteException {
		return "h2";
	}

	@Override
	public String getName() throws RemoteException {
		return i18n.tr("Integrierte Datenbank (H2)");
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
	   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsDatabaseName()
	   */
	  public boolean needsDatabaseName() throws RemoteException
	  {
	    return false;
	  }
	  
	  /**
	   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsHostname()
	   */
	  public boolean needsHostname() throws RemoteException
	  {
	    return false;
	  }
	  
	  /**
	   * @see de.willuhn.jameica.fibu.rmi.DBSupport#create(de.willuhn.util.ProgressMonitor)
	   */
	  @Override
	  public void create(ProgressMonitor monitor) throws RemoteException, ApplicationException
	  {
		  create(monitor,true);
	  }
	  
	  /**
	   * Create mit der Moeglichkeit das initiale fuellen auzulassen
	   * @param monitor Monitor.
	   * @param fill wenn true wird die Datenbank gefuellt mit dem init.sql script.
	   */
	  public void create(ProgressMonitor monitor,boolean fill) throws RemoteException, ApplicationException
	  {
		// H2-Datenbank verwendet uppercase Identifier
	    //Hier ist es nötig, da es beim FirstStart noch nicht gesetzt wurde
	    Logger.info("switching dbservice to uppercase");
	    System.setProperty(DBServiceImpl.class.getName() + ".uppercase",
	        "true");
	    
	    String workdir = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getWorkPath();
	    String appdir  = Application.getPluginLoader().getManifest(Fibu.class).getPluginDir();

	    File create = new File(appdir + File.separator + "sql" + File.separator + "create_" + this.getID() + ".sql");
	    File init   = new File(appdir + File.separator + "sql" + File.separator + "init.sql");
	    
	    Connection conn = null;
	    ResultSet rs    = null;
	    try
	    {
	      
	      // Verzeichnisse und leere DB ggf. erzeugen
	      File dbDir = new File(workdir,"h2db");
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
	      rs = conn.getMetaData().getTables("%", "%", "%", new String[] { "TABLE" });
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
	        
	        if(fill) {
	        	//Monitor zurueckgesetzt
		        monitor.setPercentComplete(0);
		        r = new InputStreamReader(new FileInputStream(init),ENCODING_SQL);
		        monitor.setStatusText(i18n.tr("Erstelle Kontenrahmen"));
		        ScriptExecutor.execute(r,conn, monitor);
	        }
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

	@Override
	public String getJdbcUrl() throws RemoteException {
		PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
	    File dbDir = new File(res.getWorkPath(),"h2db");
	    if (!dbDir.exists())
	      dbDir.mkdirs();
	    return "jdbc:h2:" + res.getWorkPath() + "/h2db/syntax";
	}

	@Override
	public String getJdbcDriver() throws RemoteException {
		return "org.h2.Driver";
	}

	@Override
	public String getSQLTimestamp(String content) throws RemoteException
	  {
	    return MessageFormat.format("DATEDIFF('MS',''1970-01-01 00:00'',{0})", new Object[]{content});
	  }

	@Override
	int getOrder() {
		return 1;
	}

}
