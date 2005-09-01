/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AnfangsbestandDelete.java,v $
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
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Anfangsbestandes.
 */
public class AnfangsbestandDelete extends BaseAction
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

    if (context == null || !(context instanceof Anfangsbestand))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    try
    {
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Anfangsbestand wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie diesen Anfangsbestand wirklich löschen?"));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      ((Anfangsbestand)context).delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Anfangsbestand gelöscht"));
    }
    catch (Exception e)
    {
      Logger.error("unable to delete anfangsbestand",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Daten des Anfangsbestandes"));
    }
  }

}


/*********************************************************************
 * $Log: AnfangsbestandDelete.java,v $
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/