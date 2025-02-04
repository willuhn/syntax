/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Hilfs-Klasse die ein paar mathematische Berechnungen enthaelt.
 * @author willuhn
 */
public class Math
{
  
  private Map table = new HashMap();
  
  /**
   * Ermittelt den Netto-Betrag basierend auf der uebergebenen Steuer.
   * @param bruttoBetrag Brutto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return ungerundeter Netto-Betrag.
   */
  public double netto(double bruttoBetrag, double steuerSatz)
  {
    if (steuerSatz < 0.01d)
      return bruttoBetrag;
    return round((100d * bruttoBetrag) / (100d + steuerSatz));
  }
  
  /**
   * Ermittelt den Brutto-Betrag basierend auf der uebergebenen Steuer.
   * @param nettoBetrag Netto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return ungerundeter Brutto-Betrag.
   */
  public double brutto(double nettoBetrag, double steuerSatz)
  {
    if (steuerSatz < 0.01d)
      return nettoBetrag;

    double brutto = (nettoBetrag * (100d + steuerSatz)) / 100d;
    
    // Vergleich
    double steuer = steuer(brutto,steuerSatz);
    double diff = (nettoBetrag + steuer) - brutto;
    
    return round(brutto + diff); 
  }

  /**
   * Rechnet den Steuerbetrag aus.
   * @param bruttoBetrag Brutto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return Steuer-Anteil.
   */
  public double steuer(double bruttoBetrag, double steuerSatz)
  {
    if (steuerSatz < 0.01d)
      return 0.0d;

    double netto = netto(bruttoBetrag,steuerSatz);
    return bruttoBetrag - netto;
  }
  
  /**
   * Rundet den Betrag auf 2 Stellen hinter dem Komma.
   * @param betrag der zu rundende Betrag.
   * @return der gerundete Betrag.
   */
  public double round(double betrag)
  {
    double d = betrag > 0 ? 0.5d : -0.5d;
    int i = (int) (betrag * 100 + d);
    return i / 100d;
  }

  /**
   * Addiert den Betrag zur Summe mit diesem Namen.
   * @param name Name der Summe.
   * @param value zu addierender Betrag.
   */
  public void add(String name, double value)
  {
    add(name,name,value);
  }
  
  /**
   * Addiert den Betrag zur Summe mit diesem Namen und pappt das Ergebnis in Target.
   * @param target das Ziel, in das die Summe soll.
   * @param name Name der Summe.
   * @param value zu addierender Betrag.
   */
  public void add(String target, String name, double value)
  {
    Double d = (Double) this.table.get(name);
    if (d == null)
      d = Double.valueOf(0d);

    d = Double.valueOf(d.doubleValue() + value);
    this.table.put(target,d);
  }

  /**
   * Substrahiert den Betrag von Summe mit diesem Namen.
   * @param name Name der Summe.
   * @param value zu substrahierender Betrag.
   */
  public void substract(String name, double value)
  {
    substract(name,name,value);
  }
  
  /**
   * Erzeugt den Absolut-Wert.
   * @param value Wert.
   * @return Absolut-Wert.
   */
  public double abs(double value)
  {
    return java.lang.Math.abs(value);
  }

  /**
   * Substrahiert den Betrag von Summe mit diesem Namen und pappt das Ergebnis in Target.
   * @param target Name des Parameters, in den die Summe soll.
   * @param name Name der Summe.
   * @param value zu substrahierender Betrag.
   */
  public void substract(String target, String name, double value)
  {
    Double d = (Double) this.table.get(name);
    if (d == null)
      d = Double.valueOf(0d);

    d = Double.valueOf(d.doubleValue() - value);
    this.table.put(target,d);
  }

  /**
   * Liefert den aufsummierten Betrag fuer diesen Namen.
   * @param name Name der Summe.
   * @return Betrag.
   */
  public double get(String name)
  {
    Double d = (Double) this.table.get(name);
    if (d == null)
      return 0.0d;
    
    double result = d.doubleValue();
    if (Double.isNaN(result))
      return 0.0d;
    
    return result;
  }
  
  /**
   * Entfernt die Summe.
   * @param name
   */
  public void reset(String name)
  {
    this.table.remove(name);
  }
}
