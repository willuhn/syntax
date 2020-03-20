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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Anlegen einer neuen Buchung.
 */
public class BuchungNeu implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    Buchung b = null;
    boolean split = false;
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
	    try {
	    	//Wenn die Buchung Splitbuchungen hat, de Splitansicht öffnen
	    	split = b!=null && b.getSplitBuchungen().hasNext();
	    }
	    catch (RemoteException e)
	    {
	      Logger.error("unable to load splitbchung",e);
	      I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
	      throw new ApplicationException(i18n.tr("Fehler beim Laden der Buchung"));
	    }
    }
    if(split)
    	GUI.startView(de.willuhn.jameica.fibu.gui.views.BuchungSplit.class,b);
    else
    	GUI.startView(de.willuhn.jameica.fibu.gui.views.BuchungNeu.class,b);
  }

}


/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.9  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
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