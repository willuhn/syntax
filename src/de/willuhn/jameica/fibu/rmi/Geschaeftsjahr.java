/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Geschaeftsjahr.java,v $
 * $Revision: 1.8 $
 * $Date: 2005/09/05 13:14:27 $
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
import java.util.Date;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;

/**
 */
public interface Geschaeftsjahr extends DBObject
{

  /**
   * Prueft, ob sich das uebergebene Datum innerhalb des Geschaeftsjahres des Mandanten befindet.
   * @param d das zu pruefende Datum.
   * @return true, wenn es im Geschaeftsjahr liegt, sonst false.
   * @throws RemoteException
   */
  public boolean check(Date d) throws RemoteException;

  /**
   * Liefert den Beginn des Geschaeftsjahres.
   * @return Beginn des Geschaeftsjahres.
   * @throws RemoteException
   */
  public Date getBeginn() throws RemoteException;
  
  /**
   * Liefert das Ende des Geschaeftsjahres.
   * @return Ende des Geschaeftsjahres.
   * @throws RemoteException
   */
  public Date getEnde() throws RemoteException;
  
  /**
   * Speichert den Beginn des Geschaeftsjahres.
   * @param beginn Beginn des Geschaeftsjahres.
   * @throws RemoteException
   */
  public void setBeginn(Date beginn) throws RemoteException;
  
  /**
   * Speichert das Ende des Geschaeftsjahres.
   * @param ende Ende des Geschaeftsjahres.
   * @throws RemoteException
   */
  public void setEnde(Date ende) throws RemoteException;
  
  /**
   * Liefert den Mandanten.
   * @return Mandant.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;
  
  /**
   * Speichert den Mandanten.
   * @param m Mandant.
   * @throws RemoteException
   */
  public void setMandant(Mandant m) throws RemoteException;
  
  /**
   * Liefert den ausgewaehlten Kontenrahmen des Mandanten.
   * @return Kontenrahmen des Mandanten.
   * @throws RemoteException
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException;

  /**
   * Speichert den ausgewaehlten Kontenrahmen des Mandanten.
   * @param kontenrahmen Kontenrahmen des Mandanten.
   * @throws RemoteException
   */
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException;
  
  /**
   * Liefert die Buchungen des Geschaeftsjahres.
   * @return Buchungen.
   * @throws RemoteException
   */
  public DBIterator getBuchungen() throws RemoteException;
  
  /**
   * Liefert die Anfangsbestaende.
   * @return Anfangsbestaende.
   * @throws RemoteException
   */
  public DBIterator getAnfangsbestaende() throws RemoteException;
  
  /**
   * Liefert eine Liste der Abschreibungen des Jahres.
   * @return Abschreibungen.
   * @throws RemoteException
   */
  public GenericIterator getAbschreibungen() throws RemoteException;
  
  /**
   * Prueft, ob das Geschaeftsjahr bereits geschlossen ist.
   * @return true, wenn es geschlossen ist.
   * @throws RemoteException
   */
  public boolean isClosed() throws RemoteException;

  /**
   * Schliesst das Geschaeftsjahr ab.
   * @throws RemoteException
   */
  public void close() throws RemoteException;
  
  /**
   * Berechnet die Anzahl der Monate des Geschaeftsjahres.
   * @return Anzahl der Monate.
   * @throws RemoteException
   */
  public int getMonate() throws RemoteException;
  
  /**
   * Liefert das Vorjahr.
   * @return Vorjahr.
   * @throws RemoteException
   */
  public Geschaeftsjahr getVorjahr() throws RemoteException;
  
  /**
   * Speichert das Vorjahr.
   * @param vorjahr Vorjahr.
   * @throws RemoteException
   */
  public void setVorjahr(Geschaeftsjahr vorjahr) throws RemoteException;
  
  /**
   * Liefert das Betriebsergebnis des Geschaeftsjahres.
   * @return Betriebsergebnis.
   * @throws RemoteException
   */
  public Betriebsergebnis getBetriebsergebnis() throws RemoteException;
}


/*********************************************************************
 * $Log: Geschaeftsjahr.java,v $
 * Revision 1.8  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.6  2005/09/01 23:28:15  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.4  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.2  2005/08/29 16:43:14  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:28  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/