/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Fibu.java,v $
 * $Revision: 1.4 $
 * $Date: 2003/11/24 14:21:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.willuhn.jameica.Plugin;

/**
 * Basisklasse des Fibu-Plugins fuer das Jameica-Framework.
 * @author willuhn
 */
public class Fibu implements Plugin
{
  public static DateFormat DATEFORMAT       = new SimpleDateFormat("dd.MM.yyyy");
  public static DecimalFormat DECIMALFORMAT = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
  public static String CURRENCY             = "EUR";
  
  static {
    DECIMALFORMAT.applyPattern("#0.00");
  }

  /**
   * Initialisiert das Plugin.
   * @see de.willuhn.jameica.Plugin#init()
   */
  public void init()
  {
  }

  /**
   * Beendet das Plugin.
   * @see de.willuhn.jameica.Plugin#shutDown()
   */
  public void shutDown()
  {
  }

}

/*********************************************************************
 * $Log: Fibu.java,v $
 * Revision 1.4  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 * Revision 1.2  2003/11/14 00:49:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/13 00:36:12  willuhn
 * *** empty log message ***
 *
 **********************************************************************/