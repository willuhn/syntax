/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/GeschaeftsjahrListMenu.java,v $
 * $Revision: 1.5 $
 * $Date: 2005/09/26 15:15:39 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenExport;
import de.willuhn.jameica.fibu.gui.action.BuchungListExport;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrClose;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrDelete;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrExport;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrNeu;
import de.willuhn.jameica.fibu.gui.action.KontoExport;
import de.willuhn.jameica.fibu.gui.action.SaldenExport;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Geschaeftsjahres-Listen.
 */
public class GeschaeftsjahrListMenu extends ContextMenu
{
  private Mandant mandant = null;
  
  /**
   * ct.
   * @param m Mandant.
   */
  public GeschaeftsjahrListMenu(Mandant m)
  {
    this.mandant = m;
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new CheckedContextMenuItem(i18n.tr("Bearbeiten"), new GeschaeftsjahrNeu()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Löschen"), new GeschaeftsjahrDelete()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Neues Geschäftsjahr"), new GNeu()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedContextMenuItem(i18n.tr("Als aktives Geschäftsjahr festlegen"), new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof Geschaeftsjahr))
          return;
        Settings.setActiveGeschaeftsjahr((Geschaeftsjahr)context);
        // Seite aktualisieren
        GUI.startView(GUI.getCurrentView().getClass(),GUI.getCurrentView().getCurrentObject());
      }
    }));
    this.addItem(new MenuItem(i18n.tr("Geschäftsjahr abschliessen"), new GeschaeftsjahrClose()));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedContextMenuItem(i18n.tr("Auswertung: Anlagevermögen"), new AnlagevermoegenExport()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Auswertung: Buchungsjournal"), new BuchungListExport()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Auswertung: Konto-Auszüge"), new KontoExport()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Auswertung: Summen- und Saldenliste"), new SaldenExport()));
    this.addItem(new CheckedContextMenuItem(i18n.tr("Auswertung: Überschuss-Rechnung"), new GeschaeftsjahrExport()));
  }
  
  /**
   * Erzeugt immer ein neues Geschaeftsjahr - unabhaengig vom Kontext.
   */
  private class GNeu extends GeschaeftsjahrNeu
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      super.handleAction(mandant);
    }
  }
  
  /**
   * Hilfs-Item.
   */
  private class MenuItem extends CheckedContextMenuItem
  {
    /**
     * @param text
     * @param a
     */
    public MenuItem(String text, Action a)
    {
      super(text, a);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o != null && (o instanceof Geschaeftsjahr))
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) o;
        try
        {
          if (jahr.isClosed())
            return false;
        }
        catch (RemoteException e)
        {
          Logger.error("error while checking gj status",e);
        }
      }
        
      return super.isEnabledFor(o);
    }
  }
}


/*********************************************************************
 * $Log: GeschaeftsjahrListMenu.java,v $
 * Revision 1.5  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.3  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
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