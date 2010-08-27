/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/ReportRegistry.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 10:18:14 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
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
      ClassFinder finder = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getClassLoader().getClassFinder();
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
 * Revision 1.1  2010/08/27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/
