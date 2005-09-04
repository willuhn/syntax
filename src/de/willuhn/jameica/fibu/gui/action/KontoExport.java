/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/KontoExport.java,v $
 * $Revision: 1.5 $
 * $Date: 2005/09/04 23:10:14 $
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
import java.util.Date;
import java.util.Vector;

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
 * Exporter fuer Konten.
 */
public class KontoExport extends BaseAction
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

    if (context == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie mindestens ein Konto aus"));

    if (!(context instanceof Konto) && !(context instanceof Konto[]))
      throw new ApplicationException(i18n.tr("Bitte wählen Sie ein oder mehrere Konten aus"));

    FileDialog fd = new FileDialog(GUI.getShell(),SWT.SAVE);
    fd.setText(i18n.tr("Bitte geben Sie eine Datei ein, in die die Daten exportiert werden sollen."));
    fd.setFileName(i18n.tr("fibu-kontoauszug-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
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
      Export export = new Export();

      Konto[] k = null;
      if (context instanceof Konto)
      {
        k = new Konto[1];
        k[0] = (Konto) context;
      }
      else if (context instanceof Konto[])
      {
        k = (Konto[]) context;
      }
      export.addObject("konten",k);
      
      for (int i=0;i<k.length;++i)
      {
        Vector buchungen = new Vector();
        DBIterator list = k[i].getBuchungen();
        while (list.hasNext())
        {
          buchungen.add(list.next());
        }
        export.addObject("buchungen." + k[i].getKontonummer(),buchungen);
      }
      export.setTarget(new FileOutputStream(file));
      export.setTitle(i18n.tr("Konto-Auszug"));
      export.setTemplate("kontoauszug.vm");

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
 * $Log: KontoExport.java,v $
 * Revision 1.5  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 * Revision 1.2  2005/08/24 23:02:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/