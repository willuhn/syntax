package de.willuhn.jameica.fibu.ext.jverein;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.dialogs.JVereinImportDialog;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class BuchungMenu implements Extension{
	
	private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

	@Override
	public void extend(Extendable extendable) {
		if (extendable == null || !(extendable instanceof ContextMenu))
	    {
	      Logger.warn("invalid extendable, skipping extension");
	      return;
	    }
	    
	    ContextMenu menu = (ContextMenu) extendable;
	    menu.addItem(ContextMenuItem.SEPARATOR);
	    
	    menu.addItem(new ContextMenuItem(i18n.tr("in SynTAX übernehmen..."), new Action() {
	    
	      public void handleAction(Object context) throws ApplicationException
	      {
	    	JVereinImportDialog dialog = new JVereinImportDialog(context,1);
	    	try {
				dialog.open();
			} catch (Exception e) {
				//Ignorieren wir
			}
	      }
	    }));
	}
}
