/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report.idea;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Implementierung fuer die Buchungs-Tabelle.
 */
public class BuchungTable implements Table
{
  /**
   * @see de.willuhn.jameica.fibu.io.report.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    DBIterator list = jahr.getHauptBuchungen(true);
    list.setOrder("order by belegnummer,id");

    List<List<String>> result = new ArrayList<List<String>>();
    while (list.hasNext())
    {
      Buchung b = (Buchung) list.next();
      result.add(createLine(b));
      
      // Mal schauen, ob wir Hilfsbuchungen (z.Bsp. fuer die Steuer) haben
      DBIterator hbList = b.getHilfsBuchungen();
      while (hbList.hasNext())
        result.add(createLine((HilfsBuchung)hbList.next()));
    }
    return result;
  }
  
  /**
   * Erzeugt die Zeile fuer eine einzelne Buchung.
   * @param b die Buchung.
   * @return die erzeugte Zeile.
   * @throws Exception
   */
  private List<String> createLine(BaseBuchung b) throws Exception
  {
    Konto ks      = b.getSollKonto();
    Konto kh      = b.getHabenKonto();
    double steuer = b.getSteuerSatz();

    List<String> line = new ArrayList<String>();
    line.add(b.getID());
    line.add(Settings.DATEFORMAT.format(b.getDatum()));
    line.add(ks.getID());
    line.add(kh.getID());
    line.add(b.getText());
    line.add(Integer.toString(b.getBelegnummer()));
    line.add(Settings.DECIMALFORMAT.format(b.getBetrag()));
    line.add(steuer == 0d ? "" : Settings.DECIMALFORMAT.format(b.getSteuerSatz()));
    return line;
  }

}


/**********************************************************************
 * $Log: BuchungTable.java,v $
 * Revision 1.1  2010/08/27 10:18:15  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.3  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/26 13:56:56  willuhn
 * @N IDEA-Export (Buchungen und Anfangsbestaende)
 *
 **********************************************************************/