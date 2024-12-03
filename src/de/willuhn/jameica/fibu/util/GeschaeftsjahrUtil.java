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
   * Liefert die Anzahl der Monate von einem zum anderen Datum.
   * Die Funktion liefert immer ganze Monate incl. des Monats aus "from".
   * Von Juli bis Dezember sind also 6 Monate - egal, welche Tage des Monats.
   * Die Funktion wird fuer die jahresanteilige Abschreibung verwendet.
   * @param from Start-Datum.
   * @param end End-Datum.
   * @return die Anzahl der Monate.
   */
  public static int getMonths(Date from, Date end)
  {
    if (from == null || end == null)
      return 0;
    
    int count = 0;
    final Calendar cal = Calendar.getInstance();
    cal.setTime(from);
    
    boolean ultimo = cal.get(Calendar.DATE) == cal.getActualMaximum(Calendar.DATE);
    while (count < 1000) // Groessere Zeitraeume waren Unsinn.
    {
      if (ultimo)
        cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DATE));
      
      Date test = cal.getTime();
      if (test.after(end))
        return count;
      
      cal.add(Calendar.MONTH,1);
      count++;
    }
    
    return count;
  }
  
  /**
   * Berechnet die Restnutzungsdauer.
   * @param anschaffung Anschaffungsdatum.
   * @param nutzungsdauer Nutzungsdauer in Jahren.
   * @param gjStart Beginn des abzuschliessenden Geschaeftjahres.
   * @param gjEnd Ende des abzuschliessenden Geschaeftsjahres.
   * @return Anzahl der Monate der Restnutzungsdauer.
   */
  public static int getRestnutzungsdauer(final Date anschaffung, int nutzungsdauer, final Date gjStart, final Date gjEnd)
  {
    if (anschaffung == null || gjStart == null || gjEnd == null || nutzungsdauer == 0)
      return 0;

    // Datum des Nutzungsende ermitteln
    final Calendar cal = Calendar.getInstance();
    cal.setTime(anschaffung);
    cal.add(Calendar.YEAR,nutzungsdauer);
    final Date end = cal.getTime();
    
    // Im Anschaffungsjahr haben wir die volle Restlaufzeit.
    if (within(gjStart,gjEnd,anschaffung))
      return Math.max(getMonths(anschaffung,end) - 1,0); // Ein Monat abziehen, weil der letzte nicht mitzaehlt
    
    // In den Folgejahren nicht mehr ab Anschaffungsdatum sondern nur noch ab Geschaeftsjahresbeginn
    return Math.max(getMonths(gjStart,end) - 1,0); // Ein Monat abziehen, weil der letzte nicht mitzaehlt
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
