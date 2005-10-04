/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/GeschaeftsjahrExport.java,v $
 * $Revision: 1.6 $
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
import java.util.Date;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Exporter fuer Uebersicht die Uberschuss-Rechnung.
 */
public class GeschaeftsjahrExport extends AbstractExportAction
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Geschaeftsjahr))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    File file = null;
    try
    {
      file = storeTo(i18n.tr("fibu-ueberschussrechnung-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
      return;
    }

    try
    {
      Export export = new Export();
      export.addObject("jahr",context);
      export.setTarget(new FileOutputStream(file));
      export.setTitle(i18n.tr("Überschuss-Rechnung"));
      export.setTemplate("ueberschussrechnung.vm");

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
 * $Log: GeschaeftsjahrExport.java,v $
 * Revision 1.6  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 **********************************************************************/