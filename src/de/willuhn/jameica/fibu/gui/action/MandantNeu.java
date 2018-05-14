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

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Anlegen eines neuen Mandanten.
 */
public class MandantNeu implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null)
    {
      try
      {
        context = Settings.getDBService().createObject(Mandant.class,null);
      }
      catch (Exception e)
      {
        Logger.error("unable to create mandant",e);
      }
    }
    GUI.startView(de.willuhn.jameica.fibu.gui.views.MandantNeu.class,context);
  }

}


/*********************************************************************
 * $Log: MandantNeu.java,v $
 * Revision 1.5  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.4  2005/09/01 23:28:16  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.2  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/