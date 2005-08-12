/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungNeu.java,v $
 * $Revision: 1.26 $
 * $Date: 2005/08/12 00:10:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.BuchungDelete;
import de.willuhn.jameica.fibu.gui.controller.BuchungControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Erzeugt eine neue BaseBuchung oder bearbeitet eine existierende.
 * @author willuhn
 */
public class BuchungNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    // Headline malen
		GUI.getView().setTitle(i18n.tr("BaseBuchung bearbeiten"));

    final BuchungControl control = new BuchungControl(this);

    // Gruppe Konto erzeugen
    LabelGroup kontoGroup = new LabelGroup(getParent(),i18n.tr("Konto"));

    kontoGroup.addLabelPair(i18n.tr("Datum"),       control.getDatum());
    kontoGroup.addLabelPair(i18n.tr("Konto"),       control.getKontoAuswahl());
    kontoGroup.addLabelPair(i18n.tr("Geld-Konto"),  control.getGeldKontoAuswahl());
    kontoGroup.addLabelPair(i18n.tr("Text"),        control.getText());
    kontoGroup.addLabelPair(i18n.tr("Beleg-Nr."),   control.getBelegnummer());
    kontoGroup.addLabelPair(i18n.tr("Betrag"),      control.getBetrag());
    kontoGroup.addLabelPair(i18n.tr("Steuersatz"),  control.getSteuer());

    // wir machen das Datums-Feld zu dem mit dem Focus.
    control.getDatum().focus();

    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = kontoGroup.createButtonArea(4);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new BuchungDelete(), getCurrentObject());
    buttonArea.addButton(i18n.tr("Speichern"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null);
    buttonArea.addButton(i18n.tr("Speichern und Neue Buchung"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
        control.handleNew();
      }
    },null,true);

  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.26  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.25  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.24  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.23  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
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