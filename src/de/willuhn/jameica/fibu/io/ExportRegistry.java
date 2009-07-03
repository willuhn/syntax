/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/ExportRegistry.java,v $
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

package de.willuhn.jameica.fibu.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;

/**
 * Registry, welche die verfuegbaren Export-Formate liefert. 
 */
public class ExportRegistry
{
  private static List<Export> list = null;

  /**
   * Liefert die Liste der Exporter.
   * @return Liste der Exporter.
   */
  public static synchronized List<Export> getExporters()
  {
    if (list == null)
    {
      list = new ArrayList<Export>();
      ClassFinder finder = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getClassLoader().getClassFinder();
      try
      {
        Class[] classes = finder.findImplementors(Export.class);
        for (int i=0;i<classes.length;++i)
        {
          try
          {
            list.add((Export)classes[i].newInstance());
          }
          catch (Exception e)
          {
            Logger.error("unable to load exporter " + classes[i].getName() + ", skipping",e);
          }
        }
        Collections.sort(list);
      }
      catch (ClassNotFoundException e)
      {
        Logger.warn("no file exporters foundd");
      }
    }
    return list;
  }

}


/**********************************************************************
 * $Log: ExportRegistry.java,v $
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/
