/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Steuer.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/01 20:29:00 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.objects;

import java.rmi.RemoteException;

import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse bildet die verschiedenen Steuersätze in Fibu ab.
 * @author willuhn
 */
public interface Steuer extends DBObject
{

  /**
   * Liefert die Bezeichnung des Steuersatzes.
   * @return Name des Steuersatzes.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;

  /**
   * Liefert den Steuersatz dieser Steuer.
   * @return Steuersatz dieser Steuer.
   * @throws RemoteException
   */
  public double getSatz() throws RemoteException;

  /**
   * Setzt den Namen des Steuersatzes.
   * @param Name des Steuersatzes.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;

  /**
   * Setzt den Steuersatz dieser Steuer.
   * @param satz Steuersatz.
   * @throws RemoteException
   */
  public void setSatz(double satz) throws RemoteException;

}

/*********************************************************************
 * $Log: Steuer.java,v $
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/