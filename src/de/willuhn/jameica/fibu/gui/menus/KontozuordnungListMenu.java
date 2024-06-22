package de.willuhn.jameica.fibu.gui.menus;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.DBObjectDelete;
import de.willuhn.jameica.fibu.gui.action.KontozuordnungNeu;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Kontext-Menu fuer Listen von Kontozuordnungen.
 */
public class KontozuordnungListMenu extends ContextMenu
{
	  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
	  
	  /**
	   * ct.
	   * @param mandant der Mandant.
	   */
	  public KontozuordnungListMenu(Mandant mandant)
	  {
	    this.addItem(new GJCheckedSingleContextMenuItem(i18n.tr("Öffnen"), new KontozuordnungNeu(),"document-open.png"));
	    this.addItem(new GJContextMenuItem(i18n.tr("Neue Kontozuordnung..."), new BNeu(mandant),"list-add.png"));
	    this.addItem(new GJCheckedContextMenuItem(i18n.tr("Löschen..."), new DBObjectDelete(),"user-trash-full.png"));
	  }
	  
	  /**
	   * Erzeugt immer eine neue Vorlage - unabhaengig vom Kontext.
	   */
	  private class BNeu extends KontozuordnungNeu
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