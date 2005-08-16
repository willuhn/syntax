/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/SteuerNeu.java,v $
 * $Revision: 1.5 $
 * $Date: 2005/08/16 17:39:24 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Anlegen eines neuen Steuersatzes.
 */
public class SteuerNeu extends BaseAction
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (!check())
    {
      super.handleAction(context);
      return;
    }

    GUI.startView(de.willuhn.jameica.fibu.gui.views.SteuerNeu.class,context);
  }

}


/*********************************************************************
 * $Log: SteuerNeu.java,v $
 * Revision 1.5  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.2  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/