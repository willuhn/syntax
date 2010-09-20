/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/VelocityReportAvEinzel.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/09/20 09:19:06 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.util.ArrayList;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;

/**
 * Report fuer Einzeluebersicht des Anlagevermoegens.
 */
public class VelocityReportAvEinzel extends AbstractVelocityReport
{
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    Date end = jahr.getEnde();

    // Liste des Anlagevermoegens ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = Settings.getDBService().createList(Anlagevermoegen.class);
    while (i.hasNext())
    {
      Anlagevermoegen av = (Anlagevermoegen) i.next();
      if (av.getAnfangsbestand(jahr) < 0.01d)
        continue; // AV, welches schon komplett abgeschrieben ist, ignorieren wir

      // Wurde nach dem aktuellen Jahr angeschafft -> ignorieren wir
      if (av.getAnschaffungsdatum().after(end))
        continue;
      
      list.add(av);
    }
    Anlagevermoegen[] av = (Anlagevermoegen[]) list.toArray(new Anlagevermoegen[list.size()]);
    
    VelocityReportData export = new VelocityReportData();
    export.addObject("anlagevermoegen",av);
    export.setTemplate("anlagevermoegen-einzel.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Anlagevermögen: Einzelübersicht");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-av-einzel.html",DATEFORMAT.format(new Date())));
    return data;
  }
  
  
}


/*********************************************************************
 * $Log: VelocityReportAvEinzel.java,v $
 * Revision 1.2  2010/09/20 09:19:06  willuhn
 * @B minor gui fixes
 *
 * Revision 1.1  2010-08-27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.2  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/