/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/Welcome.java,v $
 * $Revision: 1.6 $
 * $Date: 2004/01/29 00:06:46 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.Headline;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class Welcome extends AbstractView
{

  /**
   * @param parent
   */
  public Welcome(Composite parent)
  {
    super(parent);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    new Headline(getParent(),I18N.tr("Finanzbuchhaltung für Jameica."));
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }
}

/*********************************************************************
 * $Log: Welcome.java,v $
 * Revision 1.6  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.2  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/