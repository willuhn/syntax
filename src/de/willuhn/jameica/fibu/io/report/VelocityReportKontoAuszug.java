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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;

/**
 * Report fuer Konten.
 */
public class VelocityReportKontoAuszug extends AbstractVelocityReport
{
  private static Map filenameMap = new HashMap();
  
  static
  {
    filenameMap.put("Anlagevermögen"    ,"anlagevermoegen");
    filenameMap.put("Aufwände"          ,"aufwaende");
    filenameMap.put("Erlöse"            ,"erloese");
    filenameMap.put("Geldkonto"         ,"geldkonto");
    filenameMap.put("Privatkonto"       ,"privatkonto");
    filenameMap.put("Steuer-Sammelkonto","steuer-sammelkonto");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    Date startDate = data.getStartDatum();
    Date endDate   = data.getEndDatum();
      

    VelocityReportData export = new VelocityReportData();
    export.addObject("filenames",filenameMap);
    export.setTemplate("kontoauszug.vm");

    //////////////////////////////////////////////////////////////////////////
    // Konten
    final List<Konto> konten = this.getKonten(data);
    final List<Konto> l = new LinkedList<Konto>();
    for (Konto k1:konten)
    {
      Anfangsbestand ab = k1.getAnfangsbestand(jahr);
      if (!data.isLeereKonten() && k1.getNumBuchungen(jahr,startDate,endDate) == 0 && (ab == null || ab.getBetrag() == 0.0d))
        continue;
      l.add(k1);
    }
    export.addObject("konten",l);
    //
    //////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    // Buchungen
    for (Konto k:l)
    {
      Vector buchungen = new Vector();

      DBIterator list = k.getHauptBuchungen(jahr,startDate,endDate);
      while (list.hasNext()) {
    	  Buchung b = (Buchung)list.next();
    	   buchungen.add(b);
      }

      Kontoart ka = k.getKontoArt();
      if (ka != null && ka.getKontoArt() == Kontoart.KONTOART_STEUER)
      {
        // Ein Steuerkonto enthaelt normalerweise nur automatisch
        // erzeugte Hilfsbuchungen. Da der User aber auch echte
        // Hauptbuchungen darauf erzeugen kann, muss die Liste
        // hier noch um die Hauptbuchungen ergaenzt werden.
        list = k.getHilfsBuchungen(jahr,startDate,endDate);
        while (list.hasNext())
          buchungen.add(list.next());
      }
      export.addObject("buchungen." + k.getKontonummer(),buchungen);
    }
    //////////////////////////////////////////////////////////////////////////

    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedLeereKonten(true);
    data.setTarget(i18n.tr("syntax-{0}-kontoauszug.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Konten: Auszug");
  }
}
