/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/SteuerDelete.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/16 17:39:24 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Steuersatzes.
 */
public class SteuerDelete extends BaseAction
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (!check())
    {
      super.handleAction(context);
      return;
    }

    if (context == null || !(context instanceof Steuer))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    try
    {
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Steuersatz wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie den Steuersatz wirklich löschen?"));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      ((Steuer)context).delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Steuersatz gelöscht"));
    }
    catch (Exception e)
    {
      Logger.error("unable to delete steuer",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Steuersatzes"));
    }
  }

}


/*********************************************************************
 * $Log: SteuerDelete.java,v $
 * Revision 1.4  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/