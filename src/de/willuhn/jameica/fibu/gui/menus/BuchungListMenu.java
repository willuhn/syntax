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
import de.willuhn.jameica.fibu.gui.action.BuchungDelete;
import de.willuhn.jameica.fibu.gui.action.BuchungDuplicate;
import de.willuhn.jameica.fibu.gui.action.BuchungExport;
import de.willuhn.jameica.fibu.gui.action.BuchungImport;
import de.willuhn.jameica.fibu.gui.action.BuchungMarkChecked;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.action.BuchungReversal;
import de.willuhn.jameica.fibu.gui.action.BuchungSplitNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Buchungs-Listen.
 */
public class BuchungListMenu extends ContextMenu
{
  /**
   * ct.
   */
  public BuchungListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new SingleItem(i18n.tr("Öffnen"), new BuchungNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neue Buchung..."), new BNeu(),"list-add.png"));
    this.addItem(new SingleItem(i18n.tr("Buchung spliten..."), new BuchungSplitNeu(),"edit-copy.png"));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new BuchungDelete(),"user-trash-full.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new SingleItem(i18n.tr("Duplizieren..."), new BuchungDuplicate(),"edit-copy.png"));
    this.addItem(new SingleItem(i18n.tr("Storno-Buchung erstellen..."), new BuchungReversal(),"view-refresh.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new GeprueftItem(i18n.tr("Als \"geprüft\" markieren"), new BuchungMarkChecked(true),false,"emblem-default.png"));
    this.addItem(new GeprueftItem(i18n.tr("Als \"ungeprüft\" markieren"), new BuchungMarkChecked(false),true,"edit-undo.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedContextMenuItem(i18n.tr("Exportieren..."),new BuchungExport(),"document-save.png"));
    this.addItem(new ContextMenuItem(i18n.tr("Importieren..."),new BuchungImport(),"document-open.png"));
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
      super(text,action, icon);
    }
    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o instanceof Buchung[])
        return false;
      return super.isEnabledFor(o);
    }
  }

  /**
   * Ueberschrieben, um zu pruefen, ob die Buchung als geprueft oder ungeprueft markiert werden kann.
   */
  private static class GeprueftItem extends GJCheckedContextMenuItem
  {
    private boolean geprueft = false;
    
    /**
     * @param text
     * @param action
     * @param geprueft
     * @param icon
     */
    private GeprueftItem(String text, Action action, boolean geprueft, String icon)
    {
      super(text,action,icon);
      this.geprueft = geprueft;
    }

    /**
     * @see de.willuhn.jameica.gui.parts.ContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o instanceof Buchung)
      {
        try
        {
          Buchung b = (Buchung) o;
          return super.isEnabledFor(o) && ((b.isGeprueft() && geprueft) || (!b.isGeprueft() && !geprueft));
        }
        catch (Exception e)
        {
          Logger.error("unable to check buchung",e);
        }
      }
      return super.isEnabledFor(o);
    }
  }

  /**
   * Erzeugt immer eine neue Buchung - unabhaengig vom Kontext.
   */
  private static class BNeu extends BuchungNeu
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
