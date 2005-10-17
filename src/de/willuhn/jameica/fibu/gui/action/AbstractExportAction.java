/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/AbstractExportAction.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/10/17 22:59:38 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.io.File;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Klasse fuer die Exports.
 */
public abstract class AbstractExportAction implements ExportAction
{

  private I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private static Settings settings = new Settings(AbstractExportAction.class);
  
  private Date start = null;
  private Date end = null;
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#storeTo(java.lang.String)
   */
  public File storeTo(String suggestion) throws OperationCanceledException, ApplicationException
  {
    for (int i=0;i<10;++i)
    {
      FileDialog fd = new FileDialog(GUI.getShell(),SWT.SAVE);
      fd.setText(i18n.tr("Bitte geben Sie eine Datei ein, in die die Daten exportiert werden sollen."));
      fd.setFileName(suggestion);

      settings.setStoreWhenRead(true);
      String path = settings.getString("lastdir",System.getProperty("user.home"));
      if (path != null && path.length() > 0)
        fd.setFilterPath(path);

      String s = fd.open();
      
      if (s == null || s.length() == 0)
        throw new OperationCanceledException();
      

      if (s == null || s.length() == 0)
        throw new OperationCanceledException(i18n.tr("Export abgebrochen"));

      File file = new File(s);
      if (!file.exists())
      {
        // Wir merken uns noch das Verzeichnis vom letzten mal
        settings.setAttribute("lastdir",file.getParent());
        return file;
        
      }
      
      try
      {
        YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
        d.setTitle(i18n.tr("Datei existiert bereits"));
        d.setText(i18n.tr("Möchten Sie die Datei überschreiben?"));
        Boolean choice = (Boolean) d.open();
        if (choice.booleanValue())
          return file;
      }
      catch (Exception e)
      {
        Logger.error("error while saving export file",e);
        throw new ApplicationException(i18n.tr("Fehler beim Speichern der Export-Datei in {0}",s),e);
      }
    }
    throw new OperationCanceledException("giving up after 10 retries");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getEnd()
   */
  public Date getEnd()
  {
    return this.end;
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getStart()
   */
  public Date getStart()
  {
    return this.start;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#setEnd(java.util.Date)
   */
  public void setEnd(Date d)
  {
    this.end = d;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#setStart(java.util.Date)
   */
  public void setStart(Date d)
  {
    this.start = d;
  }
}


/*********************************************************************
 * $Log: AbstractExportAction.java,v $
 * Revision 1.3  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.2  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.1  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 **********************************************************************/