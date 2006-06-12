/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/DBSupport.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 23:05:47 $
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

import de.willuhn.datasource.GenericObject;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Basis-Interface fuer alle unterstuetzten Datenbanken.
 * Klassen, die dieses Interface implementieren, werden automatisch erkannt.
 */
public interface DBSupport extends GenericObject
{
  /**
   * Liefert den Namen der Datenbank.
   * @return Name der Datenbank.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
  
  /**
   * Erstellt die Datenbank.
   * @param monitor Monitor, ueber den die Erstellung der Datenbank beobachtet werden kann.
   * @throws ApplicationException
   */
  public void create(ProgressMonitor monitor) throws ApplicationException;
  
  /**
   * Liefert true, wenn die Datenbank einen Usernamen braucht.
   * @return true, wenn die Datenbank einen Usernamen braucht.
   * @throws RemoteException
   */
  public boolean needsUsername() throws RemoteException;
  
  /**
   * Liefert true, wenn die Datenbank ein Passwort braucht.
   * @return true, wenn die Datenbank ein Passwort braucht.
   * @throws RemoteException
   */
  public boolean needsPassword() throws RemoteException;
  
  /**
   * Liefert true, wenn die Datenbank einen Hostnamen braucht.
   * @return true, wenn die Datenbank einen Hostnamen braucht.
   * @throws RemoteException
   */
  public boolean needsHostname() throws RemoteException;
  
  /**
   * Liefert true, wenn die Datenbank einen TCP-Port braucht.
   * @return true, wenn die Datenbank einen TCP-Port braucht.
   * @throws RemoteException
   */
  public boolean needsTcpPort() throws RemoteException;
  
  /**
   * Liefert true, wenn ein Datenbankname gebraucht wird.
   * @return true, wenn ein Datenbankname gebraucht wird.
   * @throws RemoteException
   */
  public boolean needsDatabaseName() throws RemoteException;
  
  /**
   * Speichert den Usernamen.
   * @param username
   * @throws RemoteException
   */
  public void setUsername(String username) throws RemoteException;
  
  /**
   * Speichert das Passwort.
   * @param password
   * @throws RemoteException
   */
  public void setPassword(String password) throws RemoteException;
  
  /**
   * Speichert den Hostnamen.
   * @param hostname
   * @throws RemoteException
   */
  public void setHostname(String hostname) throws RemoteException;
  
  /**
   * Speichert den TCP-Port.
   * @param port
   * @throws RemoteException
   */
  public void setTcpPort(int port) throws RemoteException;
  
  /**
   * Speichert den Datenbank-Namen.
   * @param name
   * @throws RemoteException
   */
  public void setDatabaseName(String name) throws RemoteException;

  /**
   * Liefert den Usernamen.
   * @return der Username.
   * @throws RemoteException
   */
  public String getUsername() throws RemoteException;
  
  /**
   * Liefert das Passwort.
   * @return das Passwort.
   * @throws RemoteException
   */
  public String getPassword() throws RemoteException;
  
  /**
   * Liefert den Hostnamen.
   * @return der Hostname.
   * @throws RemoteException
   */
  public String getHostname() throws RemoteException;
  
  /**
   * Liefert den TCP-Port.
   * @return der TCP-Port.
   * @throws RemoteException
   */
  public int getTcpPort() throws RemoteException;
  
  /**
   * Liefert den Namen der Datenbank.
   * @return Name der Datenbank.
   * @throws RemoteException
   */
  public String getDatabaseName() throws RemoteException;
  
}


/*********************************************************************
 * $Log: DBSupport.java,v $
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/