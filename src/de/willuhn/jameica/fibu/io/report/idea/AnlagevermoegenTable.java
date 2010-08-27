/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/idea/AnlagevermoegenTable.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 10:18:15 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report.idea;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
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
   * @see de.willuhn.jameica.fibu.io.report.idea.Table#getLines(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
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
      line.add(Settings.DECIMALFORMAT.format(av.getAnschaffungskosten()));
      line.add(Settings.DECIMALFORMAT.format(av.getRestwert(jahr)));
      line.add(Settings.DATEFORMAT.format(av.getAnschaffungsdatum()));
      line.add(k == null ? "" : k.getID());
      line.add(Integer.toString(av.getNutzungsdauer()));
      result.add(line);
    }
    return result;
  }

}


/**********************************************************************
 * $Log: AnlagevermoegenTable.java,v $
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
 * Revision 1.1.2.1  2009/06/30 16:00:59  willuhn
 * @N Erste komplette Version des Exports
 *
 **********************************************************************/