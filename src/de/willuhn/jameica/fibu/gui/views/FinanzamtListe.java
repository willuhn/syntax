/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtListe.java,v $
 * $Revision: 1.10 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.gui.controller.FinanzamtControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class FinanzamtListe extends AbstractView
{


  /**
   * @param parent
   */
  public FinanzamtListe(Composite parent)
  {
    super(parent);
  }


  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    addHeadline("Liste der Finanzämter.");

		FinanzamtControl control = new FinanzamtControl(this);

    try {

 			control.getFinanzamtListe().paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton(I18N.tr("Finanzamt hinzufügen"),control);

    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading finanzamt list",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Finanzämter."));
    }
  }


  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }
}

/*********************************************************************
 * $Log: FinanzamtListe.java,v $
 * Revision 1.10  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 21:38:05  willuhn
 * @C refactoring finished
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/19 01:43:43  willuhn
 * @C small fixes
 *
 * Revision 1.5  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.2  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/