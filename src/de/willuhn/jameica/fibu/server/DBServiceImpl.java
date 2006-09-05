/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.16 $
 * $Date: 2006/09/05 20:57:27 $
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
import java.util.HashMap;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends de.willuhn.datasource.db.DBServiceImpl implements DBService
{

  private HashMap jahre = new HashMap();
  private Geschaeftsjahr jahr = null;
  
  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl() throws RemoteException
  {
    super(null,null,null,null);
    this.setClassloader(Application.getClassLoader());
    this.setClassFinder(Application.getClassLoader().getClassFinder());
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
}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
 * Revision 1.16  2006/09/05 20:57:27  willuhn
 * @ResultsetIterator merged into datasource lib
 *
 * Revision 1.15  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
 * Revision 1.14  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.12  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 * Revision 1.11  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.10  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.8  2006/03/27 20:26:53  willuhn
 * *** empty log message ***
 *
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