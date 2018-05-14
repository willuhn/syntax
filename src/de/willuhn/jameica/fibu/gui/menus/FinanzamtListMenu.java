/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
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
    this.addItem(new CheckedContextMenuItem(i18n.tr("Öffnen"), new FinanzamtNeu(),"document-open.png"));
    this.addItem(new ContextMenuItem(i18n.tr("Neues Finanzamt..."), new FNeu(),"list-add.png"));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Löschen..."), new FinanzamtDelete(),"user-trash-full.png"));
  }
  
  /**
   * Erzeugt immer ein neues FA - unabhaengig vom Kontext.
   */
  private static class FNeu extends FinanzamtNeu
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
 * Revision 1.3  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.2  2005/08/25 21:58:57  willuhn
 * @N SKR04
 *
 * Revision 1.1  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/