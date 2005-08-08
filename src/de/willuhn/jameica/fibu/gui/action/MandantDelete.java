/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/MandantDelete.java,v $
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
public class MandantDelete implements Action
{

  /**
   * 
   */
  public MandantDelete()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    try {

      MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
      box.setText(i18n.tr("Mandant wirklich löschen?"));
      box.setMessage(i18n.tr("Wollen Sie diesen Mandanten wirklich löschen?"));
      if (box.open() != SWT.YES)
        return;

      // ok, wir loeschen das Objekt
      getMandant().delete();
      GUI.getStatusBar().setSuccessText(i18n.tr("Mandant gelöscht."));
    }
    catch (RemoteException e)
    {
      GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Löschen des Mandanten."));
      Logger.error("unable to delete mandant");
    }
    catch (ApplicationException ae)
    {
      GUI.getStatusBar().setErrorText(ae.getLocalizedMessage());
    }
  }

}


/*********************************************************************
 * $Log: MandantDelete.java,v $
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/