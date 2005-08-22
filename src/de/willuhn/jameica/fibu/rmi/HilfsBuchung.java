/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/HilfsBuchung.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/22 23:13:26 $
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
 * Auf jeden Fall ist es immer eine Buchung, die allein nicht
 * existieren kann sondern Bestandteil einer anderen Buchung ist.
 * @author willuhn
 */
public interface HilfsBuchung extends BaseBuchung
{

  /**
   * Liefert die Haupt-Buchung zu der diese Hilfs-Buchung gehoert.
   * @return Haupt-Buchung.
   * @throws RemoteException
   */
  public Buchung getHauptBuchung() throws RemoteException;
  
  /**
   * Definiert die Haupt-Buchung, zu der diese Hilfs-Buchung gehoert.
   * @param buchung Haupt-Buchung.
   * @throws RemoteException
   */
  public void setHauptBuchung(Buchung buchung) throws RemoteException;

}

/*********************************************************************
 * $Log: HilfsBuchung.java,v $
 * Revision 1.4  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2003/12/16 02:27:32  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/