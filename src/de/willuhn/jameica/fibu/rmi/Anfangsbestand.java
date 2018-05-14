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
 * Interface fuer den Anfangsbestand eines Kontos.
 */
public interface Anfangsbestand extends DBObject
{
  /**
   * Liefert das Geschaeftsjahr des Anfangsbestandes.
   * @return Geschaeftsjahr.
   * @throws RemoteException
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException;
  
  /**
   * Legt das Geschaeftsjahr des Anfangsbestandes fest.
   * @param jahr Geschaeftsjahr.
   * @throws RemoteException
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException;

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
 * Revision 1.2  2005/08/29 12:17:28  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/