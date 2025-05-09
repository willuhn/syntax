/**********************************************************************
 *
 * Copyright (c) 2025 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.logging.Logger;

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
    final Map<String,Position> positions = new HashMap<String,Position>();
    
    final Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    final DBIterator<Konto> konten = jahr.getKontenrahmen().getKonten();
    while (konten.hasNext())
    {
      final Konto kt = konten.next();
      final Steuer st = kt.getSteuer();
      if (st == null)
        continue;
      
      this.process(data,st,kt,null,positions);
    }
    
    // Wir werfen jetzt noch die Zeilen raus, wo keine Betraege vorhanden sind
    final String[] keys = positions.keySet().toArray(new String[positions.size()]);
    for (String key:keys)
    {
      final Position p = positions.get(key);
      if (Math.abs(p.getBemessung()) < 0.01d && Math.abs(p.getSteuer()) < 0.01d)
        positions.remove(key);
    }
    
    final VelocityReportData export = new VelocityReportData();
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
    final ReportData data = super.createPreset();
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-ustva.html",DATEFORMAT.format(new Date())));
    return data;
  }
  
  /**
   * Führt die Verarbeitung durch.
   * @param data die Report-Daten.
   * @param st Die Steuer.
   * @param k das Konto.
   * @param b optionale Angabe der Buchung.
   * @param positions Map mit den Positionen.
   * @throws RemoteException
   */
  private void process(ReportData data, Steuer st, Konto k, Buchung b, Map<String,Position> positions) throws RemoteException
  {
    final String bemessung = st.getUstNrBemessung();
    if (bemessung != null && !bemessung.isBlank())
    {
      final Position pos = positions.computeIfAbsent(bemessung, x -> new Position());
      pos.add(data,k,b,false,positions);
    }
    
    final String s = st.getUstNrSteuer();
    if (s != null && !s.isBlank())
    {
      final Position pos = positions.computeIfAbsent(s, x -> new Position());
      pos.add(data,k,b,true,positions);
    }
  }
  
  /**
   * Eine einzelne Position auf der UST-Voranmeldung.
   */
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
     * @param steuer true, wenn es sich um das Steuerkennzeichen handelt.
     * @param positions die Map mit den Positionen.
     * @throws RemoteException
     */
    private void add(ReportData data, Konto konto, Buchung b, boolean steuer, Map<String,Position> positions) throws RemoteException
    {
      Steuer st = konto.getSteuer();
      
      if (st != null && !steuer)
      {
        double s = st.getSatz();
        if (this.satz == -1d)
          this.satz = s;
        else if (this.satz != s) // wir haben offensichtlich unterschiedliche Steuersaetze
          this.satz = -1;
      }

      // Wenn die Buchung direkt angegeben ist, dann diese übernehmen.
      if (b != null)
      {
        this.add(konto,b);
        return;
      }

      // Andernfalls über alle Buchungen des Kontos iterieren
      final Geschaeftsjahr jahr = data.getGeschaeftsjahr();
      final Date start          = data.getStartDatum();
      final Date end            = data.getEndDatum();

      final DBIterator<Buchung> buchungen = konto.getHauptBuchungen(jahr,start,end);
      while (buchungen.hasNext())
      {
        final Buchung buchung = buchungen.next();
        
        // Checken, ob die Buchung ein abweichendes Steuerkonto hat
        final Steuer stb = buchung.getSteuerObject();
        if (stb != null && !Objects.equals(st.getID(),stb.getID()))
        {
          Logger.info(String.format("have booking with different tax than account [id: %s, #: %s, st-id account: %s, st-id booking: %s]",buchung.getID(),buchung.getBelegnummer(),st.getID(),stb.getID()));
          // Ja, dann muss diese Buchung in einer separaten Position verarbeitet werden
          process(data,stb,konto,buchung,positions);
          continue;
        }
        
        this.add(konto,buchung);
      }
    }
    
    /**
     * Übernimmt das eigentliche Hinzufügen der Buchung.
     * @param k das Konto.
     * @param b die Buchung.
     * @throws RemoteException
     */
    private void add(Konto k, Buchung b) throws RemoteException
    {
      boolean soll     = b.getSollKonto().equals(k);
      boolean aufwand  = k.getKontoArt().getKontoArt() == Kontoart.KONTOART_AUFWAND;

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
