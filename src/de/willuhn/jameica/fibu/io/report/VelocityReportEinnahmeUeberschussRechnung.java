/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.util.Date;

/**
 * Report fuer Uebersicht die Uberschuss-Rechnung.
 */
public class VelocityReportEinnahmeUeberschussRechnung extends AbstractVelocityReport
{
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    VelocityReportData export = new VelocityReportData();
    export.setTemplate("ueberschussrechnung.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Ergebnis: Einnahme-Überschuss-Rechnung (EÜR)");
  }

  
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedDatum(true);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-einnahme-ueberschuss.html",DATEFORMAT.format(new Date())));
    return data;
  }
}
