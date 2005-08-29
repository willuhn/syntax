/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/GeschaeftsjahrNeu.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/29 16:43:14 $
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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Anlegen eines neuen Geschaeftsjahres.
 */
public class GeschaeftsjahrNeu implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    Geschaeftsjahr jahr = null;
    if (context != null)
    {
      if (context instanceof Mandant)
      {
        try
        {
          jahr = (Geschaeftsjahr) Settings.getDBService().createObject(Geschaeftsjahr.class,null);
          jahr.setMandant((Mandant)context);
        }
        catch (RemoteException e)
        {
          Logger.error("error while creating gj",e);
          throw new ApplicationException(i18n.tr("Fehler beim Erzeugen des Geschäftsjahres"));
        }
      }
      else
      {
        jahr = (Geschaeftsjahr) context;
      }
    }
    try
    {
      if (jahr == null)
        jahr = Settings.getActiveGeschaeftsjahr();
    }
    catch (RemoteException e)
    {
      Logger.error("unable to read active gj",e);
      GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Laden des Geschäftsjahres"));
    }
    GUI.startView(de.willuhn.jameica.fibu.gui.views.GeschaeftsjahrNeu.class,jahr);
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrNeu.java,v $
 * Revision 1.4  2005/08/29 16:43:14  willuhn
 * @B bugfixing
 *
 * Revision 1.3  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.2  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/