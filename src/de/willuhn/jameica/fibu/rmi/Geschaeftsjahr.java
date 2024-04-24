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
import java.util.Map;

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
   * Liefert die Haupt-Buchungen des Geschaeftsjahres.
   * @param noSplitHauptbuchungen. true wenn SplitHauptbuhungen weggelassen werden sollen
   * @return Buchungen.
   * @throws RemoteException
   */
  public DBIterator getHauptBuchungen(Boolean noSplitHauptbuchungen) throws RemoteException;

  /**
   * Liefert die Haupt-Buchungen des Geschaeftsjahres.
   * @param von Start-Datum. Kann auch weggelassen werden.
   * @param bis End-Datum. Kann auch weggelassen werden.
   * @param noSplitHauptbuchungen. true wenn SplitHauptbuhungen weggelassen werden sollen
   * @return Buchungen.
   * @throws RemoteException
   */
  public DBIterator getHauptBuchungen(Date von, Date bis, Boolean noSplitHauptbuchungen) throws RemoteException;

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
   * @param closed Status des Geschaeftsjahres.
   * @throws RemoteException
   */
  public void setClosed(boolean closed) throws RemoteException;
  
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
  
  /**
   * Liefert das Betriebsergebnis des Geschaeftsjahres fuer den angegebenen Zeitraum.
   * @param start Das Startdatum.
   * @param end das Enddatum.
   * @return Betriebsergebnis.
   * @throws RemoteException
   */
  public Betriebsergebnis getBetriebsergebnis(Date start, Date end) throws RemoteException;

  /**
   * Liefert die Betriebsergebnisse jedes Monats des Geschaeftsjahres.
   * @return Map von Betriebsergebnissen.
   * @throws RemoteException
   */
  public Map<String, Betriebsergebnis> getBetriebsergebnisseMonatlich() throws RemoteException;
}
