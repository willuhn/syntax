/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.28 $
 * $Date: 2012/03/28 22:28:16 $
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
  
  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl() throws RemoteException
  {
    super();
    MultipleClassLoader cl = Application.getPluginLoader().getManifest(Fibu.class).getClassLoader();
    this.setClassloader(cl);
    this.setClassFinder(cl.getClassFinder());
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
    return Settings.getDBSupport().getSQLTimestamp(content);
  }
  
  /**
   * Ueberschrieben, damit der Service nur gestartet wird, wenn die DB eingerichtet ist.
   * @see de.willuhn.datasource.Service#start()
   */
  public synchronized void start() throws RemoteException
  {
    if (Settings.getDBSupport() == null)
    {
      Logger.info("first start: skipping db service");
      return;
    }

    if (this.isStarted())
    {
      Logger.warn("service allready started, skipping request");
      return;
    }

    ////////////////////////////////////////////////////////////////////////////
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
      updater.execute("^" + Settings.getDBSupport().getID() + "-.*");
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
    
    super.start();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcDriver()
   */
  protected String getJdbcDriver() throws RemoteException
  {
    return Settings.getDBSupport().getJdbcDriver();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcPassword()
   */
  protected String getJdbcPassword() throws RemoteException
  {
    return Settings.getDBSupport().getPassword();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcUrl()
   */
  protected String getJdbcUrl() throws RemoteException
  {
    return Settings.getDBSupport().getJdbcUrl();
  }
  
  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getJdbcUsername()
   */
  protected String getJdbcUsername() throws RemoteException
  {
    return Settings.getDBSupport().getUsername();
  }

  /**
   * @see de.willuhn.datasource.db.DBServiceImpl#getTransactionIsolationLevel()
   */
  protected int getTransactionIsolationLevel() throws RemoteException
  {
    return Settings.getDBSupport().getTransactionIsolationLevel();
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
      Settings.getDBSupport().checkConnection(conn);
    }
    catch (RemoteException re)
    {
      throw new SQLException(re.getMessage());
    }
    super.checkConnection(conn);
  }
  

}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
 * Revision 1.28  2012/03/28 22:28:16  willuhn
 * @N Einfuehrung eines neuen Interfaces "Plugin", welches von "AbstractPlugin" implementiert wird. Es dient dazu, kuenftig auch Jameica-Plugins zu unterstuetzen, die selbst gar keinen eigenen Java-Code mitbringen sondern nur ein Manifest ("plugin.xml") und z.Bsp. Jars oder JS-Dateien. Plugin-Autoren muessen lediglich darauf achten, dass die Jameica-Funktionen, die bisher ein Object vom Typ "AbstractPlugin" zuruecklieferten, jetzt eines vom Typ "Plugin" liefern.
 * @C "getClassloader()" verschoben von "plugin.getRessources().getClassloader()" zu "manifest.getClassloader()" - der Zugriffsweg ist kuerzer. Die alte Variante existiert weiterhin, ist jedoch als deprecated markiert.
 *
 * Revision 1.27  2011-07-25 10:03:18  willuhn
 * *** empty log message ***
 *
 * Revision 1.26  2011-07-25 10:01:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.25  2011-03-07 09:07:37  willuhn
 * @N Datenbank-Verbindung checken, bevor sie verwendet wird (aus Hibiscus uebernommen). Siehe Mail von Simon vom 05.03.2011
 *
 * Revision 1.24  2010-11-12 12:58:41  willuhn
 * @B Falscher Classloader
 *
 * Revision 1.23  2010-06-02 15:47:42  willuhn
 * @N Separierte SQL-Scripts fuer McKoi und MySQL - dann brauchen wir nicht dauernd eine extra Update-Klasse sondern koennen Plain-SQL-Scripts nehmen
 *
 * Revision 1.22  2010/06/01 17:42:03  willuhn
 * @N Neues Update-Verfahren via UpdateProvider
 *
 * Revision 1.21  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 **********************************************************************/