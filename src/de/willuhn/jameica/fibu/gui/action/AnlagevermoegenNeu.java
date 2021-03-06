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

import java.rmi.RemoteException;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Anlegen eines neuen Anlagevermoegens.
 */
public class AnlagevermoegenNeu implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    Anlagevermoegen a = null;
    if (context != null)
    {
      if (context instanceof Mandant)
      {
        try
        {
          a = (Anlagevermoegen) Settings.getDBService().createObject(Anlagevermoegen.class,null);
          a.setMandant((Mandant)context);
        }
        catch (RemoteException e)
        {
          Logger.error("error while creating anlagevermoegen",e);
        }
      }
      else
      {
        a = (Anlagevermoegen) context;
      }
    }
    
    GUI.startView(de.willuhn.jameica.fibu.gui.views.AnlagevermoegenNeu.class,a);
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenNeu.java,v $
 * Revision 1.3  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/