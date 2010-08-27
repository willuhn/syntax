/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungstemplateExport.java,v $
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
import de.willuhn.jameica.fibu.gui.dialogs.ExportDialog;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
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
   * Erwartet ein Objekt vom Typ <code>Address</code> oder <code>Address[]</code>.
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
        u[0] = (Buchungstemplate) context;
			}
      else
      {
        u = (Object[])context;
      }

      ExportDialog d = new ExportDialog(u, Buchungstemplate.class);
      d.open();
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
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/