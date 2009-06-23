/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/Export.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/23 16:53:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Interface fuer alle unterstuetzten Exporte.
 * Wenn die Liste der vefuegbaren Auswertungen um ein
 * Element erweitert werden soll, muss es dieses Interface
 * hier implementieren.
 */
public interface Export extends Comparable
{
  /**
   * Liefert einen sprechenden Namen fuer den Export.
   * @return Sprechender Name fuer den Export.
   */
  public String getName();
  
  /**
   * Fuehrt den Export aus.
   * @param data die Meta-Daten.
   * @param monitor Fortschritts-Monitor.
   * @throws ApplicationException
   * @throws OperationCanceledException
   */
  public void doExport(ExportData data, ProgressMonitor monitor) throws ApplicationException, OperationCanceledException;
  
  /**
   * Kann vom Export implementiert werden, wenn bereits Vorauswahlen getroffen werden sollen.
   * @return Preset-Objekt mit Vorauswahlen fuer den User.
   */
  public ExportData createPreset();
  
}


/**********************************************************************
 * $Log: Export.java,v $
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/
