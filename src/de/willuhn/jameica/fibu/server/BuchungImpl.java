/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/20 03:48:44 $
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
import java.util.Date;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.rmi.AbstractDBObject;

/**
 * @author willuhn
 */
public class BuchungImpl extends AbstractDBObject implements Buchung
{

  /**
   * Erzeugt eine neue Buchung oder gibt laedt eine existierende.
   * @param conn Die Connection - reichen wir einfach durch ;).
   * @param id die optional zu ladende Buchung oder null.
   * @throws RemoteException
   */
  public BuchungImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "buchung";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getDatum()
   */
  public Date getDatum() throws RemoteException
  {
    return (Date) getField("datum");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) Application.getDefaultDatabase().createObject(Konto.class, (String) getField("konto_id"));
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "datum";
  }

}

/*********************************************************************
 * $Log: BuchungImpl.java,v $
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/