/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/GeldKontoImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/05 17:11:58 $
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

/**
 * Diese Klasse ist nur fuer Konten des Typs "Geld-Konto" zustaendig.
 * @author willuhn
 */
public class GeldKontoImpl extends KontoImpl implements GeldKonto
{

  /**
   * Erzeugt ein neues Geld-Konto.
   * @param conn
   * @param id
   * @throws RemoteException
   */
  public GeldKontoImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }
  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    return super.getListQuery() + " and kontoart="+Kontoart.KONTOART_GELD;
  }

}

/*********************************************************************
 * $Log: GeldKontoImpl.java,v $
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/