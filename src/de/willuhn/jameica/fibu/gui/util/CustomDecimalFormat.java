/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/util/CustomDecimalFormat.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/06/07 15:45:15 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;

import de.willuhn.jameica.system.Application;

/**
 * Custom-Decimalformat
 */
public class CustomDecimalFormat extends DecimalFormat
{
  /**
   * ct.
   */
  public CustomDecimalFormat()
  {
    super("###,###,##0.00",new DecimalFormatSymbols(Application.getConfig().getLocale()));
    setGroupingUsed(true);
  }

  /**
   * Nachformatieren fuer "-0,00".
   * @see java.text.DecimalFormat#format(double, java.lang.StringBuffer,
   *      java.text.FieldPosition)
   */
  public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition)
  {
    StringBuffer sb = super.format(number, result, fieldPosition);
    if (sb == null || sb.length() == 0)
      return sb;
    String s = sb.toString();
    if ("-0,00".equals(s))
    {
      sb.delete(0, sb.length());
      sb.append("0,00");
    }
    return sb;
  }
}

/*******************************************************************************
 * $Log: CustomDecimalFormat.java,v $
 * Revision 1.2  2010/06/07 15:45:15  willuhn
 * @N Erste Version der neuen UST-Voranmeldung mit Kennziffern aus der DB
 *
 * Revision 1.1  2007/03/06 15:22:36  willuhn
 * @C Anlagevermoegen in Auswertungen ignorieren, wenn Anfangsbestand bereits 0
 * @B Formatierungsfehler bei Betraegen ("-0,00")
 * @C Afa-Buchungen werden nun auch als GWG gebucht, wenn Betrag zwar groesser als GWG-Grenze aber Afa-Konto=GWG-Afa-Konto (laut Einstellungen)
 *
 ******************************************************************************/
