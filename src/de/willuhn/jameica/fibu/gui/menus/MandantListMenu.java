/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/MandantListMenu.java,v $
 * $Revision: 1.3.2.1 $
 * $Date: 2008/09/08 09:03:52 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.MandantDelete;
import de.willuhn.jameica.fibu.gui.action.MandantNeu;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Mandanten-Listen.
 */
public class MandantListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public MandantListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new CheckedContextMenuItem(i18n.tr("Bearbeiten"), new MandantNeu()));
    this.addItem(new NotActiveItem(i18n.tr("Löschen"), new MandantDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neuer Mandant"), new MNeu()));
  }
  
  /**
   * Erzeugt immer einen neuen Mandanten - unabhaengig vom Kontext.
   */
  private static class MNeu extends MandantNeu
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
   * Hilfsklasse, um das Loeschen des Mandanten zu unterbinden, wenn er das
   * aktive Geschaeftsjahr enthaelt. 
   */
  private static class NotActiveItem extends CheckedContextMenuItem
  {
    /**
     * ct
     * @param text
     * @param a
     */
    public NotActiveItem(String text, Action a)
    {
      super(text, a);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.CheckedContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      // Checken, ob der Mandant das aktive Geschaeftsjahr enthaelt
      if (o != null && (o instanceof Mandant))
      {
        try
        {
          Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
          if (current != null)
          {
            Mandant cm = current.getMandant();
            if (cm.equals((Mandant)o))
              return false;
          }
        }
        catch (Exception e)
        {
          Logger.error("error while checking if mandant is deletable",e);
          return false;
        }
      }
      return super.isEnabledFor(o);
    }
    
  }
}


/*********************************************************************
 * $Log: MandantListMenu.java,v $
 * Revision 1.3.2.1  2008/09/08 09:03:52  willuhn
 * @C aktiver Mandant/aktives Geschaeftsjahr kann nicht mehr geloescht werden
 *
 * Revision 1.3  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
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