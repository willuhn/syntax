/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Mandant.java,v $
 * $Revision: 1.13 $
 * $Date: 2006/05/08 22:44:18 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;

/**
 * Bildet einen Mandanten in Fibu ab.
 * @author willuhn
 * 24.11.2003
 */
public interface Mandant extends DBObject
{

  /**
   * Liefert den ersten Namen des Mandanten (typischerweise der Vorname).
   * @return name1 des Mandanten.
   * @throws RemoteException
   */
  public String getName1() throws RemoteException;
  
  /**
   * Liefert den zweiten Namen des Mandanten (typischerweise der Nachname).
   * @return name2 des Mandanten.
   * @throws RemoteException
   */
  public String getName2() throws RemoteException;
  
  /**
   * Liefert den Firmennamen des Mandanten.
   * @return Firmenname des Mandanten.
   * @throws RemoteException
   */
  public String getFirma() throws RemoteException;
  
  /**
   * Liefert die Strasse des Mandanten.
   * @return Strasse des Mandanten.
   * @throws RemoteException
   */
  public String getStrasse() throws RemoteException;
  
  /**
   * Liefert die PLZ des Mandanten.
   * @return PLZ des Mandanten.
   * @throws RemoteException
   */
  public String getPLZ() throws RemoteException;
  
  /**
   * Liefert den Ort des Mandanten.
   * @return Ort des Mandanten.
   * @throws RemoteException
   */
  public String getOrt() throws RemoteException;
  
  /**
   * Liefert die Steuernummer des Mandanten.
   * @return Steuernummer des Mandanten.
   * @throws RemoteException
   */
  public String getSteuernummer() throws RemoteException;
  
  /**
   * Liefert eine Liste der Geschaeftsjahre.
   * @return Liste der Geschaeftsjahre.
   * @throws RemoteException
   */
  public DBIterator getGeschaeftsjahre() throws RemoteException; 

  /**
   * Liefert das Anlagevermoegen des Mandanten.
   * @return Anlagevermoegen.
   * @throws RemoteException
   */
  public DBIterator getAnlagevermoegen() throws RemoteException;
  
  /**
   * Liefert eine Liste der Buchungsvorlagen des Mandanten.
   * @return Liste der Buchungsvorlagen.
   * @throws RemoteException
   */
  public DBIterator getBuchungstemplates() throws RemoteException;
  
  /**
   * Liefert das zugehoerige Finanzamt.
   * @return das Finanzamt des Mandanten.
   * @throws RemoteException
   */
  public Finanzamt getFinanzamt() throws RemoteException;
  
  /**
   * Speichert den ersten Namen des Mandanten (typischerweise der Vorname).
   * @param name1 Name 1 des Mandanten. 
   * @throws RemoteException
   */
  public void setName1(String name1) throws RemoteException;
  
  /**
   * Speichert den zweiten Namen des Mandanten (typischerweise der Nachname).
   * @param name2 Name 2 des Mandanten.
   * @throws RemoteException
   */
  public void setName2(String name2) throws RemoteException;
  
  /**
   * Speichert den Firmennamen des Mandanten.
   * @param firma Firmenname.
   * @throws RemoteException
   */
  public void setFirma(String firma) throws RemoteException;
  
  /**
   * Speichert die Strasse des Mandanten.
   * @param strasse Strasse des Mandanten.
   * @throws RemoteException
   */
  public void setStrasse(String strasse) throws RemoteException;
  
  /**
   * Speichert die PLZ des Mandanten.
   * @param plz PLZ des Mandanten.
   * @throws RemoteException
   */
  public void setPLZ(String plz) throws RemoteException;
  
  /**
   * Speichert den Ort des Mandanten.
   * @param ort Ort des Mandanten.
   * @throws RemoteException
   */
  public void setOrt(String ort) throws RemoteException;
  
  /**
   * Speichert die Steuernummer des Mandanten.
   * @param steuernummer Steuernummer des Mandanten.
   * @throws RemoteException
   */
  public void setSteuernummer(String steuernummer) throws RemoteException;
  
  /**
   * Speichert das Finanzamt des Mandanten.
   * @param finanzamt Finanzamt des Mandanten.
   * @throws RemoteException
   */
  public void setFinanzamt(Finanzamt finanzamt) throws RemoteException;

  /**
   * Liefert die Waehrung.
   * @return Waehrung.
   * @throws RemoteException
   */
  public String getWaehrung() throws RemoteException;
  
  /**
   * Legt die Waehrung fest.
   * @param waehrung
   * @throws RemoteException
   */
  public void setWaehrung(String waehrung) throws RemoteException;
}


/*********************************************************************
 * $Log: Mandant.java,v $
 * Revision 1.13  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.12  2006/01/04 00:53:48  willuhn
 * @B bug 166 Ausserplanmaessige Abschreibungen
 *
 * Revision 1.11  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.10  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.9  2005/08/29 12:17:28  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.8  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.7  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.6  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.4  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.3  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 * Revision 1.2  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/