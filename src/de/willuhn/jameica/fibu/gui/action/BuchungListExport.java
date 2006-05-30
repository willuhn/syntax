/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/BuchungListExport.java,v $
 * $Revision: 1.9 $
 * $Date: 2006/05/30 23:33:09 $
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
import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Exporter fuer das Buchungsjournal.
 */
public class BuchungListExport extends AbstractExportAction
{
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public BuchungListExport()
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }
  
  /**
   * Erwartet null oder ein Geschaeftsjahr.
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    Geschaeftsjahr jahr = null;
    if (context != null && context instanceof Geschaeftsjahr)
    {
      jahr = (Geschaeftsjahr) context;
      
    }
    else
    {
      try
      {
        jahr = de.willuhn.jameica.fibu.Settings.getActiveGeschaeftsjahr();
      }
      catch (RemoteException e)
      {
        Logger.error("unable to determine active geschaeftsjahr",e);
        throw new ApplicationException(i18n.tr("Aktuelles Geschäftsjahr kann nicht ermittelt werden"));
      }
    }

    File file = null;
    try
    {
      file = storeTo(i18n.tr("fibu-buchungsjournal-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
      return;
    }
    
    try
    {

      DBIterator list = jahr.getHauptBuchungen();
      list.setOrder("order by datum");
      Buchung[] b = new Buchung[list.size()];
      int count = 0;
      while (list.hasNext())
      {
        b[count++] = (Buchung) list.next();
      }
      
      list = jahr.getAnfangsbestaende();
      Anfangsbestand[] ab = new Anfangsbestand[list.size()];
      count = 0;
      while (list.hasNext())
      {
        ab[count++] = (Anfangsbestand) list.next();
      }

      Export export = new Export();
      export.addObject("buchungen",b);
      export.addObject("anfangsbestaende",ab);
      export.addObject("jahr",jahr);
      export.addObject("start",getStart());
      export.addObject("end",getEnd());
      export.setTarget(new FileOutputStream(file));
      export.setTitle(getName());
      export.setTemplate("buchungsjournal.vm");

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
    return i18n.tr("Buchungsjournal");
  }
}


/*********************************************************************
 * $Log: BuchungListExport.java,v $
 * Revision 1.9  2006/05/30 23:33:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.7  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.6  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/28 01:08:03  willuhn
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