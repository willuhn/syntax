/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.UpdateProvider;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.version.Updater;
import de.willuhn.util.MultipleClassLoader;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends de.willuhn.datasource.db.DBServiceImpl implements DBService
{
  private Geschaeftsjahr jahr = null;
  private boolean doUpdates = false;
  private DBSupport driver = null;
  
  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl() throws RemoteException
  {
    this(true);
  }

  /**
   * ct.
   * Interner Konstruktor für die Updates.
   * @param doUpdates true, wenn die Updates ausgeführt werden sollen.
   * @throws RemoteException
   */
  DBServiceImpl(boolean doUpdates) throws RemoteException
  {
    this(Settings.getDBSupport());
    this.doUpdates = doUpdates;
  }
  
  /**
   * ct.
   * Konstruktor mit expliziter Angabe des Treibers.
   * @param dbSupport der zu verwendende Treiber.
   * @throws RemoteException
   */
  public DBServiceImpl(DBSupport dbSupport) throws RemoteException
  {
    super();
    MultipleClassLoader cl = Application.getPluginLoader().getManifest(Fibu.class).getClassLoader();
    this.setClassloader(cl);
    this.setClassFinder(cl.getClassFinder());
//    if (dbSupport == null)
//      throw new RemoteException("no driver given");
    this.driver = dbSupport;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#setActiveGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setActiveGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    this.jahr = jahr;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#getActiveGeschaeftsjahr()
   */
  public Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException
  {
    return this.jahr;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#getSQLTimestamp(java.lang.String)
   */
  public String getSQLTimestamp(String content) throws RemoteException
  {
    return this.driver.getSQLTimestamp(content);
  }
  
  /**
   * Ueberschrieben, damit der Service nur gestartet wird, wenn die DB eingerichtet ist.
   * @see de.willuhn.datasource.Service#start()
   */
  public synchronized void start() throws RemoteException
  {
    if (this.driver == null)
    {
      this.driver = Settings.getDBSupport();
      if(this.driver == null)
      {
	      Logger.info("first start: skipping db service");
	      return;
      }
    }

    if (this.isStarted())
    {
      Logger.warn("service already started, skipping request");
      return;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    if (this.doUpdates)
    {
      // Init Database
      Logger.info("init database");
      Connection conn = null;
      try
      {
        Class.forName(this.getJdbcDriver());
        conn = DriverManager.getConnection(this.getJdbcUrl(),this.getJdbcUsername(),this.getJdbcPassword());

        Logger.info("init update provider");
        UpdateProvider provider = new UpdateProvider(conn);
        Updater updater = new Updater(provider,DBSupport.ENCODING_SQL);
        updater.execute("^" + Settings.getDBSupport().getID() + ".*");
        Logger.info("updates finished");
      }
      catch (RemoteException re)
      {
        throw re;
      }
      catch (Exception e)
      {
        throw new RemoteException(e.getMessage(),e);
      }
      finally
      {
        if (conn != null) {
          try {
            conn.close();
          }
          catch (Exception e)
          {
            Logger.error("error while closing connection",e);
          }
        }
      }
    }
    //
    ////////////////////////////////////////////////////////////////////////////
    
    super.start();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcDriver()
   */
  protected String getJdbcDriver() throws RemoteException
  {
    return this.driver.getJdbcDriver();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcPassword()
   */
  protected String getJdbcPassword() throws RemoteException
  {
    return this.driver.getPassword();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcUrl()
   */
  protected String getJdbcUrl() throws RemoteException
  {
    return this.driver.getJdbcUrl();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcUsername()
   */
  protected String getJdbcUsername() throws RemoteException
  {
    return this.driver.getUsername();
  }

  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getTransactionIsolationLevel()
   */
  protected int getTransactionIsolationLevel() throws RemoteException
  {
    return this.driver.getTransactionIsolationLevel();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#executeUpdate(java.lang.String, java.lang.Object[])
   */
  public void executeUpdate(String sql, Object[] params) throws RemoteException
  {
    if (!isStarted())
      throw new RemoteException("db service not started");

    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = getConnection().prepareStatement(sql);
      if (params != null)
      {
        for (int i=0;i<params.length;++i)
        {
          Object o = params[i];
          if (o == null)
            ps.setNull((i+1), Types.NULL);
          else
            ps.setObject((i+1),params[i]);
        }
      }

      ps.executeUpdate();
    }
    catch (SQLException e)
    {
      Logger.error("error while executing sql update statement \"" + sql + "\"",e);
      throw new RemoteException("error while executing sql update statement: " + e.getMessage(),e);
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
          Logger.error("error while closing resultset",t);
        }
      }
      if (ps != null)
      {
        try
        {
          ps.close();
        }
        catch (Throwable t2)
        {
          Logger.error("error while closing statement",t2);
        }
      }
    }
  }

  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#checkConnection(java.sql.Connection)
   */
  protected void checkConnection(Connection conn) throws SQLException
  {
    try
    {
    	this.driver.checkConnection(conn);
    }
    catch (RemoteException re)
    {
      throw new SQLException(re.getMessage());
    }
    super.checkConnection(conn);
  }
}
