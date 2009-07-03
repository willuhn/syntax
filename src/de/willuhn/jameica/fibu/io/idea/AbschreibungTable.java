/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/idea/Attic/AbschreibungTable.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/07/03 10:52:18 $
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

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;

/**
 * Implementierung fuer die Abschreibungs-Tabelle.
 */
public class AbschreibungTable implements Table
{
  /**
   * @see de.willuhn.jameica.fibu.io.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    // Die Abschreibungen existieren nicht in einer separaten Tabelle
    // sondern werden on-the-fly aus dem Anlagevermoegen berechnet
    // und anschliessend als regulaere Buchungen gespeichert. Um sie
    // hier dennoch zu exportieren, holen wir uns das Anlagevermoegen
    // und von dort dann die Abschreibungsbuchungen
    
    // Die erzeugte Tabelle enthaelt nicht nur die Abschreibungen des
    // uebergebenen Geschaeftsjahres sondern alle Abschreibungen BIS
    // incl. des Geschaeftsjahres.
    DBIterator list = Settings.getDBService().createList(Anlagevermoegen.class);
    list.addFilter("mandant_id = " + jahr.getMandant().getID());
    list.setOrder("order by anschaffungsdatum desc");

    List<List<String>> result = new ArrayList<List<String>>();
    while (list.hasNext())
    {
      Anlagevermoegen av = (Anlagevermoegen) list.next();
      GenericIterator afa = av.getAbschreibungen(jahr);
      while (afa.hasNext())
      {
        Abschreibung ab = (Abschreibung) afa.next();
        AbschreibungsBuchung b = ab.getBuchung();

        if (Double.compare(b.getBetrag(),0.01) < 0)
          continue; // in alten SynTAX-Versionen wurden u.U. 0,- Abschreibungen gebucht - die ueberspringen wir
        
        List<String> line = new ArrayList<String>();
        line.add(ab.getID());
        line.add(av.getID());
        line.add(b.getID());
        line.add(ab.isSonderabschreibung() ? "1" : "0");
        result.add(line);
      }
    }
    return result;
  }

}


/**********************************************************************
 * $Log: AbschreibungTable.java,v $
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/30 16:00:59  willuhn
 * @N Erste komplette Version des Exports
 *
 **********************************************************************/