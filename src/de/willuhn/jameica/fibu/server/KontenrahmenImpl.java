/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontenrahmenImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2003/11/27 00:21:05 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.objects;

import java.rmi.RemoteException;
import java.sql.Connection;

import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.rmi.AbstractDBObject;

/**
 * @author willuhn
 * 24.11.2003
 */
public class KontenrahmenImpl extends AbstractDBObject implements Kontenrahmen
{

  /**
   * ct.
   * @param conn
   * @param id
   * @throws RemoteException
   */
  public KontenrahmenImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "kontenrahmen";
  }

  /**
   * @see de.willuhn.jameica.rmi.DBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    return null;
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  public void deleteCheck() throws ApplicationException
  {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    // TODO Auto-generated method stub
    
  }

}


/*********************************************************************
 * $Log: KontenrahmenImpl.java,v $
 * Revision 1.4  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.3  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.2  2003/11/24 16:26:16  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/