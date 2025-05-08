/**********************************************************************
 *
 * Copyright (c) 2023 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;

/**
 * Sonderbehandlung für den Import von Haupt-Buchungen.
 */
public class XMLBuchungImporter extends XMLImporter
{
  /**
   * @see de.willuhn.jameica.fibu.io.XMLImporter#prePersist(de.willuhn.datasource.rmi.DBObject)
   */
  @Override
  protected void prePersist(DBObject o) throws Exception
  {
    if (o instanceof Buchung)
    {
      // Automatisch Buchungsnummer generieren, wenn sie fehlt
      // Bei Hilfsbuchungen ist das nicht nötig - die kriegen beim Speichern
      // die Buchungsnummer der Hauptbuchung mit
      Buchung b = (Buchung) o;
      b.setBelegnummer(b.getBelegnummer());
      
      // Wir importieren immer im aktuellen Geschäftsjahr - nicht in dem, aus dem der Export stammt
      b.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
    }
    super.prePersist(o);
  }

  /**
   * @see de.willuhn.jameica.fibu.io.IO#getIOFormats(java.lang.Class)
   */
  public IOFormat[] getIOFormats(Class objectType)
  {
    // Nur für Haupt-Buchungen anbieten
    if (!Buchung.class.isAssignableFrom(objectType))
      return null;
    
    IOFormat f = new IOFormat() {
      public String getName()
      {
        return XMLBuchungImporter.this.getName();
      }

      /**
       * @see de.willuhn.jameica.fibu.io.IOFormat#getFileExtensions()
       */
      public String[] getFileExtensions()
      {
        return new String[] {"*.xml"};
      }
    };
    return new IOFormat[] { f };
  }
}
