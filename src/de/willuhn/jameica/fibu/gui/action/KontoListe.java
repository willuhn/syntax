/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/KontoListe.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/16 17:39:24 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Action zum Laden der Liste der Konten.
 * @author willuhn
 */
public class KontoListe extends BaseAction
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (!check())
    {
      super.handleAction(context);
      return;
    }

    GUI.startView(de.willuhn.jameica.fibu.gui.views.KontoListe.class,context);
  }

}


/*********************************************************************
 * $Log: KontoListe.java,v $
 * Revision 1.4  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 *********************************************************************/