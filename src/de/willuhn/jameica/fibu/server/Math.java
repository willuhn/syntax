/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Math.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/12/19 01:43:43 $
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
   * @param steuer Steuersatz.
   * @return Netto-Betrag.
   */
  public static double netto(double bruttoBetrag, double steuer)
  {
    return round((100 * bruttoBetrag) / (100 + steuer));
  }
  
  /**
   * Ermittelt den Brutto-Betrag basierend auf der uebergebenen Steuer.
   * @param nettoBetrag Netto-Betrag.
   * @param steuer Steuersatz.
   * @return Brutto-Betrag.
   */
  public static double brutto(double nettoBetrag, double steuer)
  {
    return round((nettoBetrag * (100 + steuer)) / 100);
  }

  /**
   * Rechnet den Steuerbetrag aus.
   * @param bruttoBetrag Brutto-Betrag.
   * @param steuer Steuersatz.
   * @return Steuer-Anteil.
   */
  public static double steuer(double bruttoBetrag, double steuer)
  {
    return round(bruttoBetrag - netto(bruttoBetrag,steuer));
  }

  /**
   * Rundet den uebergebenen Betrag auf 2 Stellen hinter dem Komma.
   * @param d der zu rundende Betrag.
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
 * Revision 1.2  2003/12/19 01:43:43  willuhn
 * @C small fixes
 *
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/