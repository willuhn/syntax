/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
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
   * Prueft, ob das Objekt vom User geaendert werden darf.
   * Liefert genau dann true, wenn getMandant nicht null liefert oder das
   * Schreiben im Systemkontenrahmen explizit erlaubt ist.
   * @return true, wenn das Objekt geaendert werden darf.
   * @throws RemoteException
   */
  public boolean canChange() throws RemoteException;

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
 * Revision 1.4  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.3  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/