/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/update/Attic/Update_1_3_to_1_4.java,v $
 * $Revision: 1.3 $
 * $Date: 2007/10/08 22:54:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.update;

import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Fuehrt das Update von Syntax 1.3 nach 1.4 durch.
 */
public class Update_1_3_to_1_4 implements Update
{

  /**
   * @see de.willuhn.jameica.fibu.update.Update#update(de.willuhn.util.ProgressMonitor, double, double)
   */
  public void update(ProgressMonitor monitor, double oldVersion, double newVersion) throws ApplicationException
  {
    if (oldVersion != 1.3d && newVersion != 1.4d)
    {
      Logger.info("skip update " + this.getClass().getName());
      return;
    }
  }
}


/*********************************************************************
 * $Log: Update_1_3_to_1_4.java,v $
 * Revision 1.3  2007/10/08 22:54:47  willuhn
 * @N Kopieren eines kompletten Kontenrahmen auf einen Mandanten
 *
 **********************************************************************/