/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/GeldKontoImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/01/29 00:13:19 $
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

import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.rmi.Kontoart;

/**
 * Diese Klasse ist nur fuer Konten des Typs "Geld-Konto" zustaendig.
 * @author willuhn
 */
public class GeldKontoImpl extends KontoImpl implements GeldKonto
{

  /**
   * Erzeugt ein neues Geld-Konto.
   * @throws RemoteException
   */
  public GeldKontoImpl() throws RemoteException
  {
    super();
  }
  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    return super.getListQuery() + " and kontoart_id="+Kontoart.KONTOART_GELD;
  }

}

/*********************************************************************
 * $Log: GeldKontoImpl.java,v $
 * Revision 1.4  2004/01/29 00:13:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.2  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/