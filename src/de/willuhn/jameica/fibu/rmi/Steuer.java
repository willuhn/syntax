/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Steuer.java,v $
 * $Revision: 1.7 $
 * $Date: 2005/10/05 17:52:33 $
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
 * Diese Klasse bildet die verschiedenen Steuersaetze in Fibu ab.
 * Es werden zwei verschiedene Arten von Steuersaetzen unterschieden.
 * Initiale Steuersaetze gelten fuer alle Mandanten und koennen nicht
 * geaendert werden. Zusaetzlich existieren noch benutzerspezifische
 * Steuersaetze, die jeweils nur fuer den Mandanten gelten, der sie
 * angelegt hat. Wird eine Liste von Steuersaetzen geladen, so werden
 * immer auch nur die initialen sowie die des aktuellen Mandanten geladen.
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
   * Liefert das Steuersammelkonto, welches fuer Konten verwendet wird, die
   * diese Steuer als Vorschlag registriert haben.
   * @return das Steuersammel-Konto
   * @throws RemoteException
   */
  public Konto getSteuerKonto() throws RemoteException;
  
  /**
   * Setzt den Namen des Steuersatzes.
   * @param name Name des Steuersatzes.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;

  /**
   * Setzt den Steuersatz dieser Steuer.
   * @param satz Steuersatz.
   * @throws RemoteException
   */
  public void setSatz(double satz) throws RemoteException;

  /**
   * Setzt das Steuer-Sammelkonto.
   * @param k das zu verwendende Steuersammel-Konto.
   * @throws RemoteException
   */
  public void setSteuerKonto(Konto k) throws RemoteException;
  
  /**
   * Prueft, ob der Steuersatz zum initialen Datenbestand gehoert.
   * @return true, wenn er zum initialen Datenbestand gehoert und damit nicht aenderbar ist.
   * @throws RemoteException
   */
  public boolean isInitial() throws RemoteException;
  
  /**
   * Liefert den Mandanten zu dem Steuersatz.
   * @return Liefert genau den Mandanten, der den Steuersatz angelegt hat.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;
}

/*********************************************************************
 * $Log: Steuer.java,v $
 * Revision 1.7  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.6  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.5  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.4  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/