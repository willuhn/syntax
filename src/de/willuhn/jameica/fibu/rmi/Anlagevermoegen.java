/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Anlagevermoegen.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/29 14:26:57 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;

/**
 * Bildet einen einzelnen Posten des Anlagevermoegens ab.
 */
public interface Anlagevermoegen extends DBObject
{
  /**
   * Liefert die Bezeichnung des Anlagevermoegens.
   * @return Bezeichnung.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
  
  /**
   * Speichert die Bezeichnung des Anlagevermoegens.
   * @param name Bezeichnung.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;
  
  /**
   * Liefert die Anschaffungskosten.
   * @return Anschaffungskosten.
   * @throws RemoteException
   */
  public double getAnschaffungskosten() throws RemoteException;
  
  /**
   * Speichert die Anschaffungskosten.
   * @param kosten
   * @throws RemoteException
   */
  public void setAnschaffungskosten(double kosten) throws RemoteException;
  
  /**
   * Liefert das Anschaffungsatum.
   * @return Anschaffungsdatum.
   * @throws RemoteException
   */
  public Date getAnschaffungsDatum() throws RemoteException;
  
  /**
   * Speichert das Anschaffungsdatum.
   * @param d Anschaffungsdatum.
   * @throws RemoteException
   */
  public void setAnschaffungsDatum(Date d) throws RemoteException;
  
  /**
   * Liefert eine optionale Buchung, ueber die das Anlagegut in den Bestand gelangt ist.
   * @return Buchung.
   * @throws RemoteException
   */
  public Buchung getBuchung() throws RemoteException;
  
  /**
   * Speichert eine optionale Buchung, ueber die das Anlagegut in den Bestand gelangt ist.
   * @param buchung Buchung.
   * @throws RemoteException
   */
  public void setBuchung(Buchung buchung) throws RemoteException;
  
  /**
   * Liefert die Laufzeit fuer die Abschreibung in Jahren.
   * @return Laufzeit in Jahren.
   * @throws RemoteException
   */
  public int getLaufzeit() throws RemoteException;
  
  /**
   * Speichert die Laufzeit fuer die Abschreibung in Jahren.
   * @param laufzeit Laufzeit in Jahren.
   * @throws RemoteException
   */
  public void setLaufzeit(int laufzeit) throws RemoteException;
  
  /**
   * Liefert den aktuellen Restwert des Anlagegutes.
   * @return Restwert.
   * @throws RemoteException
   */
  public double getRestwert() throws RemoteException;
  
  /**
   * Liefert den Mandanten.
   * @return Mandant.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;
  
  /**
   * Speichert den Mandanten.
   * @param mandant Mandant.
   * @throws RemoteException
   */
  public void setMandant(Mandant mandant) throws RemoteException;
  
  /**
   * Liefert die Abschreibungsbuchungen zu diesem Anlagevermoegen.
   * @return Abschreibungsbuchungen.
   * @throws RemoteException
   */
  public DBIterator getAbschreibungen() throws RemoteException;
  
  /**
   * Prueft, ob das Anlagegut noch geaendert werden darf.
   * Sowie bereits Abschreibungen vorliegen, duerfen abschreibungsrelevante
   * Daten nicht mehr geaendert werden.
   * @return true, wenn das Anlagevermoegen nocht geaendert werden darf.
   * @throws RemoteException
   */
  public boolean canChange() throws RemoteException;
}


/*********************************************************************
 * $Log: Anlagevermoegen.java,v $
 * Revision 1.2  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 * Revision 1.1  2005/08/29 00:20:29  willuhn
 * @N anlagevermoegen
 *
 **********************************************************************/