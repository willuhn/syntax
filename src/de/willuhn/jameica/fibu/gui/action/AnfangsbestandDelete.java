/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AnfangsbestandDelete.java,v $
 * $Revision: 1.5 $
 * $Date: 2011/05/11 10:38:51 $
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
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Anfangsbestandes.
 */
public class AnfangsbestandDelete implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
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
    catch (OperationCanceledException oce)
    {
      Logger.info(oce.getMessage());
      return;
    }
    catch (Exception e)
    {
      Logger.error("unable to delete anfangsbestand",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Anfangsbestandes"));
    }
  }

}


/*********************************************************************
 * $Log: AnfangsbestandDelete.java,v $
 * Revision 1.5  2011/05/11 10:38:51  willuhn
 * @N OCE fangen
 *
 * Revision 1.4  2006-05-29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.3  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/