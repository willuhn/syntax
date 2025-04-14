/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Report fuer die Summen- und Saldenliste.
 */
public class VelocityReportSaldenListe extends AbstractVelocityReport
{
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    final List<Konto> konten = this.getKonten(data);
    
    final List<Konto> list = new LinkedList<Konto>();
    for (Konto k:konten)
    {
      if (!data.isLeereKonten() && k.getNumBuchungen(jahr,null,null) == 0)
        continue; // hier gibts nichts anzuzeigen
      list.add(k);
    }
    
    VelocityReportData export = new VelocityReportData();
    export.addObject("konten",list);
    export.addObject("start",data.getStartDatum());
    export.addObject("end",data.getEndDatum());
    export.setTemplate("saldenliste.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedLeereKonten(true);
    data.setNeedDatum(true);
    data.setTarget(i18n.tr("syntax-{0}-salden.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Konten: Summen- und Saldenliste");
  }
}
