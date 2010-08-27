/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/VelocityReportBuchungList.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 10:18:14 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Report fuer das Buchungsjournal.
 */
public class VelocityReportBuchungList extends AbstractVelocityReport
{
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    DBIterator list = jahr.getHauptBuchungen();

    // Filter Start- und End-Datum
    Date start       = data.getStartDatum();
    Date end         = data.getEndDatum();
    if (start != null || end != null)
    {
      DBService service = Settings.getDBService();
      if (start != null) list.addFilter(service.getSQLTimestamp("datum") + " >= " + start.getTime());
      if (end != null)   list.addFilter(service.getSQLTimestamp("datum") + " <=" + end.getTime());
    }
 
    // Filter Konto
    Konto startKonto = data.getStartKonto();
    Konto endKonto   = data.getEndKonto();
    if (startKonto != null)
      list.addFilter("(sollkonto_id >= " + startKonto.getID() + " OR habenkonto_id >= " + startKonto.getID() + ")");
    if (endKonto != null)
      list.addFilter("(sollkonto_id <= " + endKonto.getID() + " OR habenkonto_id <= " + endKonto.getID() + ")");
    
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

    VelocityReportData export = new VelocityReportData();
    export.addObject("buchungen",b);
    export.addObject("anfangsbestaende",ab);
    export.setTemplate("buchungsjournal.vm");

    return export;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Buchungen: Journal");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedDatum(true);
    data.setNeedKonto(true);
    data.setTarget(i18n.tr("syntax-{0}-journal.html",DATEFORMAT.format(new Date())));
    return data;
  }

  
}


/*********************************************************************
 * $Log: VelocityReportBuchungList.java,v $
 * Revision 1.1  2010/08/27 10:18:14  willuhn
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