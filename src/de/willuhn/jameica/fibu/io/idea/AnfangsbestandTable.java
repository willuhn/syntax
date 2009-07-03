/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/idea/Attic/AnfangsbestandTable.java,v $
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
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Implementierung fuer die Tabelle mit den Anfangsbestaenden der Konten.
 */
public class AnfangsbestandTable implements Table
{
  /**
   * @see de.willuhn.jameica.fibu.io.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    DBIterator list = jahr.getAnfangsbestaende();

    List<List<String>> result = new ArrayList<List<String>>();
    while (list.hasNext())
    {
      Anfangsbestand ab = (Anfangsbestand) list.next();
      Konto k = ab.getKonto();

      List<String> line = new ArrayList<String>();
      line.add(ab.getID());
      line.add(k == null ? "" : k.getID());
      line.add(Fibu.DECIMALFORMAT.format(ab.getBetrag()));
      result.add(line);
    }
    return result;
  }

}


/**********************************************************************
 * $Log: AnfangsbestandTable.java,v $
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/26 13:56:56  willuhn
 * @N IDEA-Export (Buchungen und Anfangsbestaende)
 *
 **********************************************************************/