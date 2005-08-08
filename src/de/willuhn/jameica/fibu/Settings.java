/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.9 $
 * $Date: 2005/08/08 21:35:46 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Verwaltet die Einstellungen des Plugins.
 * @author willuhn
 */
public class Settings
{

  private static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(Fibu.class);
  private static DBService db = null;
	private static Mandant mandant = null;
	/**
	 * Liefert den Datenbank-Service.
	 * @return Datenbank.
	 * @throws RemoteException
	 */
	public static DBService getDatabase() throws RemoteException
	{
    if (db == null)
    {
      try
      {
        db = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
      }
      catch (RemoteException e)
      {
        throw e;
      }
      catch (Exception e2)
      {
        Logger.error("unable to load database service",e2);
        throw new RemoteException("Fehler beim Laden des Datenbank-Service",e2);
      }
    }
    return db;
	}

  /**
   * Liefert den aktiven Mandanten oder null wenn noch keiner als aktiv markiert ist.
   * @return den aktiven Mandanten.
   * @throws RemoteException
   */
  public static Mandant getActiveMandant() throws RemoteException
  {
  	if (mandant != null && !mandant.isNewObject())
  		return mandant;

    String m = settings.getString("mandant",null);
    if (m == null || m.length() == 0)
			return null;

		Mandant mm = (Mandant) getDatabase().createObject(Mandant.class,m);
		if (mm == null || mm.isNewObject())
		{
			Logger.warn("defined mandant isn't readable");
		}
		mandant = mm;
		return mandant;
  }

  /**
   * Speichert den uebergebenen Mandanten als Aktiven.
   * @param m der zu aktivierende Mandant.
   * @throws RemoteException
   */
  public static void setActiveMandant(Mandant m) throws RemoteException
  {
  	if (m == null || m.isNewObject())
  	{
  		Logger.warn("given mandant was null or new object");
			return;
  	}
    settings.setAttribute("mandant",m.getID());
  }


  /**
   * Liefert die Bezeichnung der Waehrung.
   * @return Bezeichnung der Waehrung.
   */
  public static String getCurrency()
  {
    return settings.getString("currency","EUR");
  }

  /**
   * Speichert den Namen der Waehrung.
   * @param currency Name der Waehrung.
   */
  public static void setCurrency(String currency)
  {
    settings.setAttribute("currency",currency);
  }

}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.9  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.8  2004/02/09 13:05:13  willuhn
 * @C misc
 *
 * Revision 1.7  2004/01/28 00:37:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/28 00:31:34  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.3  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/