/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/HilfsBuchung.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/12 00:10:59 $
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

/**
 * Bildet Hilfs-Buchungen ab. Das sind insb. Steuerbuchungen.
 * Auf jeden Fall ist es immer eine BaseBuchung, die allein nicht
 * existieren kann sondern Bestandteil einer anderen BaseBuchung ist.
 * @author willuhn
 */
public interface HilfsBuchung extends BaseBuchung
{

  /**
   * Liefert die Haupt-BaseBuchung zu der diese Hilfs-BaseBuchung gehoert.
   * @return Haupt-BaseBuchung.
   * @throws RemoteException
   */
  public BaseBuchung getHauptBuchung() throws RemoteException;
  
  /**
   * Definiert die Haupt-BaseBuchung, zu der diese Hilfs-BaseBuchung gehoert.
   * @param buchung Haupt-BaseBuchung.
   * @throws RemoteException
   */
  public void setHauptBuchung(BaseBuchung buchung) throws RemoteException;

}

/*********************************************************************
 * $Log: HilfsBuchung.java,v $
 * Revision 1.2  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2003/12/16 02:27:32  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/