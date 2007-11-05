/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/DBSupport.java,v $
 * $Revision: 1.6 $
 * $Date: 2007/11/05 01:02:46 $
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
   * Die Funktion darf durchaus Fehler werfen oder den User fragen, wenn
   * die Datenbank schon existiert oder notwendige Daten fehlen.
   * @param monitor Monitor, ueber den die Erstellung der Datenbank beobachtet werden kann.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public void create(ProgressMonitor monitor) throws RemoteException,ApplicationException;
  
  /**
   * Speichert die Einstellungen.
   * @throws RemoteException
   */
  public void store() throws RemoteException;
  
  /**
   * Liefert die JDBC-URL.
   * @return duie JDBC-URL.
   * @throws RemoteException
   */
  public String getJdbcUrl() throws RemoteException;  
  
  /**
   * Liefert den JDBC-Treiber.
   * @return der JDBC-Treiber.
   * @throws RemoteException
   */
  public String getJdbcDriver() throws RemoteException;
  
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

  /**
   * Liefert den Namen der SQL-Funktion, mit der die Datenbank aus einem DATE-Feld einen UNIX-Timestamp macht.
   * Bei MySQL ist das z.Bsp. "UNIX_TIMESTAMP" und bei McKoi schlicht "TONUMBER".
   * @param content der Feld-Name.
   * @return Name der SQL-Funktion.
   * @throws RemoteException
   */
  public String getSQLTimestamp(String content) throws RemoteException;  
  
  /**
   * Liefert das Transaction-Isolation-Level.
   * @see de.willuhn.datasource.db.DBServiceImpl#getTransactionIsolationLevel()
   */
  public int getTransactionIsolationLevel() throws RemoteException;

}


/*********************************************************************
 * $Log: DBSupport.java,v $
 * Revision 1.6  2007/11/05 01:02:46  willuhn
 * @C Transaction-Isolation-Level in SynTAX
 *
 * Revision 1.5  2006/12/27 15:58:08  willuhn
 * @R removed unused method
 *
 * Revision 1.4  2006/11/17 00:11:20  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 **********************************************************************/