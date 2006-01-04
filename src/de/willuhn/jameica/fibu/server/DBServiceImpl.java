/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2006/01/04 16:04:33 $
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

import de.willuhn.datasource.db.EmbeddedDBServiceImpl;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends EmbeddedDBServiceImpl implements DBService
{

  private HashMap jahre = new HashMap();
  private Geschaeftsjahr jahr = null;
  
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
}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
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