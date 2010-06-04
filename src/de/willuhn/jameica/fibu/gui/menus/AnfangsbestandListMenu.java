/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/AnfangsbestandListMenu.java,v $
 * $Revision: 1.6 $
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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandDelete;
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandNeu;
import de.willuhn.jameica.gui.parts.ContextMenu;
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
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Öffnen"), new AnfangsbestandNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neuer Anfangsbestand..."), new ABNeu(),"list-add.png"));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new AnfangsbestandDelete(),"user-trash-full.png"));
  }
  
  /**
   * Erzeugt immer einen neuen Anfangsbestand - unabhaengig vom Kontext.
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
 * Revision 1.6  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.5  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
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