/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/VelocityReportUstVoranmeldung.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/08/30 15:49:45 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;

/**
 * Report fuer die Auswertung zur UST-Voranmeldung.
 */
public class VelocityReportUstVoranmeldung extends AbstractVelocityReport
{
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    
    Map<String,Position> positions = new HashMap<String,Position>();
    
    DBIterator konten = jahr.getKontenrahmen().getKonten();
    while (konten.hasNext())
    {
      Konto kt = (Konto) konten.next();
      
      Steuer st = kt.getSteuer();
      if (st == null)
        continue;

      String bemessung = st.getUstNrBemessung();
      if (bemessung != null && bemessung.length() > 0)
      {
        Position pos = positions.get(bemessung);
        if (pos == null)
        {
          pos = new Position();
          positions.put(bemessung,pos);
        }
        pos.add(data,kt,false);
      }
      
      String steuer = st.getUstNrSteuer();
      if (steuer != null && steuer.length() > 0)
      {
        Position pos = positions.get(steuer);
        if (pos == null)
        {
          pos = new Position();
          positions.put(steuer,pos);
        }
        pos.add(data,kt,true);
      }

    }
    
    // Wir werfen jetzt noch die Zeilen raus, wo keine Betraege vorhanden sind
    String[] keys = positions.keySet().toArray(new String[positions.size()]);
    for (String key:keys)
    {
      Position p = positions.get(key);
      if (p.getBemessung() < 0.01d && p.getSteuer() < 0.01d)
        positions.remove(key);
    }
    
    VelocityReportData export = new VelocityReportData();
    export.addObject("positions",positions);
    export.setTemplate("ustva.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Ergebnis: Umsatzsteuer-Voranmeldung");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-ustva.html",DATEFORMAT.format(new Date())));
    return data;
  }
  
  public class Position
  {
    private de.willuhn.jameica.fibu.server.Math math = new de.willuhn.jameica.fibu.server.Math();
    private double bemessung = 0.0d;
    private double steuer    = 0.0d;
    private double satz      = -1d;
    
    /**
     * Fuegt die Zahlen eines Kontos hinzu.
     * @param data die Basis-Daten des Exports. 
     * @param konto das Konto.
     * @param true, wenn es sich um das Steuerkennzeichen handelt.
     * @throws RemoteException
     */
    private void add(ReportData data, Konto konto, boolean steuer) throws RemoteException
    {
      Steuer st = konto.getSteuer();
      if (st != null && !steuer)
      {
        double s = st.getSatz();
        if (this.satz == -1d)
          this.satz = st.getSatz();
        else if (this.satz != s) // wir haben offensichtlich unterschiedliche Steuersaetze
          this.satz = -1;
      }

      Geschaeftsjahr jahr = data.getGeschaeftsjahr();
      Date start          = data.getStartDatum();
      Date end            = data.getEndDatum();

      DBIterator buchungen = konto.getHauptBuchungen(jahr,start,end);
      while (buchungen.hasNext())
      {
        Buchung b        = (Buchung) buchungen.next();
        boolean soll     = b.getSollKonto().equals(konto);
        boolean aufwand  = konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_AUFWAND;

        // Wir runden auf 2 Stellen hinterm Komma. Sonst stimmt ggf. die Summe der Einzelwerte nicht
        // mit der Summe ueberein.
        double betrag = math.round(b.getBetrag());
        if (aufwand) betrag = -betrag;

        if (soll) this.bemessung += betrag;
        else      this.bemessung -= betrag;
        
        DBIterator hilfsbuchungen = b.getHilfsBuchungen();
        while (hilfsbuchungen.hasNext())
        {
          BaseBuchung hb = (BaseBuchung) hilfsbuchungen.next();
          double steuerBetrag = math.round(hb.getBetrag());
          if (soll)
            this.steuer += steuerBetrag;
          else
            this.steuer -= steuerBetrag;
        }
      }
    }
    
    /**
     * Liefert den Bemessungsbetrag.
     * @return der Bemessungsbetrag.
     */
    public double getBemessung()
    {
      return java.lang.Math.abs((int)this.bemessung); // auf ganze Euro
    }

    /**
     * Liefert den Steuerbetrag.
     * @return der Steuerbetrag.
     */
    public double getSteuer()
    {
      // Wenn in this.satz ein sinnvoller Wert (> 0) drin steht,
      // haben wir einen einheitlichen Steuersatz und sollten
      // den Steuerbetrag nicht aus den Hilfsbuchungen holen sondern
      // manuell nochmal ausrechnen. Und das nur, weil in der
      // UST-Voranmeldung die Umsaetze auf ganze EUR abgerundet
      // werden koennen und sich der Steuerbetrag dann da drauf
      // bezieht. Wuerden wir einfach this.steuer zurueckliefern,
      // waere das zwar eigentlich der richtigere Betrag. Aber
      // weil wie gesagt die Voranmeldung die Umsaetze auf ganze
      // Euros gerundet will, wuerden die Steuern nicht dazu passen.
      
      // Haben wir jedoch unterschiedliche Steuersaetze in der
      // Position (z.Bsp. in  "zu anderen Steuersaetzen"), koennen wir
      // das natuerlich nicht machen.
      // Und bei Positionen, die Steuerkennzeichen darstellen, auch
      // nicht. Daemlicher Fiskus.
      if (this.satz > 0)
        return math.steuer(math.brutto(getBemessung(),this.satz),this.satz);
      
      // Wir runden auf 2 Stellen hinterm Komma
      return math.round(this.steuer);
    }

  }
}


/*********************************************************************
 * $Log: VelocityReportUstVoranmeldung.java,v $
 * Revision 1.2  2010/08/30 15:49:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2010/08/27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.8  2010/07/20 10:31:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2010/07/20 10:24:46  willuhn
 * @B Soll- und Haben-Seite beruecksichtigen. Fuer den Fall, dass auf einem Erloes- oder Aufwands-Konto auch Storno-Buchungen vorgenommen werden
 *
 * Revision 1.6  2010/06/08 16:08:12  willuhn
 * @N UST-Voranmeldung nochmal ueberarbeitet und die errechneten Werte geprueft
 *
 * Revision 1.5  2010/06/07 15:45:15  willuhn
 * @N Erste Version der neuen UST-Voranmeldung mit Kennziffern aus der DB
 *
 * Revision 1.4  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.3  2010/02/08 16:30:45  willuhn
 * @N Bei Steuerkonten auch die Hauptbuchungen beruecksichtigen. Andernfalls werden explizite Buchungen auf die Steuerkonten ignoriert (siehe Lars' Mail vom 08.02.2010)
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