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

/**
 * Datenbank-Service des Fibu-Plugins.
 */
public interface DBService extends de.willuhn.datasource.rmi.DBService
{
  /**
   * Teilt dem Server mit, welche Geschaeftsjahr der Client gerade bearbeitet.
   * @param jahr das Geschaeftsjahr des Clients.
   * @throws RemoteException
   */
  public void setActiveGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException;
  
  /**
   * Liefert das Geschaeftsjahr, welches der Client gerade bearbeitet.
   * @return Geschaeftsjahr.
   * @throws RemoteException
   */
  public Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException;
  
  /**
   * Liefert den Namen der SQL-Funktion, mit der die Datenbank aus einem DATE-Feld einen UNIX-Timestamp macht.
   * Bei MySQL ist das z.Bsp. "UNIX_TIMESTAMP" und bei McKoi schlicht "TONUMBER".
   * @param content der Feld-Name.
   * @return Name der SQL-Funktion.
   * @throws RemoteException
   */
  public String getSQLTimestamp(String content) throws RemoteException;
  
  /**
   * Fuehrt ein SQL-Update-Statement aus.
   * @param sql das Statement.
   * @param params die Parameter zur Erzeugung des PreparedStatements.
   * @throws RemoteException
   */
  public void executeUpdate(String sql, Object[] params) throws RemoteException;
  
}


/*********************************************************************
 * $Log: DBService.java,v $
 * Revision 1.7  2006/12/27 15:23:33  willuhn
 * @C merged update 1.3 and 1.4 to 1.3
 *
 * Revision 1.6  2006/09/05 20:57:27  willuhn
 * @ResultsetIterator merged into datasource lib
 *
 * Revision 1.5  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.4  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.3  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.2  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/