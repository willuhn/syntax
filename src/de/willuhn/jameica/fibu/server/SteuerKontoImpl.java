/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/SteuerKontoImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/01/29 01:18:06 $
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
   * @throws RemoteException
   */
  public SteuerKontoImpl() throws RemoteException
  {
    super();
  }
  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    return super.getListQuery() + " and kontoart_id="+Kontoart.KONTOART_STEUER;
  }

}

/*********************************************************************
 * $Log: SteuerKontoImpl.java,v $
 * Revision 1.4  2004/01/29 01:18:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.2  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 **********************************************************************/