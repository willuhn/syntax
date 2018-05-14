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

import java.rmi.RemoteException;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.AbschreibungDelete;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer die Liste von Abschreibungen.
 */
public class AbschreibungListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public AbschreibungListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new AItem(i18n.tr("Löschen..."), new AbschreibungDelete(),"user-trash-full.png"));
  }
  
  /**
   * ueberschrieben, damit nur Sonderabschreibungen geloescht werden koennen.
   */
  private class AItem extends GJCheckedContextMenuItem
  {

    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o == null || !(o instanceof Abschreibung))
        return false;
      Abschreibung a = (Abschreibung) o;
      try
      {
        return a.isSonderabschreibung() && super.isEnabledFor(o);
      }
      catch (RemoteException e)
      {
        Logger.error("unable to check abschreibung",e);
        return false;
      }
    }
    /**
     * @param text
     * @param a
     * @param icon
     */
    public AItem(String text, Action a, String icon)
    {
      super(text, a,icon);
    }
    
    
  }
  
}


/*********************************************************************
 * $Log: AbschreibungListMenu.java,v $
 * Revision 1.2  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.1  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 **********************************************************************/