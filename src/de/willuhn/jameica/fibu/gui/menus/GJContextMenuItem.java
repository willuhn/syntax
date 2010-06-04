/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/GJContextMenuItem.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/06/04 00:33:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.menus;

import java.rmi.RemoteException;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.logging.Logger;

/**
 * Context-Menu-Item, das zusaetzlich noch prueft, ob das aktuelle
 * Geschaeftsjahr noch nicht geschlossen ist und bei Bedarf die Items
 * deaktiviert.
 */
public class GJContextMenuItem extends ContextMenuItem
{

  /**
   * @param text
   * @param a
   * @param icon
   */
  public GJContextMenuItem(String text, Action a, String icon)
  {
    super(text, a, icon);
  }

  /**
   * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
   */
  public boolean isEnabledFor(Object o)
  {
    try
    {
      if (Settings.getActiveGeschaeftsjahr().isClosed())
        return false;
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking gj status",e);
    }
    return super.isEnabledFor(o);
  }
}


/*********************************************************************
 * $Log: GJContextMenuItem.java,v $
 * Revision 1.2  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.1  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 **********************************************************************/