/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AbstractBuchungGeprueft.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/05/08 15:41:57 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Aktion Markieren von Buchungen als geprueft oder ungeprueft.
 */
public abstract class AbstractBuchungGeprueft implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null)
      return;
    
    if (!(context instanceof Buchung) && !(context instanceof Buchung[]))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      Buchung[] b = null;
      
      if (context instanceof Buchung)
        b = new Buchung[] {(Buchung) context};
      else
        b = (Buchung[]) context;
      
      for (int i=0;i<b.length;++i)
      {
        try
        {
          b[i].setGeprueft(getNewState());
          b[i].store();
        }
        catch (Exception e)
        {
          Logger.error("unable to change state for buchung",e);
        }
      }

      if (b.length > 1)
      {
        GUI.getStatusBar().setSuccessText(i18n.tr("{0} Buchungen bearbeitet.", ""+b.length));
      }
      else
      {
        GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. {0} bearbeitet.", ""+b[0].getBelegnummer()));
      }

    }
    catch (Exception e)
    {
      Logger.error("unable to change state for buchungen",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Buchung(en)"));
    }
  }
  
  /**
   * Liefert den neuen Pruefungs-Status der Buchung.
   * @return true oder false.
   */
  abstract boolean getNewState();

}


/*********************************************************************
 * $Log: AbstractBuchungGeprueft.java,v $
 * Revision 1.1  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 **********************************************************************/