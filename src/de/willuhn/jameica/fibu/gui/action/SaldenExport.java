/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/SaldenExport.java,v $
 * $Revision: 1.7 $
 * $Date: 2005/10/04 23:36:13 $
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
 * Exporter fuer die Summen- und Saldenliste.
 */
public class SaldenExport extends AbstractExportAction
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

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
      file = storeTo(i18n.tr("fibu-saldenliste-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
      return;
    }
    
    try
    {
      // Liste der Konten ermitteln
      ArrayList list = new ArrayList();
      DBIterator i = jahr.getKontenrahmen().getKonten();
      while (i.hasNext())
      {
        Konto k = (Konto) i.next();
        if (k.getSaldo(jahr) == 0.0d && k.getUmsatz(jahr) == 0.0d && k.getAnfangsbestand(jahr) == null)
          continue; // hier gibts nichts anzuzeigen
        list.add(k);
      }
      
      Konto[] konten = (Konto[]) list.toArray(new Konto[list.size()]);
      Export export = new Export();
      export.addObject("konten",konten);
      export.addObject("jahr",jahr);
      export.setTarget(new FileOutputStream(file));
      export.setTitle(i18n.tr("Summen- und Saldenliste"));
      export.setTemplate("saldenliste.vm");

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
}


/*********************************************************************
 * $Log: SaldenExport.java,v $
 * Revision 1.7  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
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