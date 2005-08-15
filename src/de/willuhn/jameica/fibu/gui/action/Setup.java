/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/Setup.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/15 23:38:27 $
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
 * Action fuers Setup.
 */
public class Setup implements Action
{
  private boolean ok = false;

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (ok)
      return;
    
    try
    {
      Logger.info("starting setup");
      
      Logger.info("checking if finanzamt exists");
      
      I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
      
      DBIterator list = Settings.getDBService().createList(Finanzamt.class);
      if (list.size() == 0)
      {
        new FinanzamtNeu().handleAction(null);
        GUI.getView().setErrorText(i18n.tr("Bitte legen Sie zunächst ein Finanzamt an"));
        return;
      }

      list = Settings.getDBService().createList(Mandant.class);
      if (list.size() == 0)
      {
        new MandantNeu().handleAction(null);
        GUI.getView().setErrorText(i18n.tr("Bitte legen Sie zunächst einen Mandanten an"));
      }
      ok = true;
    }
    catch (RemoteException e)
    {
      Logger.error("unable to run setup",e);
    }
  }

}


/*********************************************************************
 * $Log: Setup.java,v $
 * Revision 1.1  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 **********************************************************************/