/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/SaldenExport.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/09/01 16:34:45 $
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Exporter fuer die Summen- und Saldenliste.
 */
public class SaldenExport extends BaseAction
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

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    FileDialog fd = new FileDialog(GUI.getShell(),SWT.SAVE);
    fd.setText(i18n.tr("Bitte geben Sie eine Datei ein, in die die Daten exportiert werden sollen."));
    fd.setFileName(i18n.tr("fibu-saldenliste-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
    String s = fd.open();
    
    Settings settings = new Settings(this.getClass());
    settings.setStoreWhenRead(true);
    String path = settings.getString("lastdir",System.getProperty("user.home"));
    if (path != null && path.length() > 0)
      fd.setFilterPath(path);

    if (s == null || s.length() == 0)
      throw new OperationCanceledException(i18n.tr("Export abgebrochen"));

    File file = new File(s);
    if (file.exists())
    {
      try
      {
        YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
        d.setTitle(i18n.tr("Datei existiert bereits"));
        d.setText(i18n.tr("Möchten Sie die Datei überschreiben?"));
        Boolean choice = (Boolean) d.open();
        if (!choice.booleanValue())
        {
          return;
        }
      }
      catch (Exception e)
      {
        Logger.error("error while saving export file",e);
        throw new ApplicationException(i18n.tr("Fehler beim Speichern der Export-Datei in {0}",s),e);
      }
    }
    
    try
    {
      // Liste der Konten ermitteln
      ArrayList list = new ArrayList();
      DBIterator i = de.willuhn.jameica.fibu.Settings.getDBService().createList(Konto.class);
      while (i.hasNext())
      {
        Konto k = (Konto) i.next();
        if (k.getSaldo() == 0.0d && k.getUmsatz() == 0.0d && k.getAnfangsbestand() == null)
          continue; // hier gibts nichts anzuzeigen
        list.add(k);
      }
      
      Konto[] konten = (Konto[]) list.toArray(new Konto[list.size()]);
      Export export = new Export();
      export.addObject("konten",konten);
      export.setTarget(new FileOutputStream(file));
      export.setTitle(i18n.tr("Summen- und Saldenliste"));
      export.setTemplate("saldenliste.vm");

      VelocityExporter.export(export);

      // Wir merken uns noch das Verzeichnis vom letzten mal
      settings.setAttribute("lastdir",file.getParent());

      GUI.getStatusBar().setSuccessText(i18n.tr("Daten exportiert nach {0}",s));
      new Program().handleAction(file);
    }
    catch (Exception e)
    {
      Logger.error("error while writing objects to " + s,e);
      throw new ApplicationException(i18n.tr("Fehler beim Exportieren der Daten in {0}",s),e);
    }
  }
}


/*********************************************************************
 * $Log: SaldenExport.java,v $
 * Revision 1.3  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/30 23:15:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 **********************************************************************/