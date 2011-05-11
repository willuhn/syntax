/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AbschreibungDelete.java,v $
 * $Revision: 1.2 $
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
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen einer Abschreibung.
 */
public class AbschreibungDelete implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Abschreibung))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    try
    {
      // Checken, ob die ueberhaupt geloescht werden darf
      Abschreibung a = (Abschreibung) context;
      
      if (!a.isSonderabschreibung())
        throw new ApplicationException(i18n.tr("Nur Sonderabschreibungen dürfen gelöscht werden"));
      
      AbschreibungsBuchung ab = a.getBuchung();
      if (ab.getGeschaeftsjahr().isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr der Abschreibungen ist bereits abgeschlossen"));

      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Abschreibung wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie diese Abschreibung wirklich löschen?"));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      a.delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Abschreibung gelöscht"));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info(oce.getMessage());
      return;
    }
    catch (Exception e)
    {
      Logger.error("unable to delete abschreibung",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Abschreibung"));
    }
  }

}


/*********************************************************************
 * $Log: AbschreibungDelete.java,v $
 * Revision 1.2  2011/05/11 10:38:51  willuhn
 * @N OCE fangen
 *
 * Revision 1.1  2006-05-29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 **********************************************************************/