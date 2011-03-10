/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/VelocityReportSaldenListe.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/03/10 13:42:26 $
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
      if (k.getNumBuchungen(jahr) == 0)
        continue; // hier gibts nichts anzuzeigen
      list.add(k);
    }
    
    VelocityReportData export = new VelocityReportData();
    export.addObject("konten",list);
    export.setTemplate("saldenliste.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedDatum(false);
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


/*********************************************************************
 * $Log: VelocityReportSaldenListe.java,v $
 * Revision 1.3  2011/03/10 13:42:26  willuhn
 * @B BUGZILLA 1001
 *
 * Revision 1.2  2010-11-30 23:32:18  willuhn
 * @B BUGZILLA 953
 * @C Velocity kann inzwischen mit java.util.List-Objekten umgehen. Das Erzeugen der Arrays ist daher nicht mehr noetig
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