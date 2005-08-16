/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/FinanzamtListMenu.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/16 23:14:36 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.menus;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.FinanzamtDelete;
import de.willuhn.jameica.fibu.gui.action.FinanzamtNeu;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer FA-Listen.
 */
public class FinanzamtListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public FinanzamtListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new CheckedContextMenuItem(i18n.tr("Bearbeiten"), new FinanzamtNeu()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Löschen"), new FinanzamtDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neue Finanzamt anlegen"), new FNeu()));
  }
  
  /**
   * Erzeugt immer ein neues FA - unabhaengig vom Kontext.
   */
  private class FNeu extends FinanzamtNeu
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      super.handleAction(null);
    }
    
  }
}


/*********************************************************************
 * $Log: FinanzamtListMenu.java,v $
 * Revision 1.1  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/