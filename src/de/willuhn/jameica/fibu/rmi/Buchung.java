/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Buchung.java,v $
 * $Revision: 1.6 $
 * $Date: 2003/12/01 21:23:00 $
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
import java.util.Date;

import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse bildet die Buchungen in Fibu ab.
 * @author willuhn
 */
public interface Buchung extends DBObject
{

  /**
   * Liefert das Datum der Buchung.
   * Wenn es eine neue Buchung ist, wird das aktuelle Datum geliefert.
   * @return Datum der Buchung.
   * @throws RemoteException
   */
  public Date getDatum() throws RemoteException;

  /**
   * Liefert das Konto zu dieser Buchung.
   * @return Konto der Buchung.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException;

  /**
   * Liefert das Geld-Konto zu dieser Buchung.
   * @return Geldkonto der Buchung.
   * @throws RemoteException
   */
  public Konto getGeldKonto() throws RemoteException;
  
  /**
   * Liefert den Mandanten zu dieser Buchung.
   * @return Mandant der Buchung.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;

  /**
   * Liefert den Text der Buchung.
   * @return Text der Buchung.
   * @throws RemoteException
   */
  public String getText() throws RemoteException;

  /**
   * Liefert die Belegnummer oder erzeugt eine neue, wenn sie null ist.
   * @return Belegnummer.
   * @throws RemoteException
   */
  public int getBelegnummer() throws RemoteException;

  /**
   * Erzeugt eine neue Buchungsnummer als Vorgabe.
   * @return neu erzeugte Buchungsnummer.
   * @throws RemoteException
   */
  public int createBelegnummer() throws RemoteException;

  /**
   * Liefert den Netto-Betrag der Buchung.
   * @return Brutto-Betrag der Buchung.
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
   * Setzt das Datum der Buchung.
   * @param d Datum.
   * @throws RemoteException
   */
  public void setDatum(Date d) throws RemoteException;

  /**
   * Setzt das Konto zu dieser Buchung.
   * @param k Konto der Buchung.
   * @throws RemoteException
   */
  public void setKonto(Konto k) throws RemoteException;

  /**
   * Setzt das Geld-Konto zu dieser Buchung.
   * @param k Konto der Buchung.
   * @throws RemoteException
   */
  public void setGeldKonto(Konto k) throws RemoteException;

  /**
   * Setzt den Text der Buchung.
   * @param Text der Buchung.
   * @throws RemoteException
   */
  public void setText(String text) throws RemoteException;

  /**
   * Setzt die Belegnummer.
   * @param belegnummer Belegnummer der Buchung.
   * @throws RemoteException
   */
  public void setBelegnummer(int belegnummer) throws RemoteException;

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
   * Setzt den Mandanten der Buchung.
   * @param mandant Mandant der Buchung.
   * @throws RemoteException
   */
  public void setMandant(Mandant mandant) throws RemoteException;
}

/*********************************************************************
 * $Log: Buchung.java,v $
 * Revision 1.6  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.3  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:10:56  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/