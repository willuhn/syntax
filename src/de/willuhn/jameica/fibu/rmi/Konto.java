/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Konto.java,v $
 * $Revision: 1.19 $
 * $Date: 2006/01/02 01:54:07 $
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
import de.willuhn.util.ApplicationException;

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
   * @param jahr das Geschaeftsjahr.
   * @return Saldo.
   * @throws RemoteException
   */
  public double getSaldo(Geschaeftsjahr jahr) throws RemoteException;

  /**
   * Liefert den Umsatz auf dem Konto im aktuellen Geschaeftsjahr.
   * Das entspricht der Summe der Buchungen auf dem Konto.
   * @param jahr das Geschaeftsjahr.
   * @return Umsatz.
   * @throws RemoteException
   */
  public double getUmsatz(Geschaeftsjahr jahr) throws RemoteException;
  
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
   * Liefert den Typ des Kontos.
   * @return Typ des Kontos oder <code>null</code> wenn kein Typ angegeben ist.
   * @throws RemoteException
   */
  public Kontotyp getKontoTyp() throws RemoteException;



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
   * Speichert die Art des Kontos.
   * @param art Art des Kontos.
   * @throws RemoteException
   */
  public void setKontoArt(Kontoart art) throws RemoteException;

  /**
   * Setzt den Typ des Kontos.
   * @param typ Typ des Kontos.
   * @throws RemoteException
   */
  public void setKontoTyp(Kontotyp typ) throws RemoteException;

  /**
   * Setzt den Steuersatz des Kontos.
   * @param steuer der Steuersatz
   * @throws RemoteException
   */
  public void setSteuer(Steuer steuer) throws RemoteException;
  
  /**
   * Liefert eine Liste aller Buchungen auf dem Konto.
   * @param jahr das Geschaeftsjahr.
   * @return Liste der Buchungen.
   * @throws RemoteException
   */
  public DBIterator getBuchungen(Geschaeftsjahr jahr) throws RemoteException;

  /**
   * Liefert eine Liste der Buchungen auf dem Konto begrenzt auf einen Zeitraum.
   * @param jahr das Geschaeftsjahr.
   * @param von Start-Datum.
   * @param bis End-Datum.
   * @throws ApplicationException
   * @return Liste der Buchungen.
   * @throws RemoteException
   */
  public DBIterator getBuchungen(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException, ApplicationException;

  /**
   * Liefert den Anfangsbestand des Kontos oder null wenn keiner existiert.
   * @param jahr das Geschaeftsjahr.
   * @return Anfangsbestand.
   * @throws RemoteException
   */
  public Anfangsbestand getAnfangsbestand(Geschaeftsjahr jahr) throws RemoteException;
  
  /**
   * Prueft, ob es ein vom User angelegtes Konto ist.
   * Liefert genau dann true, wenn getMandant nicht null liefert.
   * @return true, wenn es ein vom User angelegtes Konto ist.
   * @throws RemoteException
   */
  public boolean isUserKonto() throws RemoteException;

  /**
   * Liefert den Mandanten, wenn es ein User-Konto ist, sonst immer null.
   * @return Mandant.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException;
  
  /**
   * Speichert den Mandanten.
   * Laesst sich nur bei neu angelegten Konten ausfuehren.
   * Andernfalls wird eine RemoteException geworfen.
   * @param mandant
   * @throws RemoteException
   */
  public void setMandant(Mandant mandant) throws RemoteException;

}

/*********************************************************************
 * $Log: Konto.java,v $
 * Revision 1.19  2006/01/02 01:54:07  willuhn
 * @N Benutzerdefinierte Konten
 *
 * Revision 1.18  2005/10/18 09:25:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2005/10/06 16:00:37  willuhn
 * @B bug 135
 *
 * Revision 1.16  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.14  2005/08/30 23:15:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.2  2005/08/15 23:38:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
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