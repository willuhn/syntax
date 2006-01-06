/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.7 $
 * $Date: 2006/01/06 00:05:51 $
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
import java.rmi.server.ServerNotActiveException;
import java.sql.Connection;
import java.util.HashMap;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Settings;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends de.willuhn.datasource.db.DBServiceImpl implements DBService
{

  private HashMap jahre = new HashMap();
  private Geschaeftsjahr jahr = null;
  
  private static Settings SETTINGS = new Settings(DBService.class);

  static
  {
    SETTINGS.setStoreWhenRead(false);
  }
  
  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl() throws RemoteException
  {
    super(
      SETTINGS.getString("jdbc.driver","com.mckoi.JDBCDriver"),
      SETTINGS.getString("jdbc.url",":jdbc:mckoi:local://" + Application.getPluginLoader().getPlugin(Fibu.class).getResources().getWorkPath() + "/db/db.conf"),
      SETTINGS.getString("jdbc.username","fibu"),
      SETTINGS.getString("jdbc.password","fibu")
    );
    this.setClassloader(Application.getClassLoader());
    this.setClassFinder(Application.getClassLoader().getClassFinder());
  }

  /**
   * Liefert die Connection.
   * TODO: Boeser Hack, ich weiss. ;)
   * @see de.willuhn.datasource.db.DBServiceImpl#getConnection()
   */
  public Connection getConnection()
  {
    return super.getConnection();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#setActiveGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setActiveGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    try
    {
      this.jahre.put(getClientHost(),jahr);
    }
    catch (ServerNotActiveException e)
    {
      // hu, wir laufen wohl lokal. Also koennen wir auch das lokale Geschaeftsjahr nehmen
      this.jahr = jahr;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#getActiveGeschaeftsjahr()
   */
  public Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException
  {
    try
    {
      return (Geschaeftsjahr) this.jahre.get(getClientHost());
    }
    catch (Exception e)
    {
      // lokaler Modus, also lokales Geschaeftsjahr nehmen
      return this.jahr;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#getSQLTimestampFunction()
   */
  public String getSQLTimestampFunction() throws RemoteException
  {
    return SETTINGS.getString("sql.function.timestamp","tonumber");
  }
}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
 * Revision 1.7  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.6  2006/01/05 17:40:30  willuhn
 * @N mysql support
 *
 * Revision 1.5  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.4  2005/10/20 23:03:44  willuhn
 * @N network support
 *
 * Revision 1.3  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/