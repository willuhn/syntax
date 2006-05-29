/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Transfer.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/05/29 17:30:26 $
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
 * Basis-Interface fuer Dinge mit Soll- und Habenkonto.
 * @author willuhn
 */
public interface Transfer extends DBObject
{

  /**
   * Liefert das Soll-Konto zu dieser Buchung.
   * @return Konto der Buchung.
   * @throws RemoteException
   */
  public Konto getSollKonto() throws RemoteException;

  /**
   * Liefert das Haben-Konto zu dieser Buchung.
   * @return Haben-Konto der Buchung.
   * @throws RemoteException
   */
  public Konto getHabenKonto() throws RemoteException;
  
  /**
   * Liefert den Text der Buchung.
   * @return Text der Buchung.
   * @throws RemoteException
   */
  public String getText() throws RemoteException;

  /**
   * Liefert den Netto-Betrag der Buchung.
   * @return Netto-Betrag der Buchung.
   * @throws RemoteException
   */
  public double getBetrag() throws RemoteException;

  /**
   * Liefert Steuersatz der Buchung.
   * @return Steuersatz der Buchung.
   * @throws RemoteException
   */
  public double getSteuer() throws RemoteException;

  /**
   * Setzt das Haben-Konto zu dieser Buchung.
   * @param k Haben-Konto der Buchung.
   * @throws RemoteException
   */
  public void setHabenKonto(Konto k) throws RemoteException;

  /**
   * Setzt das Soll-Konto zu dieser Buchung.
   * @param k Soll-Konto der Buchung.
   * @throws RemoteException
   */
  public void setSollKonto(Konto k) throws RemoteException;

  /**
   * Setzt den Text der Buchung.
   * @param text Text der Buchung.
   * @throws RemoteException
   */
  public void setText(String text) throws RemoteException;

  /**
   * Setzt den Netto-Betrag der Buchung.
   * @param betrag Betrag der Buchung.
   * @throws RemoteException
   */
  public void setBetrag(double betrag) throws RemoteException;

  /**
   * Setzt den Steuersatz der Buchung.
   * @param steuer Steuersatz der Buchung.
   * @throws RemoteException
   */
  public void setSteuer(double steuer) throws RemoteException;

  /**
   * Liefert true, wenn die Buchung geprueft wurde.
   * @return true, wenn sie geprueft wurde.
   * @throws RemoteException
   */
  public boolean isGeprueft() throws RemoteException;

  /**
   * Legt fest, ob die Buchung als geprueft gelten soll.
   * @param b true, wenn sie als geprueft gelten soll.
   * @throws RemoteException
   */
  public void setGeprueft(boolean b) throws RemoteException;
  
}

/*********************************************************************
 * $Log: Transfer.java,v $
 * Revision 1.2  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/