/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/12/11 21:00:35 $
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
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.rmi.DBIterator;

/**
 * Verwaltet die Einstellungen des Plugins.
 * @author willuhn
 */
public class Settings
{

  private static de.willuhn.jameica.Settings settings = new de.willuhn.jameica.Settings(Fibu.class);

  /**
   * Liefert den aktiven Mandanten oder null wenn noch keiner als aktiv markiert ist.
   * @return den aktiven Mandanten.
   */
  public static Mandant getActiveMandant() throws RemoteException
  {
    String mandant = settings.getAttribute("mandant",null);
    if (mandant == null)
      return null;

    DBIterator list = Application.getDefaultDatabase().createList(Mandant.class);
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

}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.2  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/