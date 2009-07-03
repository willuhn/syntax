/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/UserObject.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/07/03 10:52:19 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBObject;

/**
 * Vom User erweiterbare Objekte.
 * @author willuhn
 */
public interface UserObject extends DBObject
{

  /**
   * Prueft, ob das Objekt vom User angelegt wurde.
   * Liefert genau dann true, wenn getMandant nicht null liefert.
   * @return true, wenn es ein vom User angelegtes Objekt ist.
   * @throws RemoteException
   */
  public boolean isUserObject() throws RemoteException;

  /**
   * Liefert den Mandanten, wenn es ein vom User angelegtes Objekt ist, sonst immer null.
   * @return Mandant.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;
  
  /**
   * Speichert den Mandanten.
   * Laesst sich nur bei neu angelegten Objekten ausfuehren.
   * Andernfalls wird eine RemoteException geworfen.
   * @param mandant
   * @throws RemoteException
   */
  public void setMandant(Mandant mandant) throws RemoteException;

}

/*********************************************************************
 * $Log: UserObject.java,v $
 * Revision 1.3  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/