/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/idea/Attic/SteuerTable.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/25 16:33:17 $
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
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;

/**
 * Implementierung fuer die Steuer-Tabelle.
 */
public class SteuerTable implements Table
{
  /**
   * @see de.willuhn.jameica.fibu.io.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    DBIterator list = Settings.getDBService().createList(Steuer.class);
    list.addFilter("mandant_id is null or mandant_id = " + jahr.getMandant().getID());
    list.setOrder("order by name");
    Kontenrahmen soll = jahr.getKontenrahmen();

    List<List<String>> result = new ArrayList<List<String>>();
    while (list.hasNext())
    {
      Steuer s = (Steuer) list.next();
      Konto k = s.getSteuerKonto(); // unvollstaendiger Steuersatz ohne Sammelkonto
      if (k == null)
        continue;
      
      // Checken, ob der Steuersatz im aktuellen Kontenrahmen definiert ist
      Kontenrahmen ist = k.getKontenrahmen();
      if (!soll.equals(ist))
        continue;

      // Scheint alles zu passen. Den Steuersatz nehmen wir.
      List<String> line = new ArrayList<String>();
      line.add(s.getID());
      line.add(s.getName());
      line.add(Fibu.DECIMALFORMAT.format(s.getSatz()));
      line.add(k.getID());
      
      result.add(line);
    }
    return result;
  }

}


/**********************************************************************
 * $Log: SteuerTable.java,v $
 * Revision 1.1.2.1  2009/06/25 16:33:17  willuhn
 * @N Erste CSV-Daten fuer Steuer, Kontoart, Kontotyp und Konto
 *
 **********************************************************************/
