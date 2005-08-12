/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Math.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/12 16:43:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

/**
 * Hilfs-Klasse die ein paar mathematische Berechnungen enthaelt.
 * @author willuhn
 */
public class Math
{

  /**
   * Ermittelt den Netto-Betrag basierend auf der uebergebenen Steuer.
   * @param bruttoBetrag Brutto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return Netto-Betrag.
   */
  public static double netto(double bruttoBetrag, double steuerSatz)
  {
    return round((100 * bruttoBetrag) / (100 + steuerSatz));
  }
  
  /**
   * Ermittelt den Brutto-Betrag basierend auf der uebergebenen Steuer.
   * @param nettoBetrag Netto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return Brutto-Betrag.
   */
  public static double brutto(double nettoBetrag, double steuerSatz)
  {
    return round((nettoBetrag * (100 + steuerSatz)) / 100);
  }

  /**
   * Rechnet den Steuerbetrag aus.
   * @param bruttoBetrag Brutto-Betrag.
   * @param steuerSatz Steuersatz.
   * @return Steuer-Anteil.
   */
  public static double steuer(double bruttoBetrag, double steuerSatz)
  {
    double netto = netto(bruttoBetrag,steuerSatz);
    double s     = round(bruttoBetrag - netto);
    
    // Wir pruefen jetzt noch, ob die Summe von Netto+Steuer=Brutto
    // ist. Durch Rundungsfehler kann ggf. eine Abweidung um einen
    // Cent entstehen. Den rechnen wir der Steuer zu.
    double diff = bruttoBetrag - netto - s;
    return s + diff;
  }

  /**
   * Rundet den uebergebenen Betrag auf 2 Stellen hinter dem Komma.
   * @param betrag der zu rundende Betrag.
   * @return der gerundete Betrag.
   */
  public static double round(double betrag)
  {
    int i = (int) ((betrag + 0.005d) * 100d);
    return i / 100d;
  }
}

/*********************************************************************
 * $Log: Math.java,v $
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