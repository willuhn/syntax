/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/KontoDelete.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/10 17:48:02 $
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
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Aktion zum Loeschen einer einzelnen oder einer Liste von Konten.
 */
public class KontoDelete implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    
    if (context == null || (!(context instanceof Konto) && !(context instanceof Konto[])))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      Konto[] k = null;
      
      if (context instanceof Konto)
        k = new Konto[] {(Konto) context};
      else
        k = (Konto[]) context;
      
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      if (k.length > 1)
      {
        d.setTitle(i18n.tr("Konten wirklich löschen?"));
        d.setText(i18n.tr("Wollen Sie diese {0} Konten wirklich stornieren?",""+k.length));
      }
      else
      {
        d.setTitle(i18n.tr("Konto wirklich löschen?"));
        d.setText(i18n.tr("Wollen Sie das Konto \"{0}\" [{1}] wirklich löschen?",new String[]{k[0].getKontonummer(),k[0].getName()}));
      }
      

      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      k[0].transactionBegin();
      try
      {
        for (int i=0;i<k.length;++i)
        {
          k[i].delete();
        }
        k[0].transactionCommit();

        if (k.length > 1)
          GUI.getStatusBar().setSuccessText(i18n.tr("{0} Konten gelöscht.", ""+k.length));
        else
          GUI.getStatusBar().setSuccessText(i18n.tr("Konto {0} gelöscht.", ""+k[0].getKontonummer()));
      }
      catch (ApplicationException ae)
      {
        k[0].transactionRollback();
        throw ae;
      }
      catch (Exception e)
      {
        k[0].transactionRollback();
        Logger.error("unable to delete konto",e);
        throw new ApplicationException(i18n.tr("Fehler beim Löschen der Konten"));
      }

    }
    catch (Exception e)
    {
      Logger.error("unable to delete konto",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Konten"));
    }
  }

}


/*********************************************************************
 * $Log: KontoDelete.java,v $
 * Revision 1.1  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 **********************************************************************/