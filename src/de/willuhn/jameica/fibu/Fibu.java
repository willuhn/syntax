/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Fibu.java,v $
 * $Revision: 1.9 $
 * $Date: 2003/12/11 21:00:35 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;

import java.text.*;
import java.util.Locale;
import java.util.jar.JarFile;

import de.willuhn.jameica.Plugin;

/**
 * Basisklasse des Fibu-Plugins fuer das Jameica-Framework.
 * @author willuhn
 */
public class Fibu implements Plugin
{

  public static DateFormat DATEFORMAT       = new SimpleDateFormat("dd.MM.yyyy");
  public static DateFormat FASTDATEFORMAT   = new SimpleDateFormat("ddMMyyyy");
  public static DecimalFormat DECIMALFORMAT = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
  
  public static String[] WEEKDAYS = new String[] {
    "Sonntag",
    "Montag",
    "Dienstag",
    "Mittwoch",
    "Donnerstag",
    "Freitag",
    "Sonnabend"
  };

  public static int YEAR_MIN                = 1950;
  public static int YEAR_MAX                = 2020;

  static {
    DECIMALFORMAT.applyPattern("#0.00");
  }

  /**
   * ct.
   * @param jar
   */
  public Fibu(JarFile jar)
  {
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

  /**
   * @see de.willuhn.jameica.Plugin#getName()
   */
  public String getName()
  {
    return "Fibu für Jameica";
  }

  /**
   * @see de.willuhn.jameica.Plugin#getVersion()
   */
  public double getVersion()
  {
    return 1.0;
  }

}

/*********************************************************************
 * $Log: Fibu.java,v $
 * Revision 1.9  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.8  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/25 01:23:19  willuhn
 * @N added Menu shortcuts
 *
 * Revision 1.5  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
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