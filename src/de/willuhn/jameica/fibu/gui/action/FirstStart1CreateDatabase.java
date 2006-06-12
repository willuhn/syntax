/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/FirstStart1CreateDatabase.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 23:05:47 $
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
public class FirstStart1CreateDatabase implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    GUI.startView(de.willuhn.jameica.fibu.gui.views.FirstStart1CreateDatabase.class,context);
  }

}


/*********************************************************************
 * $Log: FirstStart1CreateDatabase.java,v $
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/12 14:08:30  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/