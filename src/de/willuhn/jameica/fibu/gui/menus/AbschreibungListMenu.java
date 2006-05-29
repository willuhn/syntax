/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/AbschreibungListMenu.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/05/29 13:02:30 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
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
    this.addItem(new AItem(i18n.tr("Löschen"), new AbschreibungDelete()));
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
     */
    public AItem(String text, Action a)
    {
      super(text, a);
    }
    
    
  }
  
}


/*********************************************************************
 * $Log: AbschreibungListMenu.java,v $
 * Revision 1.1  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 **********************************************************************/