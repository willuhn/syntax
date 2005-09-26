/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/GeschaeftsjahrDelete.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/09/26 15:15:39 $
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
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Geschaeftsjahres.
 */
public class GeschaeftsjahrDelete implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Geschaeftsjahr))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    try
    {
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Geschäftsjahr wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie dieses Geschäftsjahr wirklich löschen?\n" +
                        "Hierbei werden auch die Buchungen und Anfangsbestände gelöscht."));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      ((Geschaeftsjahr)context).delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Geschäftsjahr gelöscht"));
    }
    catch (Exception e)
    {
      Logger.error("unable to delete gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Daten des Geschäftsjahres"));
    }
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrDelete.java,v $
 * Revision 1.4  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/