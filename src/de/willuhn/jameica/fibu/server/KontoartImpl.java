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
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class KontoartImpl extends AbstractDBObject implements Kontoart
{

  /**
   * Erzeugt eine neue Kontoart.
   * @throws RemoteException
   */
  public KontoartImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "kontoart";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontoart#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontoart#getKontoArt()
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

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontoart#isSteuerpflichtig()
   */
  public boolean isSteuerpflichtig() throws RemoteException
  {
		return (getKontoArt() == Kontoart.KONTOART_ERLOES ||
						getKontoArt() == Kontoart.KONTOART_AUFWAND ||
						getKontoArt() == Kontoart.KONTOART_ANLAGE);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#delete()
   */
  @Override
  public void delete() throws RemoteException, ApplicationException
  {
    super.delete();
    Cache.clear(Kontoart.class);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#store()
   */
  @Override
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    Cache.clear(Kontoart.class);
  }

}
