/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontoartImpl.java,v $
 * $Revision: 1.3 $
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

import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.server.AbstractDBObject;

/**
 * @author willuhn
 */
public class KontoartImpl extends AbstractDBObject implements Kontoart
{

  /**
   * Laedt die Kontoart oder erzeugt eine neue.
   * @param conn Die Connection - reichen wir einfach durch ;).
   * @param id die optional zu ladende Buchung oder null.
   * @throws RemoteException
   */
  public KontoartImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "kontoart";
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getField("name");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    return null;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Kontoart#getKontoArt()
   */
  public int getKontoArt() throws RemoteException
  {
    try {
      return Integer.parseInt(getID());
    }
    catch (NumberFormatException ne)
    {
      return KONTOART_UNGUELTIG;
    }
  }

}

/*********************************************************************
 * $Log: KontoartImpl.java,v $
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/