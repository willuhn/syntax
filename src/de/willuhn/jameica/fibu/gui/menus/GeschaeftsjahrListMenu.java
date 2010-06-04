/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/GeschaeftsjahrListMenu.java,v $
 * $Revision: 1.10 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrClose;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrDelete;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrNeu;
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
    this.addItem(new CheckedContextMenuItem(i18n.tr("Öffnen"), new GeschaeftsjahrNeu(),"document-open.png"));
    this.addItem(new HaveMandantItem(i18n.tr("Neues Geschäftsjahr..."), new GNeu(),"list-add.png"));
    this.addItem(new NotActiveItem(i18n.tr("Löschen..."), new GeschaeftsjahrDelete(),"user-trash-full.png"));
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
    },"emblem-default.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedHaveMandantItem(i18n.tr("Geschäftsjahr abschließen..."), new GeschaeftsjahrClose(),"go-next.png"));
  }
  
  /**
   * Wird nur aktiviert, wenn der Mandant bereits gespeichert ist.
   */
  private class HaveMandantItem extends ContextMenuItem
  {
    /**
     * ct.
     * @param text
     * @param a
     * @param icon
     */
    public HaveMandantItem(String text, Action a, String icon)
    {
      super(text, a, icon);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      try
      {
        return mandant != null && !mandant.isNewObject() && super.isEnabledFor(o);
      }
      catch (Exception e)
      {
        Logger.error("error while checking mandant",e);
      }
      return false;
    }
  }
  
  /**
   * Wird nur aktiviert, wenn der Mandant bereits gespeichert und ein Geschaeftsjahr ausgewaehlt ist und diese noch nicht geschlossen ist.
   */
  private class CheckedHaveMandantItem extends ContextMenuItem
  {
    /**
     * ct.
     * @param text
     * @param a
     * @param icon
     */
    public CheckedHaveMandantItem(String text, Action a, String icon)
    {
      super(text, a, icon);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o == null || !(o instanceof Geschaeftsjahr))
        return false;
      try
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) o;
        return !jahr.isClosed() && mandant != null && !mandant.isNewObject() && super.isEnabledFor(o);
      }
      catch (Exception e)
      {
        Logger.error("error while checking mandant",e);
      }
      return false;
    }
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
   * Hilfsklasse, um das Loeschen des aktiven Geschaeftsjahres zu unterbinden.
   */
  private class NotActiveItem extends CheckedContextMenuItem
  {
    /**
     * ct
     * @param text
     * @param a
     * @param icon
     */
    public NotActiveItem(String text, Action a,String icon)
    {
      super(text, a, icon);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.CheckedContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o != null && (o instanceof Geschaeftsjahr))
      {
        try
        {
          Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
          if (current != null)
          {
            if (current.equals((Geschaeftsjahr)o))
              return false;
          }
        }
        catch (Exception e)
        {
          Logger.error("error while checking if gj is deletable",e);
          return false;
        }
      }
      return super.isEnabledFor(o);
    }
    
  }
}


/*********************************************************************
 * $Log: GeschaeftsjahrListMenu.java,v $
 * Revision 1.10  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.9  2010/02/08 15:39:48  willuhn
 * @N Option "Geschaeftsjahr abschliessen" in Kontextmenu des Geschaeftsjahres
 * @N Zweispaltiges Layout in Mandant-Details - damit bleibt mehr Platz fuer die Reiter unten drunter
 * @N Anzeige von Pflichtfeldern
 *
 * Revision 1.8  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.7.2.1  2008/09/08 09:03:52  willuhn
 * @C aktiver Mandant/aktives Geschaeftsjahr kann nicht mehr geloescht werden
 *
 * Revision 1.7  2006/12/27 15:23:33  willuhn
 * @C merged update 1.3 and 1.4 to 1.3
 *
 * Revision 1.6  2006/06/20 23:27:17  willuhn
 * @C Anzeige des aktuellen Geschaeftsjahres
 * @C Oeffnen/Schliessen eines Geschaeftsjahres
 *
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