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
import de.willuhn.jameica.fibu.rmi.Mandant;
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
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * ct.
   * @param mandant der Mandant.
   */
  public KontoListMenu(Mandant mandant)
  {
    this.addItem(new SingleItem(i18n.tr("Öffnen"), new KontoNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neues Konto..."), new KNeu(mandant),"list-add.png"));
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
    private Mandant mandant = null;
    
    private KNeu(Mandant mandant)
    {
      this.mandant = mandant;
    }
    
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      super.handleAction(this.mandant);
    }
    
  }
}
