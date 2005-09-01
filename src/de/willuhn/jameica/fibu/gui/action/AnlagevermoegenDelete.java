/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AnlagevermoegenDelete.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/09/01 16:34:45 $
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
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Anlagevermoegens.
 */
public class AnlagevermoegenDelete extends BaseAction
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

    if (context == null || !(context instanceof Anlagevermoegen))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    try
    {
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Anlage-Gegenstand wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie diesen Anlage-Gegenstand wirklich löschen?\n" +
                        "Hierbei werden auch alle zugehörigen Abschreibungsbuchungen gelöscht."));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      ((Anlagevermoegen)context).delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Anlage-Gegenstand gelöscht"));
    }
    catch (Exception e)
    {
      Logger.error("unable to delete anlagevermoegen",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Daten des Anlage-Gegenstandes"));
    }
  }

}


/*********************************************************************
 * $Log: AnlagevermoegenDelete.java,v $
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/