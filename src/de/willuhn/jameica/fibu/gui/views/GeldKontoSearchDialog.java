/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/GeldKontoSearchDialog.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/12/08 16:19:09 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.views;

import de.willuhn.jameica.GUI;
import de.willuhn.jameica.rmi.DBObject;
import de.willuhn.jameica.views.SearchDialog;

/**
 * @author willuhn
 */
public class GeldKontoSearchDialog extends SearchDialog
{

  /**
   * @param object
   */
  public GeldKontoSearchDialog(DBObject object)
  {
    super(GUI.shell);
    setObject(object);
  }
}

/*********************************************************************
 * $Log: GeldKontoSearchDialog.java,v $
 * Revision 1.2  2003/12/08 16:19:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/08 15:41:25  willuhn
 * @N searchInput
 *
 **********************************************************************/