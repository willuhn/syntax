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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;


/**
 * Abstrakte Basis-Implementierung eines Reports.
 */
public abstract class AbstractReport implements Report
{
  final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData d = new ReportData();
    d.setNeedGeschaeftsjahr(true);
    d.setNeedKonto(true);
    d.setNeedDatum(true);
    return d;
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (!(o instanceof Report))
      return -1;
    
    // Alphabetisch nach Name sortieren
    String name = ((Report)o).getName();
    return this.getName().compareTo(name);
  }
}


/**********************************************************************
 * $Log: AbstractReport.java,v $
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
