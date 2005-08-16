/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/SteuerListMenu.java,v $
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
import de.willuhn.jameica.fibu.gui.action.SteuerDelete;
import de.willuhn.jameica.fibu.gui.action.SteuerNeu;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Steuer-Listen.
 */
public class SteuerListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public SteuerListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new CheckedContextMenuItem(i18n.tr("Bearbeiten"), new SteuerNeu()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Löschen"), new SteuerDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neuer Steuersatz"), new SNeu()));
  }
  
  /**
   * Erzeugt immer einen neuen Steuersatz - unabhaengig vom Kontext.
   */
  private class SNeu extends SteuerNeu
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
 * $Log: SteuerListMenu.java,v $
 * Revision 1.1  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/