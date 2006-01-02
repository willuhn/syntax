/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractUserObjectImpl.java,v $
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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.UserObject;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public abstract class AbstractUserObjectImpl extends AbstractDBObject implements UserObject
{
  private I18N i18n = null;
  
  /**
   * Erzeugt ein neues User-Objekt.
   * @throws RemoteException
   */
  public AbstractUserObjectImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("mandant_id".equals(field))
      return Mandant.class;
    return null;
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    try
    {
      if (!isUserObject())
        throw new ApplicationException("Datensatz gehört zum initialen Datenbestand und darf daher nicht gelöscht werden.");
    }
    catch (RemoteException e)
    {
      Logger.error("unable to check user object",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Datensatzes"));
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try {
      if (!isUserObject())
        throw new ApplicationException("Datensatz gehört zum initialen Datenbestand und darf daher nicht geändert werden.");
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(i18n.tr("Fehler bei der Überprüfung des Datensatzes"),e);
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
   * @see de.willuhn.jameica.fibu.rmi.UserObject#isUserObject()
   */
  public boolean isUserObject() throws RemoteException
  {
    return getMandant() != null;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.UserObject#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.UserObject#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant mandant) throws RemoteException
  {
    if (!this.isNewObject())
      throw new RemoteException("Datensatz gehört zum initialen Datenbestand und darf daher nicht geändert werden.");
    setAttribute("mandant_id",mandant);
  }
}

/*********************************************************************
 * $Log: AbstractUserObjectImpl.java,v $
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/