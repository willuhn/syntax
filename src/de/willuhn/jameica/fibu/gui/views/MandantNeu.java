/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantNeu.java,v $
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

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.gui.controller.MandantControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.LabelGroup;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class MandantNeu extends AbstractView
{

  /**
   * @param parent
   */
  public MandantNeu(Composite parent)
  {
    super(parent);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

		// Headline malen
		addHeadline("Mandant bearbeiten");

    MandantControl control = new MandantControl(this);

    try {
      
      LabelGroup contactGroup = new LabelGroup(getParent(),I18N.tr("Kontaktdaten"));

      contactGroup.addLabelPair(I18N.tr("Name 1")  , control.getName1());
      contactGroup.addLabelPair(I18N.tr("Name 2")  , control.getName2());
      contactGroup.addLabelPair(I18N.tr("Firma")   , control.getFirma());
      contactGroup.addLabelPair(I18N.tr("Strasse") , control.getStrasse());
      contactGroup.addLabelPair(I18N.tr("PLZ")     , control.getPLZ());
      contactGroup.addLabelPair(I18N.tr("Ort")     , control.getOrt());

      LabelGroup finanzGroup = new LabelGroup(getParent(),I18N.tr("Buchhalterische Daten"));

			finanzGroup.addLabelPair(I18N.tr("Kontenrahmen"), control.getKontenrahmenAuswahl());
      finanzGroup.addLabelPair(I18N.tr("Finanzamt"),		control.getFinanzamtAuswahl());
      finanzGroup.addLabelPair(I18N.tr("Steuernummer"),	control.getSteuernummer());
      finanzGroup.addLabelPair(I18N.tr("Geschäftsjahr"),control.getGeschaeftsjahr());

    }
    catch (RemoteException e)
    {
			Application.getLog().error("error while reading mandant",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Mandantendaten."));
    }


    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea(getParent(),control.storeAllowed() ? 3 : 2);
    buttonArea.addCancelButton(control);
    buttonArea.addDeleteButton(control);
    if (control.storeAllowed()) buttonArea.addStoreButton(control);
    
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }
}

/*********************************************************************
 * $Log: MandantNeu.java,v $
 * Revision 1.10  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.4  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.3  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.2  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/