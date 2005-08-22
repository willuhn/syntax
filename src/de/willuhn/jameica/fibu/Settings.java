/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.16 $
 * $Date: 2005/08/22 16:37:22 $
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
import de.willuhn.jameica.fibu.gui.dialogs.MandantAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Verwaltet die Einstellungen des Plugins.
 * @author willuhn
 */
public class Settings
{
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(Fibu.class);

  /**
   * Default-Waehrung.
   */
  public final static String WAEHRUNG = settings.getString("currency.default","EUR");

  private static DBService db = null;
	private static Mandant mandant = null;
  
  /**
	 * Liefert den Datenbank-Service.
	 * @return Datenbank.
	 * @throws RemoteException
	 */
	public static DBService getDBService() throws RemoteException
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
   * Legt den aktiven Mandanten manuell fest.
   * @param m zu aktivierender Mandant.
   */
  public static void setActiveMandant(Mandant m)
  {
    if (m != null)
    {
      mandant = m;
      try
      {
        settings.setAttribute("mandant.acive",mandant.getID());
      }
      catch (RemoteException e)
      {
        Logger.error("unable to refresh status bar",e);
      }
    }
  }
  
  /**
   * Liefert den aktiven Mandanten oder einen Dialog zur Abfrage, falls dieser noch
   * nicht ausgeaehlt ist.
   * @return den aktiven Mandanten.
   * @throws RemoteException
   */
  public static Mandant getActiveMandant() throws RemoteException
  {
  	if (mandant != null && !mandant.isNewObject())
  		return mandant;

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  	try
    {
      String id = settings.getString("mandant.acive",null);
      if (id != null)
      {
        mandant = (Mandant) getDBService().createObject(Mandant.class,id);
        GUI.getStatusBar().setStatusText(i18n.tr("Aktiver Mandant: {0}, Gesch�ftsjahr: {1}", new String[]{mandant.getFirma(),(String)mandant.getAttribute("geschaeftsjahr")}));
        return mandant;
      }

      Logger.info("open mandant select dialog");
      MandantAuswahlDialog d = new MandantAuswahlDialog(MandantAuswahlDialog.POSITION_CENTER);
      mandant = (Mandant) d.open();
      setActiveMandant(mandant);
      GUI.getStatusBar().setStatusText(i18n.tr("Aktiver Mandant: {0}, Gesch�ftsjahr: {1}", new String[]{mandant.getFirma(),(String)mandant.getAttribute("geschaeftsjahr")}));
      return mandant;
    }
    catch (Exception e)
    {
      Logger.error("error while choosing mandant",e);
      throw new RemoteException("Fehler beim Ausw�hlen des aktiven Mandanten");
    }
  }
}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.16  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.15  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.14  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.12  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.11  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.10  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
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