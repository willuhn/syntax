/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Buchung.java,v $
 * $Revision: 1.15 $
 * $Date: 2005/08/15 13:18:44 $
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
  

}


/*********************************************************************
 * $Log: Buchung.java,v $
 * Revision 1.15  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 **********************************************************************/