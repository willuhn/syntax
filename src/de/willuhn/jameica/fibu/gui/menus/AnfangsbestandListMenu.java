/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/AnfangsbestandListMenu.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/08/25 21:58:57 $
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
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandDelete;
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandNeu;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer AB-Listen.
 */
public class AnfangsbestandListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public AnfangsbestandListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new CheckedContextMenuItem(i18n.tr("Bearbeiten"), new AnfangsbestandNeu()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Löschen"), new AnfangsbestandDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neuer Anfangsbestand"), new ABNeu()));
  }
  
  /**
   * Erzeugt immer einen neuen Mandanten - unabhaengig vom Kontext.
   */
  private static class ABNeu extends AnfangsbestandNeu
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
 * $Log: AnfangsbestandListMenu.java,v $
 * Revision 1.3  2005/08/25 21:58:57  willuhn
 * @N SKR04
 *
 * Revision 1.2  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/