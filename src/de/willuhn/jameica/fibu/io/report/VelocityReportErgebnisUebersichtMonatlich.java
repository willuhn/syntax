package de.willuhn.jameica.fibu.io.report;

import java.util.Date;

public class VelocityReportErgebnisUebersichtMonatlich extends
    AbstractVelocityReport
{

  protected VelocityReportData getData(ReportData data) throws Exception
  {
    VelocityReportData export = new VelocityReportData();
    export.setTemplate("ergebnisuebersicht.monatlich.vm");
    return export;
  }

  public String getName()
  {
    return i18n.tr("Ergebnis: monatliche Übersicht");
  }

  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-ergebnisuebersicht-monatlich.html",
        DATEFORMAT.format(new Date())));
    return data;
  }
}
