/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungNeu.java,v $
 * $Revision: 1.8 $
 * $Date: 2005/08/22 23:13:26 $
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
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Anlegen einer neuen Buchung.
 */
public class BuchungNeu extends BaseAction
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (!check())
    {
      super.handleAction(context);
      return;
    }
    Buchung b = null;
    if (context != null)
    {
      if (context instanceof Buchung)
        b = (Buchung) context;
      else if (context instanceof HilfsBuchung)
      {
        try
        {
          b = ((HilfsBuchung)context).getHauptBuchung();
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load hauptbuchung",e);
          I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
          throw new ApplicationException(i18n.tr("Fehler beim Laden der Buchung"));
        }
      }
    }
    GUI.startView(de.willuhn.jameica.fibu.gui.views.BuchungNeu.class,b);
  }

}


/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.8  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.6  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/15 13:18:44  willuhn
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