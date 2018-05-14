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
import de.willuhn.jameica.fibu.gui.action.KontoDelete;
import de.willuhn.jameica.fibu.gui.action.KontoNeu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Konten-Listen.
 */
public class KontoListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public KontoListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new SingleItem(i18n.tr("Öffnen"), new KontoNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neues Konto..."), new KNeu(),"list-add.png"));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new KontoDelete(),"user-trash-full.png"));
  }
  
  /**
   * Ueberschrieben, um zu pruefen, ob ein Array oder ein einzelnes Element markiert ist.
   */
  private static class SingleItem extends GJCheckedContextMenuItem
  {
    /**
     * @param text
     * @param action
     * @param icon
     */
    private SingleItem(String text, Action action, String icon)
    {
      super(text,action,icon);
    }
    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o instanceof Konto[])
        return false;
      return super.isEnabledFor(o);
    }
  }

  /**
   * Erzeugt immer ein neues Konto - unabhaengig vom Kontext.
   */
  private static class KNeu extends KontoNeu
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
 * $Log: KontoListMenu.java,v $
 * Revision 1.5  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.4  2006/12/27 15:23:33  willuhn
 * @C merged update 1.3 and 1.4 to 1.3
 *
 * Revision 1.3  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
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