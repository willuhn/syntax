/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoNeu.java,v $
 * $Revision: 1.9 $
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
import de.willuhn.jameica.fibu.gui.controller.KontoControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.LabelGroup;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.util.I18N;

/**
 * Bearbeitet ein Konto.
 * @author willuhn
 */
public class KontoNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    KontoControl control = new KontoControl(this);

    // Headline malen
		GUI.setTitleText(I18N.tr("Konto bearbeiten"));

    try {
      
      // Gruppe erzeugen
      LabelGroup group = new LabelGroup(getParent(),I18N.tr("Eigenschaften des Kontos"));

      group.addLabelPair(I18N.tr("Name")        , control.getName());
      group.addLabelPair(I18N.tr("Kontonummer") , control.getKontonummer());
      group.addLabelPair(I18N.tr("Steuersatz")  , control.getSteuer());
      group.addLabelPair(I18N.tr("Kontoart")    , control.getKontoart());
      group.addLabelPair(I18N.tr("Kontenrahmen"), control.getKontenrahmen());

    }
    catch (RemoteException e)
    {
			Application.getLog().error("error while reading konto",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Konto-Daten."));
    }


    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea(getParent(),2);
    buttonArea.addCancelButton(control);
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
 * $Log: KontoNeu.java,v $
 * Revision 1.9  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/