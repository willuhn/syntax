/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/KontoSearchDialog.java,v $
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
import de.willuhn.jameica.fibu.objects.Konto;
import de.willuhn.jameica.views.SearchDialog;

/**
 * @author willuhn
 */
public class KontoSearchDialog extends SearchDialog
{

  /**
   * @param list
   */
  public KontoSearchDialog(Konto k)
  {
    super(GUI.shell);
    setObject(k);
  }
}

/*********************************************************************
 * $Log: KontoSearchDialog.java,v $
 * Revision 1.2  2003/12/08 16:19:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/08 15:41:25  willuhn
 * @N searchInput
 *
 **********************************************************************/