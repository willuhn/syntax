/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/SteuerNeu.java,v $
 * $Revision: 1.11 $
 * $Date: 2004/02/24 22:48:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import java.rmi.RemoteException;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.gui.controller.SteuerControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.LabelGroup;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.util.I18N;

/**
 * Legt einen neuen Steuersatz an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class SteuerNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

		// Headline malen
		GUI.setTitleText(I18N.tr("Steuersatz bearbeiten"));

    SteuerControl control = new SteuerControl(this);


    try {
      
      LabelGroup steuerGroup = new LabelGroup(getParent(),I18N.tr("Steuersatz"));

      steuerGroup.addLabelPair(I18N.tr("Name")      , 				control.getName());
      steuerGroup.addLabelPair(I18N.tr("Steuersatz"), 				control.getSatz());
      steuerGroup.addLabelPair(I18N.tr("Steuer-Sammelkonto"), control.getKontoAuswahl());
    }
    catch (RemoteException e)
    {
			Application.getLog().error("error while reading steuersaetze",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Steuersätze."));
    }

    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea(getParent(),3);
    buttonArea.addCancelButton(control);
    buttonArea.addDeleteButton(control);
    buttonArea.addStoreButton(control);
    
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }
}

/*********************************************************************
 * $Log: SteuerNeu.java,v $
 * Revision 1.11  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/