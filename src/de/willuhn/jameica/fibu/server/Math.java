/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Math.java,v $
 * $Revision: 1.12.2.1 $
 * $Date: 2008/08/03 23:02:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
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
    return (100d * bruttoBetrag) / (100d + steuerSatz);
  }
  
  /**
   * Ermittelt den Brutto-Betrag basierend auf der uebergebenen Steuer.
   * @param nettoBetrag Netto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return ungerundeter Brutto-Betrag.
   */
  public double brutto(double nettoBetrag, double steuerSatz)
  {
    double brutto = (nettoBetrag * (100d + steuerSatz)) / 100d;
    
    // Vergleich
    double steuer = steuer(brutto,steuerSatz);
    double diff = (nettoBetrag + steuer) - brutto;
    
//    if (diff != 0.0d)
//      Logger.warn("diff " + diff + ": netto: " + nettoBetrag + ", steuersatz: " + steuerSatz + ", brutto: " + brutto);
    return brutto + diff; 
    
  }

  /**
   * Rechnet den Steuerbetrag aus.
   * @param bruttoBetrag Brutto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return Steuer-Anteil.
   */
  public double steuer(double bruttoBetrag, double steuerSatz)
  {
    double netto = netto(bruttoBetrag,steuerSatz);
    return bruttoBetrag - netto;
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
      d = new Double(0d);

    d = new Double(d.doubleValue() + value);
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
      d = new Double(0d);

    d = new Double(d.doubleValue() - value);
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
    return d.doubleValue();
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

/*********************************************************************
 * $Log: Math.java,v $
 * Revision 1.12.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 * Revision 1.12  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.11  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2005/10/06 13:19:22  willuhn
 * @B bug 133
 *
 * Revision 1.9  2005/10/03 21:55:24  willuhn
 * @B bug 128, 129
 *
 * Revision 1.8  2005/08/24 23:02:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.5  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.4  2005/08/12 16:43:08  willuhn
 * @B DecimalInput
 *
 * Revision 1.3  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/19 01:43:43  willuhn
 * @C small fixes
 *
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/