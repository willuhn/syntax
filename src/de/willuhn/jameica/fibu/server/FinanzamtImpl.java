/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/FinanzamtImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/25 00:22:17 $
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
import java.sql.Connection;

import de.willuhn.jameica.rmi.AbstractDBObject;

/**
 * @author willuhn
 */
public class FinanzamtImpl extends AbstractDBObject implements Finanzamt
{

  /**
   * ct.
   * @param conn
   * @param id
   * @throws RemoteException
   */
  public FinanzamtImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "finanzamt";
  }

  /**
   * @see de.willuhn.jameica.rmi.DBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.rmi.DBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    return null;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getField("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getOrt()
   */
  public String getOrt() throws RemoteException
  {
    return (String) getField("ort");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getPLZ()
   */
  public String getPLZ() throws RemoteException
  {
    return (String) getField("plz");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getPostfach()
   */
  public String getPostfach() throws RemoteException
  {
    return (String) getField("postfach");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getStrasse()
   */
  public String getStrasse() throws RemoteException
  {
    return (String) getField("strasse");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setField("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setOrt(java.lang.String)
   */
  public void setOrt(String ort) throws RemoteException
  {
    setField("ort",ort);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setPLZ(java.lang.String)
   */
  public void setPLZ(String plz) throws RemoteException
  {
    setField("plz",plz);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setPostfach(java.lang.String)
   */
  public void setPostfach(String postfach) throws RemoteException
  {
    setField("postfach",postfach);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setStrasse(java.lang.String)
   */
  public void setStrasse(String strasse) throws RemoteException
  {
    setField("strasse",strasse);
  }

}

/*********************************************************************
 * $Log: FinanzamtImpl.java,v $
 * Revision 1.1  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/