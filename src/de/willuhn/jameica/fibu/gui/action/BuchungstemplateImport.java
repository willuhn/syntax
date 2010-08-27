/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungstemplateImport.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 11:19:40 $
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
import de.willuhn.jameica.fibu.gui.dialogs.ImportDialog;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action, ueber die Buchungsvorlagen importiert werden koennen.
 * Es wird kein Parameter erwartet.
 */
public class BuchungstemplateImport implements Action
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
      ImportDialog d = new ImportDialog(null, Buchungstemplate.class);
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
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Importieren der Buchungsvorlagen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
		}
  }

}


/**********************************************************************
 * $Log: BuchungstemplateImport.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/