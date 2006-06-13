/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/FirstStart2CreateMandant.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/13 22:52:10 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Erstellen des Mandanten.
 */
public class FirstStart2CreateMandant implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    GUI.startView(de.willuhn.jameica.fibu.gui.views.FirstStart2CreateMandant.class,context);
  }

}


/*********************************************************************
 * $Log: FirstStart2CreateMandant.java,v $
 * Revision 1.1  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 **********************************************************************/