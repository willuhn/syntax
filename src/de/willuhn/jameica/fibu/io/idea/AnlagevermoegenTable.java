/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/idea/Attic/AnlagevermoegenTable.java,v $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Implementierung fuer die Tabelle mit dem Anlagevermoegen.
 */
public class AnlagevermoegenTable implements Table
{
  /**
   * @see de.willuhn.jameica.fibu.io.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    DBIterator list = Settings.getDBService().createList(Anlagevermoegen.class);
    list.addFilter("mandant_id = " + jahr.getMandant().getID());
    list.setOrder("order by anschaffungsdatum desc");

    List<List<String>> result = new ArrayList<List<String>>();
    while (list.hasNext())
    {
      Anlagevermoegen av = (Anlagevermoegen) list.next();
      Konto k = av.getKonto();

      List<String> line = new ArrayList<String>();
      line.add(av.getID());
      line.add(av.getName());
      line.add(Fibu.DECIMALFORMAT.format(av.getAnschaffungskosten()));
      line.add(Fibu.DECIMALFORMAT.format(av.getRestwert(jahr)));
      line.add(Fibu.DATEFORMAT.format(av.getAnschaffungsdatum()));
      line.add(k == null ? "" : k.getID());
      line.add(Integer.toString(av.getNutzungsdauer()));
      result.add(line);
    }
    return result;
  }

}


/**********************************************************************
 * $Log: AnlagevermoegenTable.java,v $
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/30 16:00:59  willuhn
 * @N Erste komplette Version des Exports
 *
 **********************************************************************/