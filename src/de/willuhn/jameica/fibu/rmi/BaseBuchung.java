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
import java.util.Date;

/**
 * Diese Klasse bildet die Buchungen in Fibu ab.
 * @author willuhn
 */
public interface BaseBuchung extends Transfer
{

  /**
   * Liefert das Datum der Buchung.
   * Wenn es eine neue Buchung ist, wird das aktuelle Datum geliefert.
   * @return Datum der Buchung.
   * @throws RemoteException
   */
  public Date getDatum() throws RemoteException;

  /**
   * Liefert das Geschaeftsjahr zu dieser Buchung.
   * @return Geschaeftsjahr der Buchung.
   * @throws RemoteException
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException;
  
  /**
   * Liefert die Belegnummer oder erzeugt eine neue, wenn sie null ist.
   * @return Belegnummer.
   * @throws RemoteException
   */
  public int getBelegnummer() throws RemoteException;

  /**
   * Setzt das Datum der Buchung.
   * @param d Datum.
   * @throws RemoteException
   */
  public void setDatum(Date d) throws RemoteException;

  /**
   * Setzt die Belegnummer.
   * @param belegnummer Belegnummer der Buchung.
   * @throws RemoteException
   */
  public void setBelegnummer(int belegnummer) throws RemoteException;

  /**
   * Setzt das Geschaeftsjahr der Buchung.
   * @param jahr Geschaeftsjahr der Buchung.
   * @throws RemoteException
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException;
  
  /**
   * Liefert einen optionalen Kommentar fuer die Buchung.
   * @return optionaler Kommentar der Buchung.
   * @throws RemoteException
   */
  public String getKommentar() throws RemoteException;
  
  /**
   * Speichert einen optionalen Kommentar fuer die Buchung.
   * @param kommentar optionaler Kommentar der Buchung.
   * @throws RemoteException
   */
  public void setKommentar(String kommentar) throws RemoteException;

}
