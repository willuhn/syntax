/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/AnlagevermoegenAbschreiben.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/04 00:53:48 $
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
import de.willuhn.jameica.fibu.gui.dialogs.AbschreibungDialog;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum manuellen Abschreiben.
 */
public class AnlagevermoegenAbschreiben implements Action
{

  private I18N i18n = null;
  
  /**
   * ct.
   */
  public AnlagevermoegenAbschreiben()
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    try
    {
      if (context == null || !(context instanceof Anlagevermoegen))
        return;
      
      Anlagevermoegen av = (Anlagevermoegen) context;
      
      double restwert = av.getRestwert(Settings.getActiveGeschaeftsjahr());
      if (restwert == 0.0d)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Anlage-Gegenstand ist bereits abgeschrieben"));
        return;
      }
      
      AbschreibungDialog d = new AbschreibungDialog(av, AbschreibungDialog.POSITION_CENTER);
      AbschreibungsBuchung b = null;
      try
      {
        b = (AbschreibungsBuchung) d.open();
        
        b.transactionBegin();
        b.store();
        
        Abschreibung a = (Abschreibung) Settings.getDBService().createObject(Abschreibung.class,null);
        a.setAnlagevermoegen(av);
        a.setBuchung(b);
        a.store();
        b.transactionCommit();
        GUI.getStatusBar().setSuccessText(i18n.tr("Abschreibung gebucht"));
      }
      catch (OperationCanceledException oce)
      {
        Logger.info("operation cancelled");
        return;
      }
      catch (ApplicationException ae)
      {
        try
        {
          if (b != null)
            b.transactionRollback();
        }
        catch (Exception e)
        {
          Logger.error("rollback failed",e);
        }
        throw ae;
      }
      catch (Exception e)
      {
        try
        {
          if (b != null)
            b.transactionRollback();
        }
        catch (Exception e1)
        {
          Logger.error("rollback failed",e1);
        }
        Logger.error("error while calculating abschreibung",e);
        GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Erzeugen der Abschreibung"));
      }
      
      
    }
    catch (RemoteException e)
    {
      Logger.error("error while calculating abschreibung",e);
      GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Erzeugen der Abschreibung"));
    }
    
  }

}


/*********************************************************************
 * $Log: AnlagevermoegenAbschreiben.java,v $
 * Revision 1.1  2006/01/04 00:53:48  willuhn
 * @B bug 166 Ausserplanmaessige Abschreibungen
 *
 **********************************************************************/