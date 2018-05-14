/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;

/**
 * Registry, welche die verfuegbaren Report-Formate liefert. 
 */
public class ReportRegistry
{
  private static List<Report> list = null;

  /**
   * Liefert die Liste der Reports.
   * @return Liste der Reports.
   */
  public static synchronized List<Report> getReports()
  {
    if (list == null)
    {
      list = new ArrayList<Report>();
      ClassFinder finder = Application.getPluginLoader().getManifest(Fibu.class).getClassLoader().getClassFinder();
      try
      {
        Class[] classes = finder.findImplementors(Report.class);
        for (int i=0;i<classes.length;++i)
        {
          try
          {
            list.add((Report)classes[i].newInstance());
          }
          catch (Exception e)
          {
            Logger.error("unable to load report " + classes[i].getName() + ", skipping",e);
          }
        }
        Collections.sort(list);
      }
      catch (ClassNotFoundException e)
      {
        Logger.warn("no reports foundd");
      }
    }
    return list;
  }

}


/**********************************************************************
 * $Log: ReportRegistry.java,v $
 * Revision 1.2  2012/03/28 22:28:16  willuhn
 * @N Einfuehrung eines neuen Interfaces "Plugin", welches von "AbstractPlugin" implementiert wird. Es dient dazu, kuenftig auch Jameica-Plugins zu unterstuetzen, die selbst gar keinen eigenen Java-Code mitbringen sondern nur ein Manifest ("plugin.xml") und z.Bsp. Jars oder JS-Dateien. Plugin-Autoren muessen lediglich darauf achten, dass die Jameica-Funktionen, die bisher ein Object vom Typ "AbstractPlugin" zuruecklieferten, jetzt eines vom Typ "Plugin" liefern.
 * @C "getClassloader()" verschoben von "plugin.getRessources().getClassloader()" zu "manifest.getClassloader()" - der Zugriffsweg ist kuerzer. Die alte Variante existiert weiterhin, ist jedoch als deprecated markiert.
 *
 * Revision 1.1  2010-08-27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/
