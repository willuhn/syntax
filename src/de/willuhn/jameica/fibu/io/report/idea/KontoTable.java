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
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;

/**
 * Implementierung fuer die Konto-Tabelle.
 */
public class KontoTable implements Table
{
  /**
   * @see de.willuhn.jameica.fibu.io.report.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception
  {
    DBIterator list = jahr.getKontenrahmen().getKonten();

    List<List<String>> result = new ArrayList<List<String>>();
    while (list.hasNext())
    {
      Konto k     = (Konto) list.next();
      Kontoart ka = k.getKontoArt();
      Kontotyp kt = k.getKontoTyp();
      Steuer s    = k.getSteuer();

      List<String> line = new ArrayList<String>();
      line.add(k.getID());
      line.add(ka == null ? "" : ka.getID());
      line.add(kt == null ? "" : kt.getID());
      line.add(s == null ? "" : s.getID());
      line.add(k.getName());
      line.add(k.getKontonummer());

      result.add(line);
    }
    return result;
  }

}


/**********************************************************************
 * $Log: KontoTable.java,v $
 * Revision 1.1  2010/08/27 10:18:15  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/25 16:33:17  willuhn
 * @N Erste CSV-Daten fuer Steuer, Kontoart, Kontotyp und Konto
 *
 **********************************************************************/
