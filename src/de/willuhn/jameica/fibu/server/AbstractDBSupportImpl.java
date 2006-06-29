/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractDBSupportImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2006/06/29 16:38:09 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.willuhn.datasource.GenericObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Implementierung von Datenbank-Support-Klassen.
 */
public abstract class AbstractDBSupportImpl extends UnicastRemoteObject implements DBSupport
{
  
  I18N i18n = null;
  
  private String username = Settings.SETTINGS.getString("database.support.username","syntax");
  private String password = Settings.SETTINGS.getString("database.support.password",null);
  private String hostname = Settings.SETTINGS.getString("database.support.hostname","127.0.0.1");
  private String dbName   = Settings.SETTINGS.getString("database.support.dbname","syntax");
  private int tcpPort     = Settings.SETTINGS.getInt("database.support.tcpport",3306);

  /**
   * @throws java.rmi.RemoteException
   */
  public AbstractDBSupportImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }
 
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

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#store()
   */
  public void store() throws RemoteException
  {
    Settings.SETTINGS.setAttribute("database.support.username",getUsername());
    Settings.SETTINGS.setAttribute("database.support.password",getPassword());
    Settings.SETTINGS.setAttribute("database.support.hostname",getHostname());
    Settings.SETTINGS.setAttribute("database.support.tcpport",getTcpPort());
    Settings.SETTINGS.setAttribute("database.support.dbname",getDatabaseName());
    Settings.setDBSupport(this);
  }

}


/*********************************************************************
 * $Log: AbstractDBSupportImpl.java,v $
 * Revision 1.4  2006/06/29 16:38:09  willuhn
 * @N Hilfetext
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