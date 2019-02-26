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
