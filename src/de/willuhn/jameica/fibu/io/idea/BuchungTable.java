/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/idea/Attic/BuchungTable.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/26 13:56:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.idea;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
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
   * @see de.willuhn.jameica.fibu.io.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    DBIterator list = jahr.getHauptBuchungen();
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
    double steuer = b.getSteuer();

    List<String> line = new ArrayList<String>();
    line.add(b.getID());
    line.add(Fibu.DATEFORMAT.format(b.getDatum()));
    line.add(ks.getID());
    line.add(kh.getID());
    line.add(b.getText());
    line.add(Integer.toString(b.getBelegnummer()));
    line.add(Fibu.DECIMALFORMAT.format(b.getBetrag()));
    line.add(steuer == 0d ? "" : Fibu.DECIMALFORMAT.format(b.getSteuer()));
    return line;
  }

}


/**********************************************************************
 * $Log: BuchungTable.java,v $
 * Revision 1.1.2.1  2009/06/26 13:56:56  willuhn
 * @N IDEA-Export (Buchungen und Anfangsbestaende)
 *
 **********************************************************************/