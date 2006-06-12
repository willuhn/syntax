/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBSupportMySqlImpl.java,v $
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

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;

import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

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
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getConnection()
   */
  Connection getConnection() throws ApplicationException
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
    }
    catch (Throwable t)
    {
      Logger.error("unable to load jdbc driver",t);
      throw new ApplicationException(i18n.tr("Fehler beim Laden des JDBC-Treibers"));
    }

    try
    {
      String jdbcUrl = "jdbc:mysql://" + getHostname() + ":" + getTcpPort() +
                         "/" + getDatabaseName() + "?dumpQueriesOnException=true&amp;useUnicode=true&amp;characterEncoding=ISO8859_1";
      Logger.info("using jdbc url: " + jdbcUrl);
      return DriverManager.getConnection(jdbcUrl,getUsername(),getPassword());
    }
    catch (Throwable t)
    {
      Logger.error("unable to connect to database",t);

      Throwable tOrig = t.getCause();
      
      String msg = t.getLocalizedMessage();
      if (tOrig != null & tOrig != t)
        msg += ". " + tOrig.getLocalizedMessage();
      throw new ApplicationException(msg);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractDBSupportImpl#getCreateScript()
   */
  String getCreateScript()
  {
    return "create_mysql.sql";
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
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/