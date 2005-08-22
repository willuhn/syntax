/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AnfangsbestandNeu.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/22 21:44:09 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.rmi.RemoteException;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Anlegen eines neuen Anfangsbestandes.
 */
public class AnfangsbestandNeu implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    Anfangsbestand a = null;
    if (context != null)
    {
      if (context instanceof Anfangsbestand)
        a = (Anfangsbestand) context;
      else if (context instanceof Konto)
      {
        try
        {
          a = (Anfangsbestand) Settings.getDBService().createObject(Anfangsbestand.class,null);
          a.setKonto((Konto)context);
          a.setMandant(Settings.getActiveMandant());
        }
        catch (RemoteException e)
        {
          Logger.error("error while creating anfangsbestand",e);
          a = null;
        }
      }
      else if (context instanceof Mandant)
      {
        try
        {
          a = (Anfangsbestand) Settings.getDBService().createObject(Anfangsbestand.class,null);
          a.setMandant((Mandant)context);
        }
        catch (RemoteException e)
        {
          Logger.error("error while creating anfangsbestand",e);
          a = null;
        }
      }
    }
    GUI.startView(de.willuhn.jameica.fibu.gui.views.AnfangsbestandNeu.class,a);
  }

}


/*********************************************************************
 * $Log: AnfangsbestandNeu.java,v $
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/