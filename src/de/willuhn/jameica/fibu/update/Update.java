/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/update/Attic/Update.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/12/27 14:42:23 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.update;

import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Interface fuer ein Update.
 */
public interface Update
{
  /**
   * Fuehrt das Update durch.
   * @param monitor
   * @param oldVersion
   * @param newVersion
   * @throws ApplicationException
   */
  public void update(ProgressMonitor monitor, double oldVersion, double newVersion) throws ApplicationException;

}


/*********************************************************************
 * $Log: Update.java,v $
 * Revision 1.1  2006/12/27 14:42:23  willuhn
 * @N Update fuer MwSt.-Erhoehung
 *
 **********************************************************************/