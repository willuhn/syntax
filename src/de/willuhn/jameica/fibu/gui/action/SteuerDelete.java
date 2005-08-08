/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/SteuerDelete.java,v $
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
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

/**
 */
public class SteuerDelete implements Action
{

  /**
   * 
   */
  public SteuerDelete()
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
      box.setText(i18n.tr("Steuersatz wirklich löschen?"));
      box.setMessage(i18n.tr("Wollen Sie diesen Steuersatz wirklich löschen?"));
      if (box.open() != SWT.YES)
        return;

      // ok, wir loeschen das Objekt
      getSteuer().delete();
      GUI.setActionText(i18n.tr("Steuersatz gelöscht."));
    }
    catch (RemoteException e)
    {
      GUI.setActionText(i18n.tr("Fehler beim Löschen des Steuersatzes."));
      Application.getLog().error("unable to delete steuer");
    }
    catch (ApplicationException ae)
    {
      GUI.setActionText(ae.getLocalizedMessage());
    }
  }

}


/*********************************************************************
 * $Log: SteuerDelete.java,v $
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/