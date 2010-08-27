/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/VelocityReportData.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 10:18:14 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.io.OutputStream;
import java.util.HashMap;

/**
 * Kapselt die Daten eines Reports.
 */
public class VelocityReportData
{
  /**
   * Dateiname des Velocity-Templates.
   */
  private String template     = null;
  
  /**
   * Titel der Auswertung.
   */
  private String title        = null;
  
  /**
   * Die zu druckenden Objekte.
   */
  private HashMap objects    = new HashMap();
  
  /**
   * Das Ausgabe-Ziel.
   */
  private OutputStream target = null;

  /**
   * Liefert die zu druckenden Objekte mit dem genannten Alias.
   * @param name Alias-Name.
   * @return die Objekte.
   */
  public Object getObjects(String name)
  {
    return this.objects.get(name);
  }
  
  /**
   * Liefert das Ausgabe-Target.
   * @return Target.
   */
  public OutputStream getTarget()
  {
    return this.target;
  }
  
  /**
   * Liefert den Dateinamen des Velocity-Templates.
   * @return Dateiname des Templates.
   */
  public String getTemplate()
  {
    return this.template;
  }
  
  /**
   * Liefert den Titel des Exports.
   * @return Titel.
   */
  public String getTitle()
  {
    return this.title;
  }
  
  /**
   * Speichert die zu exportierenden Daten.
   * @param name Alias-Name.
   * @param object Nutzdaten.
   */
  public void addObject(String name, Object object)
  {
    this.objects.put(name, object);
  }
  
  /**
   * Speichert das Ausgabe-Target.
   * @param target
   */
  public void setTarget(OutputStream target)
  {
    this.target = target;
  }
  
  /**
   * Speichert den Dateinamen des Velocity-Templates.
   * @param template
   */
  public void setTemplate(String template)
  {
    this.template = template;
  }
  
  /**
   * Speichert den Titel des Reports.
   * @param title
   */
  public void setTitle(String title)
  {
    this.title = title;
  }
}


/*********************************************************************
 * $Log: VelocityReportData.java,v $
 * Revision 1.1  2010/08/27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 * Revision 1.1  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 **********************************************************************/