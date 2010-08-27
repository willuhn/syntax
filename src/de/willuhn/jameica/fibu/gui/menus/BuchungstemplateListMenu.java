/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/menus/BuchungstemplateListMenu.java,v $
 * $Revision: 1.4 $
 * $Date: 2010/08/27 11:19:40 $
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
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateExport;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateImport;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateNeu;
import de.willuhn.jameica.fibu.gui.action.DBObjectDelete;
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
  /**
   * ct.
   */
  public BuchungstemplateListMenu()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.addItem(new GJCheckedSingleContextMenuItem(i18n.tr("Öffnen"), new BuchungstemplateNeu(),"document-open.png"));
    this.addItem(new GJContextMenuItem(i18n.tr("Neue Buchungsvorlage..."), new BNeu(),"list-add.png"));
    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new DBObjectDelete(),"user-trash-full.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedContextMenuItem(i18n.tr("Exportieren..."),new BuchungstemplateExport(),"document-save.png"));
    this.addItem(new ContextMenuItem(i18n.tr("Importieren..."),new BuchungstemplateImport(),"document-open.png"));
  }
  
  /**
   * Erzeugt immer eine neue Vorlage - unabhaengig vom Kontext.
   */
  private static class BNeu extends BuchungstemplateNeu
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


/*********************************************************************
 * $Log: BuchungstemplateListMenu.java,v $
 * Revision 1.4  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 * Revision 1.3  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.2  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/