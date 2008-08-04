/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/UstVaExport.java,v $
 * $Revision: 1.1.2.3 $
 * $Date: 2008/08/04 22:33:16 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Exporter fuer die Auswertung zur UST-Voranmeldung.
 */
public class UstVaExport extends AbstractExportAction
{
  private static Map kennzeichen = new HashMap();
  static
  {
    kennzeichen.put(new Double(0d), "48");
    kennzeichen.put(new Double(19d),"81");
    kennzeichen.put(new Double(7d), "86");
    kennzeichen.put(null, "35");
  }

  private Geschaeftsjahr jahr = null;
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#fill(de.willuhn.jameica.fibu.io.Export, java.lang.Object)
   */
  protected void fill(Export export, Object context) throws ApplicationException, RemoteException, OperationCanceledException
  {
    if (context != null && (context instanceof Geschaeftsjahr))
      jahr = (Geschaeftsjahr) context;
    else
      jahr = de.willuhn.jameica.fibu.Settings.getActiveGeschaeftsjahr();
      
    DBIterator konten = jahr.getKontenrahmen().getKonten();

    double vst = 0.0d;

    Hashtable erloese = new Hashtable();

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
          e.add(getUmsatz(kt));
          break;
        case Kontoart.KONTOART_STEUER:
          Kontotyp ktyp = kt.getKontoTyp();
          if (ktyp != null && ktyp.getKontoTyp() == Kontotyp.KONTOTYP_AUSGABE)
            vst += getUmsatz(kt);
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
    export.addObject("erloese",list.toArray(new Erloes[list.size()]));
    export.addObject("vst",new Double(vst));
    export.addObject("jahr",jahr);
    export.setTemplate("ustva.vm");
  }
  
  /**
   * Liefert den Umsatz des Kontos im genannten Zeitraum.
   * @param konto Konto.
   * @return Umsatz
   * @throws RemoteException
   */
  private double getUmsatz(Konto konto) throws RemoteException
  {
    double sum = 0.0d;
    DBIterator buchungen = null;
    if (konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_STEUER)
      buchungen = konto.getHilfsBuchungen(jahr,getStart(),getEnd());
    else
      buchungen = konto.getHauptBuchungen(jahr,getStart(),getEnd());
    while (buchungen.hasNext())
    {
      BaseBuchung b = (BaseBuchung) buchungen.next();
      sum += Math.abs(b.getBetrag());
    }
    return sum;
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Umsatzsteuer-Voranmeldung");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-ustva.html",DATEFORMAT.format(new Date()));
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
 * $Log: UstVaExport.java,v $
 * Revision 1.1.2.3  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.1.2.2  2008/08/03 23:10:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 **********************************************************************/