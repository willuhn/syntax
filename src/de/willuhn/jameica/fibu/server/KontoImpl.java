/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontoImpl.java,v $
 * $Revision: 1.21 $
 * $Date: 2005/08/12 00:10:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;

import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Implementierung des generischen Kontos.
 */
public class KontoImpl extends AbstractBaseKontoImpl implements Konto
{

  /**
   * @throws RemoteException
   */
  public KontoImpl() throws RemoteException
  {
    super();
  }

}


/*********************************************************************
 * $Log: KontoImpl.java,v $
 * Revision 1.21  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 **********************************************************************/