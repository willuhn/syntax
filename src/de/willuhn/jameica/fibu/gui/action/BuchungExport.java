/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungExport.java,v $
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
import de.willuhn.jameica.fibu.gui.dialogs.ExportDialog;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action, ueber die Buchungen exportieren werden koennen.
 * Als Parameter kann eine einzelnes Buchungs-Objekt oder ein Array uebergeben werden.
 */
public class BuchungExport implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Erwartet ein Objekt vom Typ <code>Buchung</code> oder <code>Buchung[]</code>.
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {

		if (context == null)
			throw new ApplicationException(i18n.tr("Bitte wählen Sie mindestens eine Buchung aus"));

		if (!(context instanceof Buchung) &&
        !(BaseBuchung[].class.isAssignableFrom(context.getClass())))
			throw new ApplicationException(i18n.tr("Bitte wählen Sie einen oder mehrere Buchungen aus"));

    Object[] u = null;
		try {

			if (context instanceof BaseBuchung)
			{
				u = new BaseBuchung[1];
        u[0] = context;
			}
      else
      {
        u = (Object[])context;
      }

      ExportDialog d = new ExportDialog(u, BaseBuchung.class);
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
			Logger.error("error while exporting transfers",e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Exportieren der Buchungen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
		}
  }

}


/**********************************************************************
 * $Log: BuchungExport.java,v $
 * Revision 1.2  2011/05/11 10:38:51  willuhn
 * @N OCE fangen
 *
 * Revision 1.1  2010-08-30 16:31:43  willuhn
 * @N Import und Export von Buchungen im XML-Format
 *
 **********************************************************************/