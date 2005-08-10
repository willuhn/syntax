/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Konto.java,v $
 * $Revision: 1.10 $
 * $Date: 2005/08/10 17:48:02 $
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
 * Diese Klasse bildet die Konten in Fibu ab.
 * @author willuhn
 */
public interface Konto extends DBObject
{

  /**
   * Liefert die Kontonummer.
   * @return Liefert die Kontonummer
   * @throws RemoteException
   */
  public String getKontonummer() throws RemoteException;

  /**
   * Liefert den Kontenrahmen, in dem sich das Konto befindet.
   * @return Kontenrahmen des Kontos.
   * @throws RemoteException
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException;

  /**
   * Liefert den Saldo des Kontos.
   * @return Saldo.
   * @throws RemoteException
   */
  public double getSaldo() throws RemoteException;


  /**
   * Liefert den Namen des Kontos.
   * @return Liefert die Bezeichnung des Kontos.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;

  /**
   * Liefert die Art des Kontos.
   * @return Art des Kontos.
   * @throws RemoteException
   */
  public Kontoart getKontoArt() throws RemoteException;

  /**
   * Liefert den Steuersatz des Kontos.
   * @return Liefert den Steuersatz.
   * @throws RemoteException
   */
  public Steuer getSteuer() throws RemoteException;




  /**
   * Setzt die Kontonummer.
   * @param kontonummer Die Kontonummer.
   * @throws RemoteException
   */
  public void setKontonummer(String kontonummer) throws RemoteException;

  /**
   * Setzt den Kontenrahmen, in dem sich das Konto befindet.
   * @param k Kontenrahmen des Kontos.
   * @throws RemoteException
   */
  public void setKontenrahmen(Kontenrahmen k) throws RemoteException;

  /**
   * Setzt den Namen des Kontos.
   * @param name Name des Kontos.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;

  /**
   * Setzt den Typ des Kontos.
   * @param art Art des Kontos.
   * @throws RemoteException
   */
  public void setKontoArt(Kontoart art) throws RemoteException;

  /**
   * Setzt den Steuersatz des Kontos.
   * @param steuer der Steuersatz
   * @throws RemoteException
   */
  public void setSteuer(Steuer steuer) throws RemoteException;

}

/*********************************************************************
 * $Log: Konto.java,v $
 * Revision 1.10  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.9  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.7  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.6  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.5  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.4  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/