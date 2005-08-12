/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungDelete.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/08/12 00:10:59 $
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
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Aktion zum Loeschen einer einzelnen oder einer Liste von Buchungen.
 */
public class BuchungDelete implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    
    if (context == null || (!(context instanceof BaseBuchung) && !(context instanceof BaseBuchung[])))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      BaseBuchung[] b = null;
      
      if (context instanceof BaseBuchung)
        b = new BaseBuchung[] {(BaseBuchung) context};
      else
        b = (BaseBuchung[]) context;
      
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      if (b.length > 1)
      {
        d.setTitle(i18n.tr("Buchungen wirklich stornieren?"));
        d.setText(i18n.tr("Wollen Sie diese {0} Buchungen wirklich stornieren?",""+b.length));
      }
      else
      {
        d.setTitle(i18n.tr("BaseBuchung wirklich stornieren?"));
        d.setText(i18n.tr("Wollen Sie die BaseBuchung \"{0}\" [Beleg {1}] wirklich stornieren?",new String[]{b[0].getText(),""+b[0].getBelegnummer()}));
      }
      

      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      b[0].transactionBegin();
      try
      {
        for (int i=0;i<b.length;++i)
        {
          b[i].delete();
        }
        b[0].transactionCommit();

        if (b.length > 1)
          GUI.getStatusBar().setSuccessText(i18n.tr("{0} Buchungen storniert.", ""+b.length));
        else
          GUI.getStatusBar().setSuccessText(i18n.tr("BaseBuchung Nr. {0} storniert.", ""+b[0].getBelegnummer()));
      }
      catch (ApplicationException ae)
      {
        b[0].transactionRollback();
        throw ae;
      }
      catch (Exception e)
      {
        b[0].transactionRollback();
        Logger.error("unable to delete buchung",e);
        throw new ApplicationException(i18n.tr("Fehler beim Stornieren der BaseBuchung(en)"));
      }

    }
    catch (Exception e)
    {
      Logger.error("unable to delete buchung",e);
      throw new ApplicationException(i18n.tr("Fehler beim Stornieren der BaseBuchung(en)"));
    }
  }

}


/*********************************************************************
 * $Log: BuchungDelete.java,v $
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