/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Konto.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/11/22 20:43:07 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.objects;

import java.rmi.RemoteException;

import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse bildet die Konten in Fibu ab.
 * @author willuhn
 */
public interface Konto extends DBObject
{

  /**
   * Liefert die Kontonummer.
   * @return
   * @throws RemoteException
   */
  public String getKontonummer() throws RemoteException;

  /**
   * Liefert den Saldo des Kontos.
   * @return Saldo.
   * @throws RemoteException
   */
  public double getSaldo() throws RemoteException;

}

/*********************************************************************
 * $Log: Konto.java,v $
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