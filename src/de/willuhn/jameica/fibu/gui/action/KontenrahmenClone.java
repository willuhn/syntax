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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.dialogs.KontenrahmenCloneDialog;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.util.KontenrahmenUtil;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Dupliziert einen Kontenrahmen.
 */
public class KontenrahmenClone implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    try
    {
      Kontenrahmen template = null;
      if (context instanceof Kontenrahmen)
        template = (Kontenrahmen) context;
      KontenrahmenCloneDialog d = new KontenrahmenCloneDialog(KontenrahmenCloneDialog.POSITION_CENTER,template);
      d.open();
      
      final Kontenrahmen kontenrahmen = d.getKontenrahmen();
      final Mandant mandant           = d.getMandant();
      final String name               = d.getName();

      BackgroundTask task = new BackgroundTask() {
        public void run(ProgressMonitor monitor) throws ApplicationException
        {
          KontenrahmenUtil.clone(kontenrahmen,mandant,name,monitor);
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Kontenrahmen erstellt"),StatusBarMessage.TYPE_SUCCESS));
        }
        public boolean isInterrupted()
        {
          return false;
        }
        public void interrupt()
        {
        }
      };
      
      Application.getController().start(task);
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
    }
    catch (Exception e)
    {
      Logger.error("unable to clone data",e);
      throw new ApplicationException(i18n.tr("Kopieren fehlgeschlagen: {0}",e.getMessage()));
    }
  }

}



/**********************************************************************
 * $Log: KontenrahmenClone.java,v $
 * Revision 1.1  2011/03/21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 **********************************************************************/