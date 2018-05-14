/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.dialogs.ImportDialog;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action, ueber die Buchungen importiert werden koennen.
 * Es wird kein Parameter erwartet.
 */
public class BuchungImport implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Erwartet keinen Parameter.
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {

    try
    {
      ImportDialog d = new ImportDialog(null, Buchung.class);
      d.open();
		}
    catch (OperationCanceledException oce)
    {
      // ignore
    }
		catch (ApplicationException ae)
		{
			throw ae;
		}
		catch (Exception e)
		{
			Logger.error("error while importing templates",e);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Importieren der Buchungen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
		}
  }

}


/**********************************************************************
 * $Log: BuchungImport.java,v $
 * Revision 1.1  2010/08/30 16:31:43  willuhn
 * @N Import und Export von Buchungen im XML-Format
 *
 **********************************************************************/