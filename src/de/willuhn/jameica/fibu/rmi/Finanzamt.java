/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Finanzamt.java,v $
 * $Revision: 1.3 $
 * $Date: 2004/01/25 19:44:03 $
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
 * Diese Interface bildet die Anschriftsdaten der Finanzaemter ab.
 * @author willuhn
 */
public interface Finanzamt extends DBObject
{

  /**
   * Liefert den Namen des Finanzamtes.
   * @return Name des Finanzamtes.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;

  /**
   * Liefert den Ort des Finanzamtes.
   * @return Ort des Finanzamtes.
   * @throws RemoteException
   */
  public String getOrt() throws RemoteException;

  /**
   * Liefert die Postleitzahl des Finanzamtes.
   * @return Postleitzahl des Finanzamtes.
   * @throws RemoteException
   */
  public String getPLZ() throws RemoteException;

  /**
   * Liefert das Postfach des Finanzamtes.
   * @return Postfach des Finanzamtes.
   * @throws RemoteException
   */
  public String getPostfach() throws RemoteException;

  /**
   * Liefert die Strasse des Finanzamtes.
   * @return Strasse des Finanzamtes.
   * @throws RemoteException
   */
  public String getStrasse() throws RemoteException;

  /**
   * Speichert den Namen des Finanzamtes.
   * @param name Name des Finanzamtes.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;

  /**
   * Speichert den Ort des Finanzamtes.
   * @param ort Ort des Finanzamtes.
   * @throws RemoteException
   */
  public void setOrt(String ort) throws RemoteException;

  /**
   * Speichert die Postleitzahl des Finanzamtes.
   * @param plz Postleitzahl des Finanzamtes.
   * @throws RemoteException
   */
  public void setPLZ(String plz) throws RemoteException;

  /**
   * Speichert das Postfach des Finanzamtes.
   * @param postfach Postfach des Finanzamtes.
   * @throws RemoteException
   */
  public void setPostfach(String postfach) throws RemoteException;

  /**
   * Speichert die Strasse des Finanzamtes.
   * @param strasse Strasse des Finanzamtes.
   * @throws RemoteException
   */
  public void setStrasse(String strasse) throws RemoteException;

}

/*********************************************************************
 * $Log: Finanzamt.java,v $
 * Revision 1.3  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/