/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/MandantImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/11/24 16:26:16 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.rmi.AbstractDBObject;

/**
 * @author willuhn
 * 24.11.2003
 */
public class MandantImpl extends AbstractDBObject implements Mandant
{

  /**
   * ct.
   * @param conn
   * @param id
   * @throws RemoteException
   */
  protected MandantImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "mandant";
  }

  /**
   * @see de.willuhn.jameica.rmi.DBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "firma";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    Integer krId = (Integer) getField("kr_id");
    if (krId != null)
      return (Kontenrahmen) Application.getDefaultDatabase().createObject(Kontenrahmen.class,krId.toString());

    throw new RemoteException("unable to determine kontenrahmen of this mandant");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getName1()
   */
  public String getName1() throws RemoteException
  {
    return (String) getField("name1");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getName2()
   */
  public String getName2() throws RemoteException
  {
    return (String) getField("name2");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getFirma()
   */
  public String getFirma() throws RemoteException
  {
    return (String) getField("firma");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getStrasse()
   */
  public String getStrasse() throws RemoteException
  {
    return (String) getField("strasse");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getPLZ()
   */
  public String getPLZ() throws RemoteException
  {
    return (String) getField("plz");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getOrt()
   */
  public String getOrt() throws RemoteException
  {
    return (String) getField("ort");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getSteuernummer()
   */
  public String getSteuernummer() throws RemoteException
  {
    return (String) getField("steuernummer");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    return null;
  }


}


/*********************************************************************
 * $Log: MandantImpl.java,v $
 * Revision 1.2  2003/11/24 16:26:16  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/