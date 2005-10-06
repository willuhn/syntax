/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/KontoExport.java,v $
 * $Revision: 1.10 $
 * $Date: 2005/10/06 22:50:32 $
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
import java.util.Vector;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Exporter fuer Konten.
 */
public class KontoExport extends AbstractExportAction
{
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public KontoExport()
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie mindestens ein Konto/Geschäftsjahr aus"));

    File file = null;
    try
    {
      file = storeTo(i18n.tr("fibu-kontoauszug-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
      return;
    }
    
    try
    {
      Export export = new Export();

      Geschaeftsjahr jahr = de.willuhn.jameica.fibu.Settings.getActiveGeschaeftsjahr();

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
      else if (context instanceof Geschaeftsjahr)
      {
        jahr = (Geschaeftsjahr) context;
        DBIterator konten = jahr.getKontenrahmen().getKonten();
        ArrayList l = new ArrayList();
        while (konten.hasNext())
        {
          Konto k1 = (Konto) konten.next();
          if (k1.getUmsatz(jahr) == 0.0d)
            continue;
          l.add(k1);
        }
        k = (Konto[]) l.toArray(new Konto[l.size()]);
      }
        
      export.addObject("konten",k);
      export.addObject("jahr",jahr);

      for (int i=0;i<k.length;++i)
      {
        Vector buchungen = new Vector();
        DBIterator list = k[i].getBuchungen(jahr);
        while (list.hasNext())
        {
          buchungen.add(list.next());
        }
        export.addObject("buchungen." + k[i].getKontonummer(),buchungen);
      }
      export.setTarget(new FileOutputStream(file));
      export.setTitle(getName());
      export.setTemplate("kontoauszug.vm");

      VelocityExporter.export(export);

      GUI.getStatusBar().setSuccessText(i18n.tr("Daten exportiert nach {0}",file.getAbsolutePath()));
      new Program().handleAction(file);
    }
    catch (Exception e)
    {
      Logger.error("error while writing objects to " + file.getAbsolutePath(),e);
      throw new ApplicationException(i18n.tr("Fehler beim Exportieren der Daten in {0}",file.getAbsolutePath()),e);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Konto-Auszug");
  }
}


/*********************************************************************
 * $Log: KontoExport.java,v $
 * Revision 1.10  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.9  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2005/09/26 23:57:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
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