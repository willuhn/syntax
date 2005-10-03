/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Math.java,v $
 * $Revision: 1.9 $
 * $Date: 2005/10/03 21:55:24 $
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
   * @return Netto-Betrag.
   */
  public double netto(double bruttoBetrag, double steuerSatz)
  {
    return round((100d * bruttoBetrag) / (100d + steuerSatz));
  }
  
  /**
   * Ermittelt den Brutto-Betrag basierend auf der uebergebenen Steuer.
   * @param nettoBetrag Netto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return Brutto-Betrag.
   */
  public double brutto(double nettoBetrag, double steuerSatz)
  {
    return round((nettoBetrag * (100d + steuerSatz)) / 100d);
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
    return round(bruttoBetrag - netto);
  }

  /**
   * Rundet den uebergebenen Betrag auf 2 Stellen hinter dem Komma.
   * @param betrag der zu rundende Betrag.
   * @return der gerundete Betrag.
   */
  public double round(double betrag)
  {
    int i = (int) ((betrag + 0.005d) * 100d);
    return i / 100d;
  }
  
  /**
   * Addiert den Betrag zur Summe mit diesem Namen.
   * @param name Name der Summe.
   * @param value zu addierender Betrag.
   */
  public void add(String name, double value)
  {
    Double d = (Double) this.table.get(name);
    if (d == null)
      d = new Double(0d);

    d = new Double(d.doubleValue() + value);
    this.table.put(name,d);
  }
  
  /**
   * Substrahiert den Betrag von Summe mit diesem Namen.
   * @param name Name der Summe.
   * @param value zu substrahierender Betrag.
   */
  public void subtract(String name, double value)
  {
    Double d = (Double) this.table.get(name);
    if (d == null)
      d = new Double(0d);

    d = new Double(d.doubleValue() - value);
    this.table.put(name,d);
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