/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.6 $
 * $Date: 2004/01/28 00:31:34 $
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
import de.willuhn.datasource.rmi.ServiceData;
import de.willuhn.jameica.Application;
import de.willuhn.jameica.Config;
import de.willuhn.jameica.fibu.rmi.Mandant;

/**
 * Verwaltet die Einstellungen des Plugins.
 * @author willuhn
 */
public class Settings
{

  private static de.willuhn.jameica.Settings settings = new de.willuhn.jameica.Settings(Fibu.class);
  private static DBService db = null;
	private static Mandant mandant = null;
	/**
	 * Liefert den Datenbank-Service.
	 * @return Datenbank.
	 * @throws RemoteException
	 */
	public static DBService getDatabase() throws RemoteException
	{
		return db;
	}

	/**
	 * Speichert die zu verwendende Datenbank.
	 * @param db die Datenbank.
	 */
	protected static void setDatabase(DBService db)
	{
		Settings.db = db;
	}

  /**
   * Liefert den aktiven Mandanten oder null wenn noch keiner als aktiv markiert ist.
   * @return den aktiven Mandanten.
   */
  public static Mandant getActiveMandant() throws RemoteException
  {
  	if (mandant != null)
  		return mandant;

    String m = settings.getAttribute("mandant",null);
    if (m == null ||m.length() == 0)
      return null;

		Mandant mm = (Mandant) getDatabase().createObject(Mandant.class,m);
		if (mm == null || mm.isNewObject())
		{
			Application.getLog().warn("defined mandant isn't readable");
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
  		Application.getLog().warn("given mandant was null or new object");
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
    return settings.getAttribute("currency","EUR");
  }

  /**
   * Speichert den Namen der Waehrung.
   * @param currency Name der Waehrung.
   */
  public static void setCurrency(String currency)
  {
    settings.setAttribute("currency",currency);
  }


  /**
   * Speichert die Datenbankverbindung.
   * @param name Alias-Name des Datenbank-Services.
   * @throws RemoteException
   */
  public static void setDatabase(String name) throws RemoteException
  {
    ServiceData sd = null;
    try {
      sd = (ServiceData) Application.getConfig().getLocalServiceData(name);
      if (sd == null) sd = (ServiceData) Application.getConfig().getRemoteServiceData(name);

      if (sd == null || sd.getType() != Config.SERVICETYPE_DATABASE)
        throw new RemoteException("A database hub with this name does not exist.");
      settings.setAttribute("dbhub",name);
    }
    catch (Exception e)
    {
			Application.getLog().error("unable to set database",e);
      throw new RemoteException(e.getMessage());
    }
    
  }

}

/*********************************************************************
 * $Log: Settings.java,v $
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