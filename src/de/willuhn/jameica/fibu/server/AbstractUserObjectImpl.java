/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractUserObjectImpl.java,v $
 * $Revision: 1.7 $
 * $Date: 2011/03/25 10:14:10 $
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
import de.willuhn.jameica.fibu.Settings;
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
      if (!canChange())
        throw new ApplicationException("Datensatz gehört zum System-Kontenrahmen und darf daher nicht gelöscht werden.");
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
      if (!canChange())
        throw new ApplicationException("Datensatz gehört zum System-Kontenrahmen und darf daher nicht geändert werden.");
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
   * @see de.willuhn.jameica.fibu.rmi.UserObject#canChange()
   */
  public boolean canChange() throws RemoteException
  {
    return Settings.getSystemDataWritable() || isUserObject() || Settings.inUpdate();
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
    if (!this.isNewObject() && !this.canChange())
      throw new RemoteException("Datensatz gehört zum initialen Datenbestand und darf daher nicht geändert werden.");
    setAttribute("mandant_id",mandant);
  }
}

/*********************************************************************
 * $Log: AbstractUserObjectImpl.java,v $
 * Revision 1.7  2011/03/25 10:14:10  willuhn
 * @N Loeschen von Mandanten und Beruecksichtigen der zugeordneten Konten und Kontenrahmen
 * @C BUGZILLA 958
 *
 * Revision 1.6  2010-06-01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.5  2009/09/03 14:31:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.2  2006/12/27 14:42:23  willuhn
 * @N Update fuer MwSt.-Erhoehung
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/