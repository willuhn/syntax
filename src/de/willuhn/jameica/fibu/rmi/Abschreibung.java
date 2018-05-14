/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBObject;

/**
 * Interface fuer eine einzelne Abschreibungsbuchung.
 */
public interface Abschreibung extends DBObject
{
  /**
   * Liefert das zu dieser Abschreibung gehoerende Anlagevermoegen.
   * @return Anlagevermoegen.
   * @throws RemoteException
   */
  public Anlagevermoegen getAnlagevermoegen() throws RemoteException;
  
  /**
   * Liefert die zugehoerige Buchung.
   * @return Buchung.
   * @throws RemoteException
   */
  public AbschreibungsBuchung getBuchung() throws RemoteException;
  
  /**
   * Speichert das Anlagevermoegen zu dieser Abschreibung.
   * @param av Anlagevermoegen.
   * @throws RemoteException
   */
  public void setAnlagevermoegen(Anlagevermoegen av) throws RemoteException;
  
  /**
   * Speichert die Buchung der Abschreibung.
   * @param b Buchung.
   * @throws RemoteException
   */
  public void setBuchung(AbschreibungsBuchung b) throws RemoteException;
  
  /**
   * Prueft, ob es eine manuelle Sonderabschreibung ist.
   * @return true, wenn es eine Sonderabschreibung ist.
   * @throws RemoteException
   */
  public boolean isSonderabschreibung() throws RemoteException;
  
  /**
   * Legt fest, ob es eine Sonderabschreibung ist.
   * @param b true, wenn es eine Sonderabschreibung ist.
   * @throws RemoteException
   */
  public void setSonderabschreibung(boolean b) throws RemoteException;
  
}


/*********************************************************************
 * $Log: Abschreibung.java,v $
 * Revision 1.5  2006/01/08 15:28:41  willuhn
 * @N Loeschen von Sonderabschreibungen
 *
 * Revision 1.4  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.1  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/