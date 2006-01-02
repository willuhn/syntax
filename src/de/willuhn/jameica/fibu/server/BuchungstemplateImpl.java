/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungstemplateImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/02 15:18:29 $
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

import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Buchungs-Templates.
 */
public class BuchungstemplateImpl extends AbstractTransferImpl implements Buchungstemplate
{
  /**
   * ct.
   * @throws RemoteException
   */
  public BuchungstemplateImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "buchungstemplate";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      if (getName() == null || getName().length() == 0)
        throw new ApplicationException(i18n.tr("Bitten geben Sie eine Bezeichnung an."));
    }
    catch (RemoteException e)
    {
      Logger.error("unable to check template",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Vorlage"));
    }
    super.insertCheck();
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    insertCheck();
  }
  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }
}


/*********************************************************************
 * $Log: BuchungstemplateImpl.java,v $
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/