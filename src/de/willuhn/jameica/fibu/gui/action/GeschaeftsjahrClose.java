/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/GeschaeftsjahrClose.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/29 21:37:02 $
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
import java.util.Date;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Schliessen des Geschaeftsjahres.
 */
public class GeschaeftsjahrClose implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Geschaeftsjahr))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    Geschaeftsjahr jahr = (Geschaeftsjahr) context;

    try
    {
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Sicher?"));
      d.setText(i18n.tr("Sind Sie sicher, dass Sie das Geschäftsjahr \"{0}\" abschliessen möchten?\n" +
                        "Hierbei werden alle Abschreibungsbuchungen vorgenommen und die Salden der " +
                        "Konto als Anfangsbestand auf das neue Geschäftsjahr übernommen",jahr.getAttribute(jahr.getPrimaryAttribute()).toString()));
      Boolean b = (Boolean) d.open();
      if (!b.booleanValue())
        return;
    }
    catch (OperationCanceledException oce)
    {
      return;
    }
    catch (Exception e)
    {
      Logger.error("error while checking gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Geschäftsjahres"));
    }
    
    try
    {
      if (new Date().before(jahr.getEnde()))
      {
        YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
        d.setTitle(i18n.tr("Abschluss vor Ablauf"));
        d.setText(i18n.tr("Warnung: Sie schliessen das Geschäftsjahr noch bevor dessen Ende erreicht ist. " +
            "Sie können anschliessend keine Buchungen mehr darauf erfassen. Sind Sie sicher?"));
        Boolean b = (Boolean) d.open();
        if (!b.booleanValue())
          return;
      }
    }
    catch (OperationCanceledException oce)
    {
      return;
    }
    catch (Exception e)
    {
      Logger.error("error while checking gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Geschäftsjahres"));
    }
    
    try
    {
      jahr.close();
    }
    catch (RemoteException e)
    {
      Logger.error("error while closing gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Schliessen des Geschäftsjahres"));
    }
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrClose.java,v $
 * Revision 1.1  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 **********************************************************************/