/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/BaseAction.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/22 23:13:26 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Basis-Action, um sicherzustellen, dass der Mandant existiert.
 */
public abstract class BaseAction implements Action
{
  private boolean fa = false;
  private boolean ma = false;

  private I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Prueft, ob ein Mandant angelegt ist.
   * @return true, wenn er angelegt ist.
   * @throws ApplicationException
   */
  boolean check() throws ApplicationException
  {
    if (fa && ma)
      return true;

    try
    {
      if (!fa)
      {
        DBIterator list = Settings.getDBService().createList(Finanzamt.class);
        fa = (list.size() > 0);
      }

      if (!ma)
      {
        DBIterator list = Settings.getDBService().createList(Mandant.class);
        ma = (list.size() > 0);
      }
      return fa & ma;
    }
    catch (RemoteException e)
    {
      Logger.error("unable to run check",e);
      throw new ApplicationException(i18n.tr("Fehler beim Pr�fen des Mandanten"),e);
    }
  }

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (fa && ma)
      return;
    
    if (!fa)
    {
      new FinanzamtNeu().handleAction(null);
      GUI.getView().setErrorText(i18n.tr("Bitte legen Sie zun�chst ein Finanzamt an"));
      return;
    }
    
    if (!ma)
    {
      new MandantNeu().handleAction(null);
      GUI.getView().setErrorText(i18n.tr("Bitte legen Sie zun�chst einen Mandanten an"));
      return;
    }
  }

}


/*********************************************************************
 * $Log: BaseAction.java,v $
 * Revision 1.4  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.2  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 **********************************************************************/