/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/IOFormat.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 11:19:40 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;


/**
 * Dieses Interface kapselt die Datei-Formate.
 * Jeder Importer oder Exporter unterstuetzt ein oder mehrere
 * Dateiformate. Ueber
 * <code>de.willuhn.jameica.fibu.io.IO#getIOFormats(Class type)</code>
 * kann ein Importer/Exporter abgefragt werden, welche Formate
 * er unterstuetzt.
 */
public interface IOFormat
{
  /**
   * Liefert einen sprechenden Namen fuer das Datei-Format.
   * Zum Beispiel &quotCSV-Datei&quot;
   * @return Sprechender Name des Datei-Formats.
   */
  public String getName();

  /**
   * Liefert die Datei-Endungen des Formats.
   * Zum Beispiel "*.csv" oder "*.txt".
   * @return Datei-Endung.
   */
  public String[] getFileExtensions();
}


/*********************************************************************
 * $Log: IOFormat.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/