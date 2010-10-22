/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Buchung.java,v $
 * $Revision: 1.20 $
 * $Date: 2010/10/22 11:47:30 $
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

/**
 * Basis-Interface von Buchungen.
 */
public interface Buchung extends BaseBuchung
{
  /**
   * Liefert eine Liste mit allen Hilfs-Buchungen, die zu dieser gehoeren.
   * @return Liste aller Hilfs-Buchungen dieser Buchung.
   * @throws RemoteException
   */
  public DBIterator getHilfsBuchungen() throws RemoteException;
  
  /**
   * Liefert den Brutto-Betrag (also incl. der Hilfsbuchungen).
   * @return Brutto-Betrag.
   * @throws RemoteException
   */
  public double getBruttoBetrag() throws RemoteException;
  
  /**
   * Speichert den Butto-Betrag.
   * @param d der Brutto-Betrag.
   * @throws RemoteException
   */
  public void setBruttoBetrag(double d) throws RemoteException;
  
  /**
   * Falls mit dieser Buchung ein Anlagegut erzeugt wurde, liefert es die Funktion.
   * @return meist <code>null</code> oder das Anlagegut, wenn es zusammen mit der Buchung
   * angelegt wurde.
   * @throws RemoteException
   */
  public Anlagevermoegen getAnlagevermoegen() throws RemoteException;

  /**
   * Liefert die optionale ID eines Hibiscus-Umsatzes.
   * @return optionale zugeordnete ID eines Hibiscus-Umsatzes.
   * @throws RemoteException
   */
  public String getHibiscusUmsatzID() throws RemoteException;
  
  /**
   * Speichert eine optionale Hibiscus-Umsatz-ID.
   * @param id die optionale Umsatz-ID.
   * @throws RemoteException
   */
  public void setHibiscusUmsatzID(String id) throws RemoteException;
}


/*********************************************************************
 * $Log: Buchung.java,v $
 * Revision 1.20  2010/10/22 11:47:30  willuhn
 * @B Keine Doppelberechnung mehr in der Buchungserfassung (brutto->netto->brutto)
 *
 * Revision 1.19  2006-10-09 23:48:41  willuhn
 * @B bug 140
 *
 * Revision 1.18  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.17  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 * Revision 1.16  2005/09/05 13:47:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 **********************************************************************/