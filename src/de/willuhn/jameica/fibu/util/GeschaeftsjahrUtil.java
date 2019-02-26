/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Statische Hilfsfunktionen fuer das Geschaeftsjahr.
 */
public class GeschaeftsjahrUtil
{
  /**
   * Prueft, ob sich das Datum vor der Mitte des Geschaeftsjahres befindet.
   * Ist das der Fall, kann nach der vereinfachten Halbjahresregel im Anschaffungsjahr fuer das ganze Jahr abgeschrieben werden.
   * @param start das Start-Datum des Geschaeftsjahres.
   * @param end das End-Datum des Geschaeftsjahres.
   * @param date das zu pruefende Datum.
   * @return true, wenn sich das Datum vor der Mitte des Jahres befindet.
   */
  public static boolean beforeMiddle(final Date start, final Date end, final Date date)
  {
    if (!within(start,end,date))
      return false;
    
    final Calendar cal = Calendar.getInstance();
    cal.setTime(start);
    cal.add(Calendar.MONTH,6);
    
    return cal.getTime().after(date);
  }
  
  /**
   * Prueft, ob sich das angegebene Datum im angegebenen Zeitraum befindet.
   * @param start Start-Datum
   * @param end End-Datum.
   * @param d das zu pruefende Datum.
   * @return true, wenn sich das Datum im Zeitraum befindet.
   */
  public static boolean within(final Date start, final Date end, final Date d)
  {
    if (start == null || end == null || d == null)
      return false;
    
    return ((d.after(start) || d.equals(start)) && // Nach oder identisch mit Beginn
            (d.before(end)  || d.equals(end)));    //  Vor oder identisch mit Ende
  }
}
