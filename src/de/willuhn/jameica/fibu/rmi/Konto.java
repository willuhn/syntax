/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Konto.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/11/21 02:10:57 $
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

}

/*********************************************************************
 * $Log: Konto.java,v $
 * Revision 1.2  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/