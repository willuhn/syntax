/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungDelete.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/08 21:35:46 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 */
public class BuchungDelete implements Action
{

  /**
   * 
   */
  public BuchungDelete()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {

    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
    box.setText(i18n.tr("Buchung wirklich stornieren?"));
    box.setMessage(i18n.tr("Wollen Sie diese Buchung wirklich stornieren?"));

    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt und wechseln zurueck zur Buchungsliste
      try {
        int beleg = getBuchung().getBelegnummer();
        getBuchung().delete();
        GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. " + beleg + " storniert."));
      }
      catch (ApplicationException e1)
      {
        GUI.getStatusBar().setErrorText(e1.getLocalizedMessage());
      }
      catch (RemoteException e)
      {
        Logger.error("unable to delete buchung");
        GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Stornieren der Buchung."));
      }
    }
  }

}


/*********************************************************************
 * $Log: BuchungDelete.java,v $
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/