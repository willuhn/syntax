/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Konto.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/20 03:48:44 $
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

/**
 * Diese Klasse bildet die Konten in Fibu ab.
 * @author willuhn
 */
public interface Konto
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
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/