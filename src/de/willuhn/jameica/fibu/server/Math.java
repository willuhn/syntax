/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Math.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/16 02:27:33 $
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
 * Hilfs-Klasse die ein paar methamatische Berechnungen enthaelt.
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
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/