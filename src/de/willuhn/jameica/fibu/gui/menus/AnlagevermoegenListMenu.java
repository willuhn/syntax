/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/AnlagevermoegenListMenu.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/01/04 16:04:33 $
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
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenDelete;
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenNeu;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer das Anlagevermoegen.
 */
public class AnlagevermoegenListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public AnlagevermoegenListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new GJContextMenuItem(i18n.tr("Bearbeiten"), new AnlagevermoegenNeu()));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen"), new AnlagevermoegenDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new GJContextMenuItem(i18n.tr("Anlagevermögen hinzufügen"), new ANeu()));
  }
  
  /**
   * Erzeugt immer ein neues Anlagevermoegen - unabhaengig vom Kontext.
   */
  private static class ANeu extends AnlagevermoegenNeu
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
 * $Log: AnlagevermoegenListMenu.java,v $
 * Revision 1.2  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.1  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/