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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit-Tests fuer GeschaeftsjahrUtil.
 */
public class TestGeschaeftsjahrUtil
{
  private final static DateFormat DF = new SimpleDateFormat("dd.MM.yyyy");

  /**
   * Testet, ob fuer Testdaten korrekt ermittelt wird, ob sie sich vor der Mitte des Geschaeftsjahres befinden.
   * @throws Exception
   */
  @Test
  public void testBeforeMiddle() throws Exception
  {
    Assert.assertTrue(this.prepareBeforeMiddle("01.01.2018","31.12.2018","01.01.2018"));
    Assert.assertTrue(this.prepareBeforeMiddle("01.01.2018","31.12.2018","01.06.2018"));
    Assert.assertTrue(this.prepareBeforeMiddle("01.01.2018","31.12.2018","30.06.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018","31.12.2018","01.07.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018","31.12.2018","30.07.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018","31.12.2018","31.12.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018","31.12.2018","31.12.2017"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018","31.12.2018","01.01.2019"));

    Assert.assertFalse(this.prepareBeforeMiddle(null,        "31.12.2018","01.06.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018",null,        "01.06.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.01.2018","31.12.2018",null));

    // Krummes Geschaeftsjahr
    Assert.assertTrue(this.prepareBeforeMiddle("01.07.2018","30.06.2019","01.07.2018"));
    Assert.assertTrue(this.prepareBeforeMiddle("01.07.2018","30.06.2019","01.12.2018"));
    Assert.assertTrue(this.prepareBeforeMiddle("01.07.2018","30.06.2019","31.12.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.07.2018","30.06.2019","01.01.2019"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.07.2018","30.06.2019","01.06.2019"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.07.2018","30.06.2019","30.06.2019"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.07.2018","30.06.2019","30.06.2018"));
    Assert.assertFalse(this.prepareBeforeMiddle("01.07.2018","30.06.2019","01.07.2019"));
  }
  
  /**
   * Testet die Berechnung der Anzahl der Monate.
   * @throws Exception
   */
  @Test
  public void testMonths() throws Exception
  {
    Assert.assertEquals(12,prepareMonths("01.01.2017","31.12.2017"));
    Assert.assertEquals(6,prepareMonths("01.07.2017","31.12.2017"));
    Assert.assertEquals(6,prepareMonths("01.07.2017","01.12.2017"));
    Assert.assertEquals(11,prepareMonths("01.01.2017","30.11.2017"));
    Assert.assertEquals(11,prepareMonths("31.01.2017","30.11.2017"));
    Assert.assertEquals(1,prepareMonths("01.12.2017","31.12.2017"));
    Assert.assertEquals(8,prepareMonths("23.11.2017","30.06.2018"));
    Assert.assertEquals(20,prepareMonths("23.11.2017","30.06.2019"));
    Assert.assertEquals(0,prepareMonths(null,"30.06.2019"));
    Assert.assertEquals(0,prepareMonths("23.11.2017",null));
    Assert.assertEquals(1000,prepareMonths("23.11.1700","30.06.2300"));
  }

  /**
   * Testet die Berechnung der Restnutzungsdauer.
   * @throws Exception
   */
  @Test
  public void testRestnutzungsdauer() throws Exception
  {
    //////////////////////////////////
    // Einfachster Fall - nur ganze Jahre
    Assert.assertEquals(12,prepareRnd("01.01.2017",1,"01.01.2017","31.12.2017"));
    Assert.assertEquals(120,prepareRnd("01.01.2017",10,"01.01.2017","31.12.2017"));
    Assert.assertEquals(108,prepareRnd("01.01.2017",10,"01.01.2018","31.12.2018"));
    Assert.assertEquals(12,prepareRnd("01.01.2017",10,"01.01.2026","31.12.2026"));
    
    Assert.assertEquals(12,prepareRnd("31.01.2017",1,"01.01.2017","31.12.2017"));
    Assert.assertEquals(120,prepareRnd("31.01.2017",10,"01.01.2017","31.12.2017"));
    Assert.assertEquals(108,prepareRnd("31.01.2017",10,"01.01.2018","31.12.2018"));
    Assert.assertEquals(12,prepareRnd("31.01.2017",10,"01.01.2026","31.12.2026"));
    //////////////////////////////////
    
    //////////////////////////////////
    // Etwas schwieriger: In der Mitte des Jahres angeschafft
    
    // Jahr 1
    Assert.assertEquals(120,prepareRnd("01.07.2017",10,"01.01.2017","31.12.2017"));
    
    // Jahr 2 - im ersten Jahr waren nur 6 Monate
    Assert.assertEquals(114,prepareRnd("01.07.2017",10,"01.01.2018","31.12.2018"));
    
    // Jahr 3
    Assert.assertEquals(102,prepareRnd("01.07.2017",10,"01.01.2019","31.12.2019"));
    
    // Jahr 10
    Assert.assertEquals(6,prepareRnd("01.07.2017",10,"01.01.2027","31.12.2027"));
    
    // Jahr 1
    Assert.assertEquals(12,prepareRnd("01.07.2017",1,"01.01.2017","31.12.2017"));
    // Jahr 2 - im ersten Jahr waren nur 6 Monate
    Assert.assertEquals(6,prepareRnd("01.07.2017",1,"01.01.2018","31.12.2018"));

    //
    //////////////////////////////////
    
    //////////////////////////////////
    // Noch schwieriger: "Krummes" Geschaeftsjahr
    Assert.assertEquals(120,prepareRnd("01.07.2017",10,"01.07.2017","30.06.2018"));
    Assert.assertEquals(108,prepareRnd("01.07.2017",10,"01.07.2018","30.06.2019"));
    Assert.assertEquals(96, prepareRnd("01.07.2017",10,"01.07.2019","30.06.2020"));
    Assert.assertEquals(12, prepareRnd("01.07.2017",10,"01.07.2026","30.06.2027"));
    
    Assert.assertEquals(120,prepareRnd("30.07.2017",10,"01.07.2017","30.06.2018"));
    Assert.assertEquals(108,prepareRnd("30.07.2017",10,"01.07.2018","30.06.2019"));
    Assert.assertEquals(96, prepareRnd("30.07.2017",10,"01.07.2019","30.06.2020"));
    Assert.assertEquals(12, prepareRnd("30.07.2017",10,"01.07.2026","30.06.2027"));
    
    Assert.assertEquals(12,prepareRnd("30.07.2017",1,"01.07.2017","30.06.2018"));
    Assert.assertEquals(0,prepareRnd("30.07.2017",1,"01.07.2018","30.06.2019"));
    //
    //////////////////////////////////

  
    //////////////////////////////////
    // Ganz schwierig: "Krummes" Geschaeftsjahr und mitten im Jahr gekauft

    Assert.assertEquals(12,prepareRnd("23.11.2017",1,"01.07.2017","30.06.2018"));
    // Jahr 2 - im ersten Jahr waren nur 8 Monate
    Assert.assertEquals(4,prepareRnd("23.11.2017",1,"01.07.2018","30.06.2019"));

    // Jahr 1
    Assert.assertEquals(120,prepareRnd("23.11.2017",10,"01.07.2017","30.06.2018"));
    // Jahr 2 - im ersten Jahr waren nur 8 Monate
    Assert.assertEquals(112,prepareRnd("23.11.2017",10,"01.07.2018","30.06.2019"));
    Assert.assertEquals(100,prepareRnd("23.11.2017",10,"01.07.2019","30.06.2020"));
    Assert.assertEquals(88,prepareRnd("23.11.2017",10,"01.07.2020","30.06.2021"));
    Assert.assertEquals(76,prepareRnd("23.11.2017",10,"01.07.2021","30.06.2022"));
    Assert.assertEquals(64,prepareRnd("23.11.2017",10,"01.07.2022","30.06.2023"));
    Assert.assertEquals(52,prepareRnd("23.11.2017",10,"01.07.2023","30.06.2024"));
    Assert.assertEquals(40,prepareRnd("23.11.2017",10,"01.07.2024","30.06.2025"));
    Assert.assertEquals(28,prepareRnd("23.11.2017",10,"01.07.2025","30.06.2026"));
    Assert.assertEquals(16,prepareRnd("23.11.2017",10,"01.07.2026","30.06.2027"));
    Assert.assertEquals(4,prepareRnd("23.11.2017",10,"01.07.2027","30.06.2028"));
    Assert.assertEquals(0,prepareRnd("23.11.2017",10,"01.07.2028","30.06.2029"));
    //
    //////////////////////////////////
  }

  /**
   * Ermittelt die Anzahl der Monate fuer einen Testdatensatz.
   * @param from Start-Datum.
   * @param to End-Datum.
   * @return Anzahl der Monate.
   * @throws Exception
   */
  private int prepareRnd(String date, int jahre, String from, String to) throws Exception
  {
    final Date d     = date != null ? DF.parse(date) : null;
    final Date start = from != null ? DF.parse(from) : null;
    final Date end   = to   != null ? DF.parse(to) : null;
    return GeschaeftsjahrUtil.getRestnutzungsdauer(d,jahre,start,end);
  }

  /**
   * Ermittelt die Anzahl der Monate fuer einen Testdatensatz.
   * @param from Start-Datum.
   * @param to End-Datum.
   * @return Anzahl der Monate.
   * @throws Exception
   */
  private int prepareMonths(String from, String to) throws Exception
  {
    final Date start = from != null ? DF.parse(from) : null;
    final Date end   = to   != null ? DF.parse(to) : null;
    return GeschaeftsjahrUtil.getMonths(start,end);
  }
  
  /**
   * Testet fuer einen Satz Daten, ob sich das Datum vor der Mitte des Geschaeftsjahres befindet oder nicht.
   * @param from Startdatum des Geschaeftsjahres im Format dd.mm.yyyy.
   * @param to Enddatum des Geschaeftsjahres im Format dd.mm.yyyy.
   * @param d Test-Datum im Format dd.mm.yyyy.
   * @return true, wenn sich das Test-Datum vor der Mitte des Geschaeftsjahres befindet.
   * @throws Exception
   */
  private boolean prepareBeforeMiddle(String from, String to, String d) throws Exception
  {
    final Date start = from != null ? DF.parse(from) : null;
    final Date end   = to   != null ? DF.parse(to) : null;
    final Date date  = d    != null ? DF.parse(d) : null;
    return GeschaeftsjahrUtil.beforeMiddle(start,end,date);
  }

}
