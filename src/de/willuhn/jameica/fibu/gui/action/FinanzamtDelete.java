/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/FinanzamtDelete.java,v $
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
public class FinanzamtDelete implements Action
{

  /**
   * 
   */
  public FinanzamtDelete()
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
    box.setText(i18n.tr("Finanzamt wirklich löschen?"));
    box.setMessage(i18n.tr("Wollen Sie die Daten dieses Finanzamtes wirklich löschen?"));
    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt
      try {
        getFinanzamt().delete();
        GUI.getStatusBar().setSuccessText(i18n.tr("Daten des Finanzamtes gelöscht."));
      }
      catch (RemoteException e)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Löschen der Daten des Finanzamtes."));
        Logger.error("unable to delete finanzamt");
      }
      catch (ApplicationException e1)
      {
        GUI.getStatusBar().setErrorText(e1.getLocalizedMessage());
      }
    }
  }

}


/*********************************************************************
 * $Log: FinanzamtDelete.java,v $
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/