/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Mandant.java,v $
 * $Revision: 1.5 $
 * $Date: 2003/12/11 21:00:35 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.jameica.rmi.DBObject;

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
   * Liefert den ausgewaehlten Kontenrahmen des Mandanten.
   * @return Kontenrahmen des Mandanten.
   * @throws RemoteException
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException;


  /**
   * Liefert das zugehoerige Finanzamt.
   * @return das Finanzamt des Mandanten.
   * @throws RemoteException
   */
  public Finanzamt getFinanzamt() throws RemoteException;
  
  /**
   * Liefert das Geschaeftsjahr oder das aktuelle wenn noch keins definiert ist.
   * @return das aktuelle Geschaeftsjahr.
   * @throws RemoteException
   */
  public int getGeschaeftsjahr() throws RemoteException;

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
   * Speichert den ausgewaehlten Kontenrahmen des Mandanten.
   * @param kontenrahmen Kontenrahmen des Mandanten.
   * @throws RemoteException
   */
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException;

  /**
   * Speichert das Finanzamt des Mandanten.
   * @param finanzamt Finanzamt des Mandanten.
   * @throws RemoteException
   */
  public void setFinanzamt(Finanzamt finanzamt) throws RemoteException;

  /**
   * Speichert das aktuelle Geschaeftsjahr.
   * @param das aktuelle Geschaeftsjahr.
   * @throws RemoteException
   */
  public void setGeschaeftsjahr(int jahr) throws RemoteException;

  /**
   * Prueft, ob der aktuelle Mandant aktiv ist und somit nicht geloescht werden kann.
   * @return true, wenn er aktiv ist.
   * @throws RemoteException
   */
  public boolean isActive() throws RemoteException;

}


/*********************************************************************
 * $Log: Mandant.java,v $
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