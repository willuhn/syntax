/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/Report.java,v $
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

import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Interface fuer alle unterstuetzten Reports.
 * Wenn die Liste der vefuegbaren Auswertungen um ein
 * Element erweitert werden soll, muss es dieses Interface
 * hier implementieren.
 */
public interface Report extends Comparable
{
  /**
   * Liefert einen sprechenden Namen fuer den Report.
   * @return Sprechender Name fuer den Report.
   */
  public String getName();
  
  /**
   * Fuehrt den Report aus.
   * @param data die Meta-Daten.
   * @param monitor Fortschritts-Monitor.
   * @throws ApplicationException
   * @throws OperationCanceledException
   */
  public void doReport(ReportData data, ProgressMonitor monitor) throws ApplicationException, OperationCanceledException;
  
  /**
   * Kann vom Report implementiert werden, wenn bereits Vorauswahlen getroffen werden sollen.
   * @return Preset-Objekt mit Vorauswahlen fuer den User.
   */
  public ReportData createPreset();
  
}


/**********************************************************************
 * $Log: Report.java,v $
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
