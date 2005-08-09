/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/MandantDelete.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/09 23:53:34 $
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
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Mandanten.
 */
public class MandantDelete implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Mandant))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    try
    {
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Mandant wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie diesen Mandanten wirklich löschen?"));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      ((Mandant)context).delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Mandant gelöscht"));
    }
    catch (Exception e)
    {
      Logger.error("unable to delete mandant",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Daten des Mandanten"));
    }
  }

}


/*********************************************************************
 * $Log: MandantDelete.java,v $
 * Revision 1.2  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/