/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontoImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/11/22 20:43:07 $
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
import java.sql.ResultSet;
import java.sql.Statement;

import de.willuhn.jameica.rmi.AbstractDBObject;

/**
 * @author willuhn
 */
public class KontoImpl extends AbstractDBObject implements Konto
{

  /**
   * @param conn
   * @param id
   * @throws RemoteException
   */
  public KontoImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "konto";
  }
  
  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getKontonummer()
   */
  public String getKontonummer() throws RemoteException
  {
    return (String) getField("kontonummer");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "kontonummer";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getSaldo()
   */
  public double getSaldo() throws RemoteException
  {
    try {
      // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
      if ("".equals(getID()) || getID() == null)
        return 0;

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select sum(betrag) as betrag from buchung where konto_id = " + Integer.parseInt(getID()));
      rs.next();
      return rs.getDouble("betrag");
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to get saldo.",e);
    }
    
  }

}

/*********************************************************************
 * $Log: KontoImpl.java,v $
 * Revision 1.2  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/