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
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateExport;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateImport;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateNeu;
import de.willuhn.jameica.fibu.gui.action.DBObjectDelete;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Listen von Buchungsvorlagen.
 */
public class BuchungstemplateListMenu extends ContextMenu
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * ct.
   * @param mandant der Mandant.
   */
  public BuchungstemplateListMenu(Mandant mandant)
  {
    this.addItem(new GJCheckedSingleContextMenuItem(i18n.tr("Öffnen"), new BuchungstemplateNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neue Buchungsvorlage..."), new BNeu(mandant),"list-add.png"));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new DBObjectDelete(),"user-trash-full.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedContextMenuItem(i18n.tr("Exportieren..."),new BuchungstemplateExport(),"document-save.png"));
    this.addItem(new ContextMenuItem(i18n.tr("Importieren..."),new BuchungstemplateImport(),"document-open.png"));
  }
  
  /**
   * Erzeugt immer eine neue Vorlage - unabhaengig vom Kontext.
   */
  private class BNeu extends BuchungstemplateNeu
  {
    private Mandant mandant = null;

    /**
     * ct.
     * @param mandant der Mandant.
     */
    private BNeu(Mandant mandant)
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
