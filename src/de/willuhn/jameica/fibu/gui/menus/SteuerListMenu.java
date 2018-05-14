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
import de.willuhn.jameica.fibu.gui.action.SteuerDelete;
import de.willuhn.jameica.fibu.gui.action.SteuerNeu;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.ContextMenu;
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
    this.addItem(new EditItem(i18n.tr("Öffnen"), new SteuerNeu(),false,"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neuer Steuersatz..."), new SNeu(),"list-add.png"));
    this.addItem(new EditItem(i18n.tr("Löschen..."), new SteuerDelete(),true,"user-trash-full.png"));
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
  
  private static class EditItem extends GJCheckedContextMenuItem
  {
    private boolean strict = false;
    
    /**
     * @param text
     * @param action
     * @param strict
     * @param icon
     */
    private EditItem(String text, Action action, boolean strict, String icon)
    {
      super(text,action,icon);
      this.strict = strict;
    }
    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      try
      {
        if (strict && o != null && !((Steuer)o).canChange())
          return false;
      }
      catch (RemoteException e)
      {
        Logger.error("unable to check steuer",e);
      }
      return super.isEnabledFor(o);
    }
  }
}


/*********************************************************************
 * $Log: SteuerListMenu.java,v $
 * Revision 1.10  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.9  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.8  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.6  2006/06/19 22:54:34  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.4  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
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