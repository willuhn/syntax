/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Buchungstemplate.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/02 15:18:29 $
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
 * Interface fuer Buchungs-Templates.
 */
public interface Buchungstemplate extends Transfer
{
  
  /**
   * Liefert den Namen des Templates.
   * @return Name des Templates.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
  
  /**
   * Speichert den Namen des Templates.
   * @param name Name des Templates.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;

}


/*********************************************************************
 * $Log: Buchungstemplate.java,v $
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/