/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungNeu.java,v $
 * $Revision: 1.7 $
 * $Date: 2005/08/16 23:14:36 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Anlegen einer neuen Buchung.
 */
public class BuchungNeu extends BaseAction
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
    Buchung b = null;
    if (context != null && (context instanceof Buchung))
      b = (Buchung) context;
    GUI.startView(de.willuhn.jameica.fibu.gui.views.BuchungNeu.class,b);
  }

}


/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.7  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.6  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/15 13:18:44  willuhn
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