/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/BaseAction.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/15 23:38:27 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.gui.Action;

/**
 * Basis-Action, um sicherzustellen, dass der Mandant existiert.
 */
public abstract class BaseAction implements Action
{

  /**
   * ct.
   */
  public BaseAction()
  {
//    try
//    {
//      new Setup().handleAction(null);
//    }
//    catch (ApplicationException ae)
//    {
//      GUI.getStatusBar().setErrorText(ae.getMessage());
//    }
  }
}


/*********************************************************************
 * $Log: BaseAction.java,v $
 * Revision 1.1  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 **********************************************************************/