/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/SteuerListMenu.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/10/05 17:52:33 $
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
import de.willuhn.jameica.fibu.gui.action.SteuerDelete;
import de.willuhn.jameica.fibu.gui.action.SteuerNeu;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
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
    this.addItem(new EditItem(i18n.tr("Bearbeiten"), new SteuerNeu()));
    this.addItem(new EditItem(i18n.tr("Löschen"), new SteuerDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neuer Steuersatz"), new SNeu()));
  }
  
  /**
   * Erzeugt immer einen neuen Steuersatz - unabhaengig vom Kontext.
   */
  private static class SNeu extends SteuerNeu
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      super.handleAction(null);
    }
  }
  
  /**
   * Prueft zusaetzlich, ob der Steuersatz geaendert werden darf,
   */
  private static class EditItem extends CheckedContextMenuItem
  {
    /**
     * ct.
     * @param text
     * @param a
     */
    public EditItem(String text, Action a) {
      super(text, a);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o != null && (o instanceof Steuer))
      {
        try
        {
          Steuer s = (Steuer) o;
          if (s.isInitial())
            return false;
        }
        catch (RemoteException e)
        {
          Logger.error("unable to check steuer",e);
        }
      }
      return super.isEnabledFor(o);
    }
  }
}


/*********************************************************************
 * $Log: SteuerListMenu.java,v $
 * Revision 1.3  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
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