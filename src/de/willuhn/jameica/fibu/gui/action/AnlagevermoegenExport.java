/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/AnlagevermoegenExport.java,v $
 * $Revision: 1.8 $
 * $Date: 2006/01/04 17:59:27 $
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
import java.util.ArrayList;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Exporter fuer Uebersicht des Anlagevermoegens.
 */
public class AnlagevermoegenExport extends AbstractExportAction
{
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public AnlagevermoegenExport()
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
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
        throw new ApplicationException(i18n.tr("Aktuelles Gesch�ftsjahr kann nicht ermittelt werden"));
      }
    }
    
    File file = null;
    try
    {
      file = storeTo(getOutputFile());
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
      return;
    }
    
    try
    {
      // Liste des Anlagevermoegens ermitteln
      ArrayList list = new ArrayList();
      DBIterator i = de.willuhn.jameica.fibu.Settings.getDBService().createList(Anlagevermoegen.class);
      while (i.hasNext())
      {
        Anlagevermoegen av = (Anlagevermoegen) i.next();
        if (av.getRestwert(jahr) > 0.0d)
          list.add(av);
      }
      
      Anlagevermoegen[] av = (Anlagevermoegen[]) list.toArray(new Anlagevermoegen[list.size()]);
      Export export = new Export();
      export.addObject("anlagevermoegen",av);
      export.addObject("jahr",jahr);
      export.setTarget(new FileOutputStream(file));
      export.setTitle(getName());
      export.setTemplate(getTemplate());

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
    return i18n.tr("Gesamt�bersicht des Anlageverm�gens");
  }
  
  /**
   * Liefert den Namen des Templates.
   * @return Dateiname des Templates.
   */
  String getTemplate()
  {
    return "anlagevermoegen.vm";
  }
  
  /**
   * Liefert den vorzuschlagenden Dateinamen fuer den Report.
   * @return Dateiname.
   */
  String getOutputFile()
  {
    return i18n.tr("fibu-anlagevermoegen-{0}.html",Fibu.FASTDATEFORMAT.format(new Date()));    
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenExport.java,v $
 * Revision 1.8  2006/01/04 17:59:27  willuhn
 * @B bug 171
 *
 * Revision 1.7  2006/01/04 17:59:11  willuhn
 * @B bug 171
 *
 * Revision 1.6  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.5  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 **********************************************************************/