/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportUstVoranmeldung.java,v $
 * $Revision: 1.3 $
 * $Date: 2010/02/08 16:30:45 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;

/**
 * Exporter fuer die Auswertung zur UST-Voranmeldung.
 */
public class VelocityExportUstVoranmeldung extends AbstractVelocityExport
{
  private static Map kennzeichen = new HashMap();
  static
  {
    kennzeichen.put(new Double(0d), "48");
    kennzeichen.put(new Double(19d),"81");
    kennzeichen.put(new Double(7d), "86");
    kennzeichen.put(null, "35");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    Date start          = data.getStartDatum();
    Date end            = data.getEndDatum();
    
    double vst = 0.0d;

    Hashtable erloese = new Hashtable();

    DBIterator konten = jahr.getKontenrahmen().getKonten();
    while (konten.hasNext())
    {
      Konto kt = (Konto) konten.next();
      int type = kt.getKontoArt().getKontoArt();
      switch (type)
      {
        case Kontoart.KONTOART_ERLOES:
          Steuer st = kt.getSteuer();
          Double satz = new Double(st != null ? st.getSatz() : 0.0d);

          Erloes e = (Erloes) erloese.get(satz);
          if (e == null)
          {
            e = new Erloes(satz.doubleValue());
            erloese.put(satz,e);
          }
          e.add(getUmsatz(jahr,kt,start,end));
          break;
        case Kontoart.KONTOART_STEUER:
          Kontotyp ktyp = kt.getKontoTyp();
          if (ktyp != null && ktyp.getKontoTyp() == Kontotyp.KONTOTYP_AUSGABE)
            vst += getUmsatz(jahr,kt,start,end);
          break;
      }
    }
    
    // Wir fischen jetzt noch die UST-Gruppen raus, in denen keine Umsaetze vorliegen
    ArrayList list = new ArrayList();
    Iterator i = erloese.values().iterator();
    while (i.hasNext())
    {
      Erloes e = (Erloes) i.next();
      if (e.getBemessungsgrundlage() > 0.0d)
        list.add(e);
    }
      
    Collections.sort(list);
    VelocityExportData export = new VelocityExportData();
    export.addObject("erloese",list.toArray(new Erloes[list.size()]));
    export.addObject("vst",new Double(vst));
    export.setTemplate("ustva.vm");
    return export;
  }


  /**
   * Liefert den Umsatz des Kontos im genannten Zeitraum.
   * @param jahr Geschaeftsjahr.
   * @param konto Konto.
   * @param start Start-Datum.
   * @param end End-Datum.
   * @return Umsatz
   * @throws RemoteException
   */
  private double getUmsatz(Geschaeftsjahr jahr, Konto konto, Date start, Date end) throws RemoteException
  {
    double sum = 0.0d;
    DBIterator buchungen = null;

    // Wenn es ein Steuerkonto ist, holen wir uns die Hilfs- und Hauptbuchungen
    // des Kontos. Andernfalls nur die Hauptbuchungen
    if (konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_STEUER)
    {
      buchungen = konto.getHilfsBuchungen(jahr,start,end);
      while (buchungen.hasNext())
      {
        BaseBuchung b = (BaseBuchung) buchungen.next();
        sum += Math.abs(b.getBetrag());
      }
    }
    
    buchungen = konto.getHauptBuchungen(jahr,start,end);
    while (buchungen.hasNext())
    {
      BaseBuchung b = (BaseBuchung) buchungen.next();
      sum += Math.abs(b.getBetrag());
    }
    return sum;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Ergebnis: Umsatzsteuer-Voranmeldung");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-ustva.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * Hilfsklasse zum Gruppieren der Erloese nach Steuerklasse.
   */
  public class Erloes implements Comparable
  {
    private double satz = 0.0d;
    private double summe = 0.0d;
    
    /**
     * ct.
     * @param satz Steuersatz.
     */
    private Erloes(double satz)
    {
      this.satz = satz;
    }
    
    /**
     * Fuegt einen weiteren Betrag hinzu.
     * @param value der Betrag.
     */
    private void add(double value)
    {
      this.summe += value;
    }
    
    /**
     * Liefert die Summe der Erloese abgerundet auf volle Euro.
     * @return Summe.
     */
    public double getBemessungsgrundlage()
    {
      return Math.floor(this.summe);
    }
    
    /**
     * Liefert den Steuerbetrag basierend auf Steuersatz und Bemessungsgrundlage.
     * @return Steuerbetrag.
     */
    public double getSteuer()
    {
      return this.getBemessungsgrundlage() / 100d * this.satz;
    }
    
    /**
     * Liefert das Kennzeichen der Steuerklasse.
     * @return Kennzeichen.
     */
    public String getKennzeichen()
    {
      String kz = (String) kennzeichen.get(new Double(this.satz));
      return kz != null ? kz : (String) kennzeichen.get(null);
    }
    
    /**
     * Liefert die Bezeichnung der Erloes-Kategorie.
     * @return Bezeichnung der Erloes-Kategorie.
     */
    public String getName()
    {
      return i18n.tr("zum Steuersatz von {0}%",Integer.toString((int)satz));
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other)
    {
      if (this == other)
        return true;
      
      if (other == null || !(other instanceof Erloes))
        return false;
      
      return this.satz == ((Erloes)other).satz;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
      return new Double(this.satz).hashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
      if (o == null || !(o instanceof Erloes))
        return -1;
      return Double.compare(this.satz,((Erloes)o).satz);
    }
  }
}


/*********************************************************************
 * $Log: VelocityExportUstVoranmeldung.java,v $
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