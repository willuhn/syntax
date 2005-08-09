/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtNeu.java,v $
 * $Revision: 1.11 $
 * $Date: 2005/08/09 23:53:34 $
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

import de.willuhn.jameica.fibu.gui.controller.FinanzamtControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Legt ein neues Finanzamt an oder bearbeitet ein existierendes.
 * @author willuhn
 */
public class FinanzamtNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    FinanzamtControl control = new FinanzamtControl(this);

    // Headline malen
		GUI.setTitleText(I18N.tr("Daten des Finanzamtes bearbeiten"));

    try {
      
      // Gruppe Kontaktdaten erzeugen
      LabelGroup contactGroup = new LabelGroup(getParent(),I18N.tr("Anschriftsdaten"));

      contactGroup.addLabelPair(I18N.tr("Name")    , control.getName());
      contactGroup.addLabelPair(I18N.tr("Strasse") , control.getStrasse());
      contactGroup.addLabelPair(I18N.tr("Postfach"), control.getPostfach());
      contactGroup.addLabelPair(I18N.tr("PLZ")     , control.getPLZ());
      contactGroup.addLabelPair(I18N.tr("Ort")     , control.getOrt());

    }
    catch (RemoteException e)
    {
			Application.getLog().error("error while reading finanzamt",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen des Finanzamtes."));
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
 * $Log: FinanzamtNeu.java,v $
 * Revision 1.11  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.10  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/27 21:38:05  willuhn
 * @C refactoring finished
 *
 * Revision 1.6  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.4  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/