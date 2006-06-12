/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/FirstStart2CreateDatabase.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 14:08:30 $
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
 * Action zum Erstellen der Datenbank.
 */
public class FirstStart2CreateDatabase implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    GUI.startView(de.willuhn.jameica.fibu.gui.views.FirstStart2CreateDatabase.class,context);
  }

}


/*********************************************************************
 * $Log: FirstStart2CreateDatabase.java,v $
 * Revision 1.1  2006/06/12 14:08:30  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/