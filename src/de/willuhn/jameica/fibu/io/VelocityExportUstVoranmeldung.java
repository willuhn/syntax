/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportUstVoranmeldung.java,v $
 * $Revision: 1.5 $
 * $Date: 2010/06/07 15:45:15 $
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;

/**
 * Exporter fuer die Auswertung zur UST-Voranmeldung.
 */
public class VelocityExportUstVoranmeldung extends AbstractVelocityExport
{
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
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

      String[] kz = new String[]{st.getUstNrBemessung(),st.getUstNrSteuer()};
      for (String s:kz)
      {
        if (s == null || s.length() == 0)
          continue;
        
        Position pos = positions.get(s);
        if (pos == null)
        {
          pos = new Position();
          positions.put(s,pos);
        }
        pos.add(data,kt);
      }
    }
    
    // Wir werfen jetzt noch die Zeilen raus, wo keine Betraege vorhanden sind
    String[] keys = positions.keySet().toArray(new String[positions.size()]);
    for (String key:keys)
    {
      Position p = positions.get(key);
      if (p.getBemessung() == 0.0d && p.getSteuer() == 0.0d)
        positions.remove(key);
    }
    
    VelocityExportData export = new VelocityExportData();
    export.addObject("positions",positions);
    export.setTemplate("ustva.vm");
    return export;
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
  
  public class Position
  {
    private double bemessung = 0.0d;
    private double steuer = 0.0d;
    
    /**
     * Fuegt die Zahlen eines Kontos hinzu.
     * @param data die Basis-Daten des Exports. 
     * @param konto das Konto.
     * @throws RemoteException
     */
    private void add(ExportData data, Konto konto) throws RemoteException
    {
      Geschaeftsjahr jahr = data.getGeschaeftsjahr();
      Date start          = data.getStartDatum();
      Date end            = data.getEndDatum();

      DBIterator buchungen = konto.getHauptBuchungen(jahr,start,end);
      while (buchungen.hasNext())
      {
        Buchung b = (Buchung) buchungen.next();
        this.bemessung += b.getBetrag();
        
        DBIterator hilfsbuchungen = b.getHilfsBuchungen();
        while (hilfsbuchungen.hasNext())
        {
          BaseBuchung hb = (BaseBuchung) hilfsbuchungen.next();
          this.steuer += hb.getBetrag();
        }
      }
    }
    
    /**
     * Liefert den Bemessungsbetrag.
     * @return der Bemessungsbetrag.
     */
    public double getBemessung()
    {
      return Math.abs((int)this.bemessung); // auf ganze Euro
    }

    /**
     * Liefert den Steuerbetrag.
     * @return der Steuerbetrag.
     */
    public double getSteuer()
    {
      // Wir runden gleich auf 2 Stellen hinterm Komma
      int i = (int) (this.steuer * 100);
      return Math.abs(i / 100d);
    }

  }
}


/*********************************************************************
 * $Log: VelocityExportUstVoranmeldung.java,v $
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