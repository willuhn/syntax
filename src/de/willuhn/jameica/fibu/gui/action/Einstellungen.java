/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Oeffnen der Einstellungen.
 * @author willuhn
 */
public class Einstellungen implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    GUI.startView(de.willuhn.jameica.fibu.gui.views.Einstellungen.class,null);
  }

}


/*********************************************************************
 * $Log: Einstellungen.java,v $
 * Revision 1.1  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 *********************************************************************/