/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Mandant.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/24 15:18:21 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.objects;

import java.rmi.RemoteException;

import de.willuhn.jameica.rmi.DBObject;

/**
 * Bildet einen Mandanten in Fibu ab.
 * @author willuhn
 * 24.11.2003
 */
public interface Mandant extends DBObject
{

  /**
   * Liefert den ersten Namen des Mandanten (typischerweise der Vorname).
   * @return name1 des Mandanten.
   * @throws RemoteException
   */
  public String getName1() throws RemoteException;
  
  /**
   * Liefert den zweiten Namen des Mandanten (typischerweise der Nachname).
   * @return name2 des Mandanten.
   * @throws RemoteException
   */
  public String getName2() throws RemoteException;
  
  /**
   * Liefert den Firmennamen des Mandanten.
   * @return Firmenname des Mandanten.
   * @throws RemoteException
   */
  public String getFirma() throws RemoteException;
  
  /**
   * Liefert die Strasse des Mandanten.
   * @return Strasse des Mandanten.
   * @throws RemoteException
   */
  public String getStrasse() throws RemoteException;
  
  /**
   * Liefert die PLZ des Mandanten.
   * @return PLZ des Mandanten.
   * @throws RemoteException
   */
  public String getPLZ() throws RemoteException;
  
  /**
   * Liefert den Ort des Mandanten.
   * @return Ort des Mandanten.
   * @throws RemoteException
   */
  public String getOrt() throws RemoteException;
  
  /**
   * Liefert die Steuernummer des Mandanten.
   * @return Steuernummer des Mandanten.
   * @throws RemoteException
   */
  public String getSteuernummer() throws RemoteException;
  
  /**
   * Liefert den ausgewaehlten Kontenrahmen des Mandanten.
   * @return Kontenrahmen des Mandanten.
   * @throws RemoteException
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException;

}


/*********************************************************************
 * $Log: Mandant.java,v $
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/