/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.rmi.Kontotyp;

/**
 * @author willuhn
 */
public class KontotypImpl extends AbstractDBObject implements Kontotyp
{

  /**
   * Erzeugt einen neuen Kontotyp.
   * @throws RemoteException
   */
  public KontotypImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "kontotyp";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontotyp#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontotyp#getKontoTyp()
   */
  public int getKontoTyp() throws RemoteException
  {
    try {
      return Integer.parseInt(getID());
    }
    catch (NumberFormatException ne)
    {
      return KONTOTYP_UNGUELTIG;
    }
  }
}

/*********************************************************************
 * $Log: KontotypImpl.java,v $
 * Revision 1.1  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 **********************************************************************/