/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Attic/KontenrahmenObject.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/02/22 10:41:41 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBObject;

/**
 * Basisinterface fuer alle Objekte, die an einem Kontenrahmen haengen.
 */
public interface KontenrahmenObject extends DBObject
{
  /**
   * Speichert den Kontenrahmen.
   * @param kontenrahmen
   * @throws RemoteException
   */
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException;
  
  /**
   * Liefert den Kontenrahmen.
   * @return der Kontenrahmen.
   * @throws RemoteException
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException;
}


/*********************************************************************
 * $Log: KontenrahmenObject.java,v $
 * Revision 1.1  2008/02/22 10:41:41  willuhn
 * @N Erweiterte Mandantenfaehigkeit (IN PROGRESS!)
 *
 **********************************************************************/