/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/10/18 23:28:55 $
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
import java.util.Hashtable;

import de.willuhn.datasource.db.EmbeddedDBServiceImpl;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends EmbeddedDBServiceImpl implements DBService
{

  private Hashtable jahre = new Hashtable();
  
  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl() throws RemoteException
  {
    super(Application.getPluginLoader().getPlugin(Fibu.class).getResources().getWorkPath() + "/db/db.conf","fibu","fibu");
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
    if (jahr == null)
      return;
    try
    {
      this.jahre.put(getClientHost(),jahr);
    }
    catch (ServerNotActiveException e)
    {
      // hu, wir laufen wohl lokal. Also koennen wir auch das lokale Geschaeftsjahr nehmen
      Settings.setActiveGeschaeftsjahr(jahr);
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
    catch (ServerNotActiveException e)
    {
      // lokaler Modus, also lokales Geschaeftsjahr nehmen
      return Settings.getActiveGeschaeftsjahr();
    }
  }
}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
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