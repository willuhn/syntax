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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.willuhn.datasource.GenericObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Implementierung von Datenbank-Support-Klassen.
 */
public abstract class AbstractDBSupportImpl implements DBSupport, Comparable
{
  final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  private String username = Settings.SETTINGS.getString("database.support.username","syntax");
  private String password = Settings.SETTINGS.getString("database.support.password",null);
  private String hostname = Settings.SETTINGS.getString("database.support.hostname","127.0.0.1");
  private String dbName   = Settings.SETTINGS.getString("database.support.dbname","syntax");
  private int tcpPort     = Settings.SETTINGS.getInt("database.support.tcpport",3306);

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

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#getTransactionIsolationLevel()
   */
  public int getTransactionIsolationLevel() throws RemoteException
  {
    return -1;
  }
  
  /**
   * Liefert die Reihenfolge fuer die Sortierung in der Auswahlbox.
   * @return Reihenfolge.
   */
  abstract int getOrder();

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (o == null || !(o instanceof AbstractDBSupportImpl))
      return -1;
    return this.getOrder() - ((AbstractDBSupportImpl)o).getOrder();
  }
  
  private long lastCheck = 0;

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBSupport#checkConnection(java.sql.Connection)
   */
  public void checkConnection(Connection conn) throws RemoteException
  {
    long newCheck = System.currentTimeMillis();
    if ((newCheck - lastCheck) < (10 * 1000L))
      return; // Wir checken hoechstens aller 10 Sekunden
    
    Statement s  = null;
    ResultSet rs = null;
    try
    {
      s = conn.createStatement();
      rs = s.executeQuery("select 1");
      lastCheck = newCheck;
    }
    catch (SQLException e)
    {
      // das Ding liefert in getMessage() den kompletten Stacktrace mit, den brauchen wir
      // nicht (das muellt uns nur das Log voll) Also fangen wir sie und werfen eine neue
      // saubere mit kurzem Fehlertext
      String msg = e.getMessage();
      if (msg != null && msg.indexOf("\n") != -1)
        msg = msg.substring(0,msg.indexOf("\n"));
      throw new RemoteException(msg);
    }
    finally
    {
      try
      {
        if (rs != null) rs.close();
        if (s != null)  s.close();
      }
      catch (Exception e)
      {
        throw new RemoteException("unable to close statement/resultset",e);
      }
    }
  }
  
}


/*********************************************************************
 * $Log: AbstractDBSupportImpl.java,v $
 * Revision 1.9  2011/03/07 09:07:37  willuhn
 * @N Datenbank-Verbindung checken, bevor sie verwendet wird (aus Hibiscus uebernommen). Siehe Mail von Simon vom 05.03.2011
 *
 * Revision 1.8  2010-06-07 15:45:15  willuhn
 * @N Erste Version der neuen UST-Voranmeldung mit Kennziffern aus der DB
 *
 * Revision 1.7  2010/06/02 15:47:42  willuhn
 * @N Separierte SQL-Scripts fuer McKoi und MySQL - dann brauchen wir nicht dauernd eine extra Update-Klasse sondern koennen Plain-SQL-Scripts nehmen
 *
 * Revision 1.6  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.5  2007/11/05 01:02:26  willuhn
 * @C Transaction-Isolation-Level in SynTAX
 *
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