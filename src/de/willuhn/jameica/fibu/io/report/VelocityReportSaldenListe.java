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

import de.willuhn.datasource.rmi.DBIterator;
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

    // Liste der Konten ermitteln
    DBIterator i = jahr.getKontenrahmen().getKonten();
    
    Konto start = data.getStartKonto();
    Konto end   = data.getEndKonto();
    
    if (start != null) i.addFilter("kontonummer >= ?",new Object[]{start.getKontonummer()});
    if (end != null) i.addFilter("kontonummer <= ?",new Object[]{end.getKontonummer()});

    List<Konto> list = new LinkedList<Konto>();
    while (i.hasNext())
    {
      Konto k = (Konto) i.next();
      if (k.getNumBuchungen(jahr,null,null) == 0)
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
