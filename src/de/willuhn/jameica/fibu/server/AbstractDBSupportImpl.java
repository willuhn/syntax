/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractDBSupportImpl.java,v $
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
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;

import de.willuhn.datasource.GenericObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.ScriptExecutor;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Abstrakte Basis-Implementierung von Datenbank-Support-Klassen.
 */
public abstract class AbstractDBSupportImpl extends UnicastRemoteObject implements
    DBSupport
{
  
  I18N i18n = null;
  
  private String username = null;
  private String password = null;
  private String hostname = null;
  private String dbName   = null;
  private int tcpPort     = 0;

  /**
   * @throws java.rmi.RemoteException
   */
  public AbstractDBSupportImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#create(de.willuhn.util.ProgressMonitor)
   */
  public void create(ProgressMonitor monitor) throws ApplicationException
  {
    PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
    File create = new File(res.getPath() + File.separator + "sql" + File.separator + getCreateScript());
    File init   = new File(res.getPath() + File.separator + "sql" + File.separator + "init.sql");

    Connection conn = null;
    try
    {
      conn = getConnection();
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
      throw new ApplicationException(i18n.tr("Fehler beim Initialisieren der Datenbank"),t);
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
   * Liefert eine Connection zur Datenbank.
   * @return Connection.
   * @throws ApplicationException
   */
  abstract Connection getConnection() throws ApplicationException;

  /**
   * Liefert den Dateinamen des SQL-Create-Scripts.
   * @return Dateiname des SQL-Create-Scripts.
   */
  abstract String getCreateScript(); 
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsUsername()
   */
  public boolean needsUsername() throws RemoteException
  {
    return false;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsPassword()
   */
  public boolean needsPassword() throws RemoteException
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
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#needsTcpPort()
   */
  public boolean needsTcpPort() throws RemoteException
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
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#setUsername(java.lang.String)
   */
  public void setUsername(String username) throws RemoteException
  {
    this.username = username;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#setPassword(java.lang.String)
   */
  public void setPassword(String password) throws RemoteException
  {
    this.password = password;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#setHostname(java.lang.String)
   */
  public void setHostname(String hostname) throws RemoteException
  {
    this.hostname = hostname;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#setTcpPort(int)
   */
  public void setTcpPort(int port) throws RemoteException
  {
    this.tcpPort = port;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#setDatabaseName(java.lang.String)
   */
  public void setDatabaseName(String name) throws RemoteException
  {
    this.dbName = name;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getUsername()
   */
  public String getUsername() throws RemoteException
  {
    return this.username;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getPassword()
   */
  public String getPassword() throws RemoteException
  {
    return this.password;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getHostname()
   */
  public String getHostname() throws RemoteException
  {
    return this.hostname;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getTcpPort()
   */
  public int getTcpPort() throws RemoteException
  {
    return this.tcpPort;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getDatabaseName()
   */
  public String getDatabaseName() throws RemoteException
  {
    return this.dbName;
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    return getName();
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttributeNames()
   */
  public String[] getAttributeNames() throws RemoteException
  {
    return new String[]{"name"};
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getID()
   */
  public String getID() throws RemoteException
  {
    return getClass().getName();
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#equals(de.willuhn.datasource.GenericObject)
   */
  public boolean equals(GenericObject arg0) throws RemoteException
  {
    if (arg0 == null)
      return false;
    return this.getID().equals(arg0.getID());
  }

}


/*********************************************************************
 * $Log: AbstractDBSupportImpl.java,v $
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/