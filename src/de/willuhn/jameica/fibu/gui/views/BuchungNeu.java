/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungNeu.java,v $
 * $Revision: 1.21 $
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
import de.willuhn.jameica.fibu.gui.controller.BuchungControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.LabelGroup;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class BuchungNeu extends AbstractView
{

  /**
   * @param parent
   */
  public BuchungNeu(Composite parent)
  {
    super(parent);
  }



  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

		// Headline malen
		addHeadline("Buchung bearbeiten");

    BuchungControl control = new BuchungControl(this);

    // Gruppe Konto erzeugen
    LabelGroup kontoGroup = new LabelGroup(getParent(),I18N.tr("Konto"));

    try {
      
      kontoGroup.addLabelPair(I18N.tr("Datum"),       control.getDatum());
      kontoGroup.addLabelPair(I18N.tr("Konto"),       control.getKontoAuswahl());
      kontoGroup.addLabelPair(I18N.tr("Geld-Konto"),  control.getGeldKontoAuswahl());
      kontoGroup.addLabelPair(I18N.tr("Text"),        control.getText());
      kontoGroup.addLabelPair(I18N.tr("Beleg-Nr."),   control.getBelegnummer());
      kontoGroup.addLabelPair(I18N.tr("Betrag"),      control.getBetrag());
      kontoGroup.addLabelPair(I18N.tr("Steuer"),      control.getSteuer());

      // wir machen das Datums-Feld zu dem mit dem Focus.
      control.getDatum().focus();
    }
    catch (RemoteException e)
    {
			Application.getLog().error("error while reading buchung",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Buchungsdaten."));
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
 * $Log: BuchungNeu.java,v $
 * Revision 1.21  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.20  2004/01/27 21:38:05  willuhn
 * @C refactoring finished
 *
 * Revision 1.19  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.17  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.16  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.14  2003/12/10 01:12:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2003/12/10 00:47:29  willuhn
 * @N SearchDialog for Konto works now ;)
 *
 * Revision 1.12  2003/12/08 15:41:25  willuhn
 * @N searchInput
 *
 * Revision 1.11  2003/12/05 18:42:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.9  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.5  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/11/23 19:26:25  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/