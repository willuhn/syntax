/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/KontoDelete.java,v $
 * $Revision: 1.9 $
 * $Date: 2009/07/03 10:52:19 $
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
      {
        k = new Konto[] {(Konto) context};
        if (!k[0].isUserObject())
          throw new ApplicationException(i18n.tr("System-Konten dürfen nicht gelöscht werden"));
      }
      else
        k = (Konto[]) context;
      
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      if (k.length > 1)
      {
        d.setTitle(i18n.tr("Konten wirklich löschen?"));
        d.setText(i18n.tr("Wollen Sie diese {0} Konten wirklich löschen?",""+k.length));
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
        int deleted = 0;
        String s = null;
        for (int i=0;i<k.length;++i)
        {
          if (k[i].isUserObject())
          {
            s = k[i].getKontonummer();
            k[i].delete();
            deleted++;
          }
        }
        k[0].transactionCommit();

        if (deleted > 1)
          GUI.getStatusBar().setSuccessText(i18n.tr("{0} Konten gelöscht.", ""+k.length));
        else
          GUI.getStatusBar().setSuccessText(i18n.tr("Konto {0} gelöscht.",s));
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
    catch (ApplicationException ae)
    {
      throw ae;
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
 * Revision 1.9  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.7  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.6  2006/01/02 01:54:07  willuhn
 * @N Benutzerdefinierte Konten
 *
 * Revision 1.5  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.3  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 **********************************************************************/