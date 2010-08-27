/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/idea/Table.java,v $
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

import java.util.List;

import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;

/**
 * Interface fuer eine einzelne Tabelle fuer den IDEA-Export.
 * Jede Implementierung muss einen parameterlosen Konstruktor
 * (Bean-Spec) haben.
 */
public interface Table
{
  /**
   * Liefert die zu exportierenden Zeilen.
   * @param jahr
   * @return Liste der Zeilen.
   * @throws Exception
   */
  public List<List<String>> getLines(Geschaeftsjahr jahr) throws Exception;
}


/**********************************************************************
 * $Log: Table.java,v $
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
