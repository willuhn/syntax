/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Buchung.java,v $
 * $Revision: 1.17 $
 * $Date: 2006/05/08 15:41:57 $
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
   * Liefert true, wenn die Buchung geprueft wurde.
   * @return true, wenn sie geprueft wurde.
   * @throws RemoteException
   */
  public boolean isGeprueft() throws RemoteException;

  /**
   * Legt fest, ob die Buchung als geprueft gelten soll.
   * @param b true, wenn sie als geprueft gelten soll.
   * @throws RemoteException
   */
  public void setGeprueft(boolean b) throws RemoteException;
  
  /**
   * Falls mit dieser Buchung ein Anlagegut erzeugt wurde, liefert es die Funktion.
   * @return meist <code>null</code> oder das Anlagegut, wenn es zusammen mit der Buchung
   * angelegt wurde.
   * @throws RemoteException
   */
  public Anlagevermoegen getAnlagevermoegen() throws RemoteException;

}


/*********************************************************************
 * $Log: Buchung.java,v $
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