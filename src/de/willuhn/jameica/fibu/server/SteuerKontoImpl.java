/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/SteuerKontoImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/12/11 21:00:34 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.sql.Connection;

import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;

/**
 * Diese Klasse ist nur fuer Steuersammelkonten zustaendig.
 * @author willuhn
 */
public class SteuerKontoImpl extends KontoImpl implements SteuerKonto
{

  /**
   * Erzeugt ein neues Steuersammelkonto.
   * @param conn
   * @param id
   * @throws RemoteException
   */
  public SteuerKontoImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }
  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    return super.getListQuery() + " and kontoart="+Kontoart.KONTOART_STEUER;
  }

}

/*********************************************************************
 * $Log: SteuerKontoImpl.java,v $
 * Revision 1.2  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 **********************************************************************/