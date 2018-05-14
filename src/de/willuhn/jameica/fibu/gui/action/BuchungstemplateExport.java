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
import de.willuhn.jameica.fibu.gui.dialogs.ExportDialog;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action, ueber die Buchungsvorlagen exportieren werden koennen.
 * Als Parameter kann eine einzelnes Buchungstemplate-Objekt oder ein Array uebergeben werden.
 */
public class BuchungstemplateExport implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Erwartet ein Objekt vom Typ <code>Buchungstemplate</code> oder <code>Buchungstemplate[]</code>.
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {

		if (context == null)
			throw new ApplicationException(i18n.tr("Bitte wählen Sie mindestens eine Buchungsvorlage aus"));

		if (!(context instanceof Buchungstemplate) &&
        !(Buchungstemplate[].class.isAssignableFrom(context.getClass())))
			throw new ApplicationException(i18n.tr("Bitte wählen Sie einen oder mehrere Buchungsvorlagen aus"));

    Object[] u = null;
		try {

			if (context instanceof Buchungstemplate)
			{
				u = new Buchungstemplate[1];
        u[0] = context;
			}
      else
      {
        u = (Object[])context;
      }

      ExportDialog d = new ExportDialog(u, Buchungstemplate.class);
      d.open();
		}
    catch (OperationCanceledException oce)
    {
      Logger.info(oce.getMessage());
      return;
    }
		catch (ApplicationException ae)
		{
			throw ae;
		}
		catch (Exception e)
		{
			Logger.error("error while exporting templates",e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Exportieren der Buchungsvorlagen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
		}
  }

}


/**********************************************************************
 * $Log: BuchungstemplateExport.java,v $
 * Revision 1.3  2011/05/11 10:38:51  willuhn
 * @N OCE fangen
 *
 * Revision 1.2  2010-08-30 16:31:43  willuhn
 * @N Import und Export von Buchungen im XML-Format
 *
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/