/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/IORegistry.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/11/12 16:27:27 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.util.ArrayList;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;

/**
 * Ueber diese Klasse koennen alle verfuegbaren Export-und Import Formate abgerufen werden.
 */
public class IORegistry
{

  // Liste der Export-Filter
  private static ArrayList exporters = null;

  // Liste der Importer
  private static ArrayList importers = null;

  static
  {
    Logger.info("looking for installed export filters");
    exporters = load(Exporter.class);
    Logger.info("looking for installed import filters");
    importers = load(Importer.class);
  }
  
  /**
   * Sucht im Classpath nach allen Importern/Exportern.
   * @param type zu ladender Typ.
   * @return Liste der gefundenen Importer/Exporter.
   */
  private static synchronized ArrayList load(Class type)
  {
    ArrayList l = new ArrayList();
    try
    {
      ClassFinder finder = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getClassLoader().getClassFinder();
      Class[] list = finder.findImplementors(type);
      if (list == null || list.length == 0)
        throw new ClassNotFoundException();

      // Initialisieren
      for (int i=0;i<list.length;++i)
      {
        try
        {
          IO io = (IO) list[i].newInstance();
          Logger.info("  " + io.getName() + " - " + list[i].getName());
          l.add(io);
        }
        catch (Exception e)
        {
          Logger.error("error while loading import/export filter " + list[i].getName(),e);
        }
      }

    }
    catch (ClassNotFoundException e)
    {
      Logger.warn("no filters found for type: " + type.getName());
    }
    return l;
  }

  /**
   * Liefert eine Liste aller verfuegbaren Export-Formate.
   * @return Export-Filter.
   */
  public static Exporter[] getExporters()
  {
    return (Exporter[]) exporters.toArray(new Exporter[exporters.size()]);
  }

  /**
   * Liefert eine Liste aller verfuegbaren Import-Formate.
   * @return Import-Filter.
   */
  public static Importer[] getImporters()
  {
    return (Importer[]) importers.toArray(new Importer[importers.size()]);
  }
  
}


/**********************************************************************
 * $Log: IORegistry.java,v $
 * Revision 1.2  2010/11/12 16:27:27  willuhn
 * @C Plugin-Classloader statt dem von Jameica verwenden
 *
 * Revision 1.1  2010-08-27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/