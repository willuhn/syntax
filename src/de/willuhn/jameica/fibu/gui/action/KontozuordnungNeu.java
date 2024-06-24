package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Anlegen einer neuen Kontozuordnung.
 */
public class KontozuordnungNeu implements Action{
	/**
	   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
	   */
	  public void handleAction(Object context) throws ApplicationException
	  {
	    GUI.startView(de.willuhn.jameica.fibu.gui.views.KontozuordnungNeu.class,context);
	  }
}
