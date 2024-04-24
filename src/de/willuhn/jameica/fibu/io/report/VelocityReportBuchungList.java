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

import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
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

    // Das Filtern nach Konto koennen wir nicht direkt im SQL machen, weil
    // wir hierfuer einen Join brauchen. Den koennen wir aber nicht einsetzen,
    // weil in BuchungImpl die Funktion getListQuery ueberschrieben ist,
    // die den aushebelt. Das Umprogrammieren von DBIteratorImpl ist mir
    // jetzt zu umfangreich. Daher machen wir das Filtern manuell. BUGZILLA 953
    Konto startKonto = data.getStartKonto();
    Konto endKonto   = data.getEndKonto();
    
    list.setOrder("order by datum");
    //Split-Hauptbuchungen rausfiltern
    list.addFilter("NOT EXISTS(SELECT 1 FROM buchung b WHERE b.split_id = buchung.id)");
    List<Buchung> buchungen = new LinkedList<Buchung>();
    while (list.hasNext())
    {
      Buchung b = (Buchung) list.next();
      
      // Keine Begrenzung angegeben
      if (startKonto == null && endKonto == null)
      {
        buchungen.add(b);
        continue;
      }
      
      // Wir haben einen Filter angegeben

      int kh = parseKontonummer(b.getHabenKonto());
      int ks = parseKontonummer(b.getSollKonto());

      // Ein alphanumerischen compareTo-Vergleich mit Strings koennen wir nicht machen, weil wir uns nicht darauf
      // verlassen koennen, dass kurze Kontonummern vorn mit Nullen aufgefuellt sind.

      // BUGZILLA 1285
      int lower = startKonto != null ? parseKontonummer(startKonto) : Integer.MIN_VALUE; 
      int upper = endKonto   != null ? parseKontonummer(endKonto)   : Integer.MAX_VALUE;

      if (inRange(lower,upper,ks) || inRange(lower,upper,kh))
        buchungen.add(b);
    }
    
    VelocityReportData export = new VelocityReportData();
    export.addObject("buchungen",buchungen);
    export.addObject("anfangsbestaende",PseudoIterator.asList(jahr.getAnfangsbestaende()));
    export.setTemplate("buchungsjournal.vm");

    return export;
  }
  
  /**
   * Prueft, ob sich die angegebene Zahl innerhalb des Bereichs befindet.
   * @param lower untere Schranke.
   * @param upper obere Schranke.
   * @param i zu pruefende Zahl.
   * @return true, wenn die Zahl innerhalb des Bereichs liegt.
   */
  private boolean inRange(int lower, int upper, int i)
  {
    return i >= lower && i <= upper;
  }
  
  /**
   * Liefert die Kontonummer als int.
   * @param k das Konto.
   * @return die Kontonummer als int.
   * @throws RemoteException
   */
  private int parseKontonummer(Konto k) throws RemoteException
  {
    String s = k.getKontonummer();
    try
    {
      return Integer.parseInt(s);
    }
    catch (NumberFormatException e)
    {
      throw new RemoteException(i18n.tr("Ungültige Kontonummer: {0}",s));
    }
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
 * Revision 1.2  2010/11/30 23:32:18  willuhn
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