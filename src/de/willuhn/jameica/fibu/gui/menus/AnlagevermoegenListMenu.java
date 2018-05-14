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
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenDelete;
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenNeu;
import de.willuhn.jameica.gui.parts.ContextMenu;
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
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Öffnen"), new AnlagevermoegenNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neues Anlagevermögen..."), new ANeu(),"list-add.png"));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new AnlagevermoegenDelete(),"user-trash-full.png"));
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
 * Revision 1.4  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.3  2010/06/03 14:26:16  willuhn
 * @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
 * @C Code-Cleanup
 *
 * Revision 1.2  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.1  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/