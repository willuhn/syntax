/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungListe.java,v $
 * $Revision: 1.14 $
 * $Date: 2004/01/27 21:38:05 $
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
import de.willuhn.jameica.fibu.gui.controller.BuchungControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.ButtonArea;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class BuchungListe extends AbstractView
{


  /**
   * @param parent
   */
  public BuchungListe(Composite parent)
  {
    super(parent);
  }


  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    addHeadline("Buchungsliste.");

		BuchungControl control = new BuchungControl(this);

    try {

      control.getBuchungListe().paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton(I18N.tr("Neue Buchung"),control);

    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading buchung list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Buchungen."));
      e.printStackTrace();
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
 * $Log: BuchungListe.java,v $
 * Revision 1.14  2004/01/27 21:38:05  willuhn
 * @C refactoring finished
 *
 * Revision 1.13  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.11  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.9  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.8  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 * Revision 1.5  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.4  2003/11/24 16:26:16  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.3  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:47:50  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 **********************************************************************/