/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/01/03 18:07:22 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.Config;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.rmi.*;
import de.willuhn.jameica.rmi.DBHub;
import de.willuhn.jameica.rmi.DBIterator;

/**
 * Verwaltet die Einstellungen des Plugins.
 * @author willuhn
 */
public class Settings
{

  private static de.willuhn.jameica.Settings settings = new de.willuhn.jameica.Settings(Fibu.class);
  private static DBHub db = null;

  /**
   * Liefert den aktiven Mandanten oder null wenn noch keiner als aktiv markiert ist.
   * @return den aktiven Mandanten.
   */
  public static Mandant getActiveMandant() throws RemoteException
  {
    String mandant = settings.getAttribute("mandant",null);
    if (mandant == null)
      return null;

    DBIterator list = getDatabase().createList(Mandant.class);
    list.addFilter("firma='" + mandant + "' limit 1");
    if(!list.hasNext())
      return null; // kann sein, dass noch keiner als default definiert ist oder keine existieren

    return (Mandant) list.next();
  }

  /**
   * Speichert den uebergebenen Mandanten als Aktiven.
   * @param m der zu aktivierende Mandant.
   * @throws RemoteException
   */
  public static void setActiveMandant(Mandant m) throws RemoteException
  {
    settings.setAttribute("mandant",m.getFirma());
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

  /**
   * Liefert den Datenbank-Service fuer Fibu.
   * Bei der ersten Anfrage speichern wir den DBHub gleich hier, damit
   * er beim naechsten Request gleich da ist.
   * @return Datenbank-Servicehub.
   * @throws RemoteException
   */
  public static DBHub getDatabase() throws RemoteException
  {
    if (db != null)
      return db;

    try {
      db = (DBHub) ServiceFactory.lookupService(settings.getAttribute("dbhub",null));
      if (db == null)
        throw new RemoteException("unable to find configured database service.");
      
      return db;
    }
    catch (Exception e)
    {
			Application.getLog().error("unable to get database",e);
      throw new RemoteException(e.getMessage());
    }
  }

}

/*********************************************************************
 * $Log: Settings.java,v $
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