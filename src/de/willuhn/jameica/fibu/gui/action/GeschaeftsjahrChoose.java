/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/GeschaeftsjahrChoose.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2008/07/03 10:37:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.dialogs.GeschaeftsjahrAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Diese Action oeffnet einen Dialog zur Auswahl des Geschaeftsjahres.
 * @author willuhn
 */
public class GeschaeftsjahrChoose implements Action
{

  
  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    GeschaeftsjahrAuswahlDialog d = new GeschaeftsjahrAuswahlDialog(AbstractDialog.POSITION_CENTER);
    try
    {
      Geschaeftsjahr jahr = (Geschaeftsjahr) d.open();
      if (jahr != null)
      {
        Settings.setActiveGeschaeftsjahr(jahr);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Geschäftsjahr ausgewählt"),StatusBarMessage.TYPE_SUCCESS));
      }
      else
      {
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Kein Geschäftsjahr ausgewählt"),StatusBarMessage.TYPE_ERROR));
      }
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (OperationCanceledException oce)
    {
      Logger.debug("operation cancelled");
    }
    catch (Exception e)
    {
      Logger.error("unable to choose geschaeftsjahr",e);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler bei der Auswahl des Geschäftsjahres"),StatusBarMessage.TYPE_ERROR));
    }
    
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrChoose.java,v $
 * Revision 1.1.2.1  2008/07/03 10:37:08  willuhn
 * @N Effektivere Erzeugung neuer Buchungsnummern
 * @B Nach Wechsel des Geschaeftsjahres nicht Dialog "Geschaeftsjahr bearbeiten" oeffnen
 *
 * Revision 1.1  2006/06/29 15:11:31  willuhn
 * @N Setup-Wizard fertig
 * @N Auswahl des Geschaeftsjahres
 *
 *********************************************************************/