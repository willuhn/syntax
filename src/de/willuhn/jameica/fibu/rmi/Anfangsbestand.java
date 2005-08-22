/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Anfangsbestand.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/22 16:37:22 $
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
 * Interface fuer den Anfangsbestand eines Kontos.
 */
public interface Anfangsbestand extends DBObject
{
  /**
   * Liefert den Mandanten des Anfangsbestandes.
   * @return Mandant.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;
  
  /**
   * Legt den Mandanten des Anfangsbestandes fest.
   * @param m Mandant.
   * @throws RemoteException
   */
  public void setMandant(Mandant m) throws RemoteException;

  /**
   * Liefert das Konto des Anfangsbestandes.
   * @return Konto.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException;

  /**
   * Legt das Konto des Anfangsbestandes fest.
   * @param k das Konto.
   * @throws RemoteException
   */
  public void setKonto(Konto k) throws RemoteException;

  /**
   * Liefert den Betrag des Anfangsbestandes.
   * @return der Betrag des Anfangsbestandes.
   * @throws RemoteException
   */
  public double getBetrag() throws RemoteException;
  
  /**
   * Legt den Betrag des Anfangsbestandes fest.
   * @param betrag Betrag des Anfangsbestandes.
   * @throws RemoteException
   */
  public void setBetrag(double betrag) throws RemoteException;
}


/*********************************************************************
 * $Log: Anfangsbestand.java,v $
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/