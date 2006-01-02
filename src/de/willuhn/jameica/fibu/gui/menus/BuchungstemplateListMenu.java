/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/BuchungstemplateListMenu.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/02 15:18:29 $
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
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateDelete;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateNeu;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Listen von Buchungsvorlagen.
 */
public class BuchungstemplateListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public BuchungstemplateListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new CheckedContextMenuItem(i18n.tr("Bearbeiten"), new BuchungstemplateNeu()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Löschen"), new BuchungstemplateDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neue Buchungsvorlage"), new BNeu()));
  }
  
  /**
   * Erzeugt immer eine neue Vorlage - unabhaengig vom Kontext.
   */
  private static class BNeu extends BuchungstemplateNeu
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
 * $Log: BuchungstemplateListMenu.java,v $
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/