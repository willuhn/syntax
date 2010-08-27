/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/GJCheckedSingleContextMenuItem.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 11:19:40 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.menus;

import de.willuhn.jameica.gui.Action;

/**
 * Context-Menu-Item, das zusaetzlich noch prueft, ob nur ein Element markiert ist.
 */
public class GJCheckedSingleContextMenuItem extends GJCheckedContextMenuItem
{

  /**
   * @param text
   * @param a
   * @param icon
   */
  public GJCheckedSingleContextMenuItem(String text, Action a, String icon)
  {
    super(text, a, icon);
  }

  /**
   * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
   */
  public boolean isEnabledFor(Object o)
  {
    if (o instanceof Object[])
      return false;
    
    return super.isEnabledFor(o);
  }
}


/*********************************************************************
 * $Log: GJCheckedSingleContextMenuItem.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 * Revision 1.2  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.1  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 **********************************************************************/