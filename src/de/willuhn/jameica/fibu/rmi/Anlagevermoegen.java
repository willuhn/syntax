/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Anlagevermoegen.java,v $
 * $Revision: 1.9 $
 * $Date: 2006/01/04 16:04:33 $
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
  public Date getAnschaffungsdatum() throws RemoteException;
  
  /**
   * Speichert das Anschaffungsdatum.
   * @param d Anschaffungsdatum.
   * @throws RemoteException
   */
  public void setAnschaffungsDatum(Date d) throws RemoteException;
  
  /**
   * Liefert die Nutzungsdauer fuer die Abschreibung in Jahren.
   * @return Laufzeit in Jahren.
   * @throws RemoteException
   */
  public int getNutzungsdauer() throws RemoteException;
  
  /**
   * Speichert die Nutzungsdauer fuer die Abschreibung in Jahren.
   * @param dauer Nutzungsdauer in Jahren.
   * @throws RemoteException
   */
  public void setNutzungsdauer(int dauer) throws RemoteException;
  
  /**
   * Liefert den aktuellen Restwert des Anlagegutes.
   * Wurde im Geschaeftsjahr noch keine Abschreibungsbuchung vorgenommen,
   * entspricht der Restwert dem Anfangsbestand.
   * @param jahr das Geschaeftsjahr.
   * @return Restwert.
   * @throws RemoteException
   */
  public double getRestwert(Geschaeftsjahr jahr) throws RemoteException;
  
  /**
   * Liefert die Abschreibung des Jahres insofern es abgeschlossen wurde.
   * @param jahr das Geschaeftsjahr.
   * @return Abschreibung des Geschaeftsjahres oder <code>0.0</code>.
   * @throws RemoteException
   */
  public double getJahresAbschreibung(Geschaeftsjahr jahr) throws RemoteException;

  /**
   * Liefert den Anfangsbestand der Anlage.
   * Das entspricht den Anschaffungskosten abzueglich aller Abschreibungen der
   * Vorjahre und somit dem Restwert <b>vor</b> der aktuellen Abschreibung.
   * @param jahr das Geschaeftsjahr.
   * @return Anfangsbestand im Jahr.
   * @throws RemoteException
   */
  public double getAnfangsbestand(Geschaeftsjahr jahr) throws RemoteException;

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
   * Liefert die Abschreibungsbuchungen zu diesem Anlagevermoegen bis zum genannten Jahr.
   * @param jahr das Geschaeftsjahr.
   * @return Abschreibungsbuchungen.
   * @throws RemoteException
   */
  public GenericIterator getAbschreibungen(Geschaeftsjahr jahr) throws RemoteException;
  
  /**
   * Prueft, ob das Anlagegut noch geaendert werden darf.
   * Sowie bereits Abschreibungen vorliegen, duerfen abschreibungsrelevante
   * Daten nicht mehr geaendert werden.
   * @return true, wenn das Anlagevermoegen nocht geaendert werden darf.
   * @throws RemoteException
   */
  public boolean canChange() throws RemoteException;
  
  /**
   * Liefert das Bestands-Konto des Anlage-Gegenstandes.
   * @return Konto.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException;
  
  /**
   * Speichert das Konto.
   * @param k Konto.
   * @throws RemoteException
   */
  public void setKonto(Konto k) throws RemoteException;
  
  /**
   * Liefert das Konto, auf dem die Abschreibungen gebucht werden sollen.
   * @return Abschreibungskonto.
   * @throws RemoteException
   */
  public Konto getAbschreibungskonto() throws RemoteException;
  
  /**
   * Speichert das Konto, auf dem die Abschreibungen gebucht werden sollen.
   * @param k Abschreibungskonto.
   * @throws RemoteException
   */
  public void setAbschreibungskonto(Konto k) throws RemoteException;
  
  /**
   * Legt den Restwert der Anlage fest.
   * Die manuelle Festlegung des Restwertes ist nur dann erlaubt, wenn:
   * <ol>
   *   <li>Noch keine Abschreibungen vorliegen</li>
   *   <li>sich das Anschaffungsdatum vor dem aktuellen Geschaeftsjahr befindet</li>
   * </ol>
   * Hiermit lassen sich bereits existierende Anlagegueter aus anderen
   * Fibu-Programmen uebernehmen, die bereits vorher existierten und
   * schon Abschreibungen in dem vorherigen Fibu-Programm vorlagen.
   * @param restwert
   * @throws RemoteException
   */
  public void setRestwert(double restwert) throws RemoteException;
}


/*********************************************************************
 * $Log: Anlagevermoegen.java,v $
 * Revision 1.9  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.8  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.7  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.6  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/01 23:28:15  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.3  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 * Revision 1.1  2005/08/29 00:20:29  willuhn
 * @N anlagevermoegen
 *
 **********************************************************************/