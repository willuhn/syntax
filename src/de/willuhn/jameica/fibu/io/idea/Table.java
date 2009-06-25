/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/idea/Attic/Table.java,v $
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
 * Revision 1.1.2.1  2009/06/25 16:33:17  willuhn
 * @N Erste CSV-Daten fuer Steuer, Kontoart, Kontotyp und Konto
 *
 **********************************************************************/
