/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/IO.java,v $
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
 * Basis-Interface aller Importer und Exporter.
 */
public interface IO
{
  /**
   * Liefert einen sprechenden Namen des Exporters/Importers.
   * @return Name
   */
  public String getName();

  /**
   * Liefert eine Liste der von diesem unterstuetzten Datei-Formate.
   * @param objectType Art der zu exportierenden/importierenden Objekte.
   * Z.Bsb.: Buchung.class.
   * Abhaengig davon kann der Exporter/Importer eine unterschiedliche
   * Liste von Dateiformaten liefern, die er zu dieser Objektart unterstuetzt.
   * @return Liste der Export-Formate.
   */
  public IOFormat[] getIOFormats(Class objectType);
}


/*********************************************************************
 * $Log: IO.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 *********************************************************************/