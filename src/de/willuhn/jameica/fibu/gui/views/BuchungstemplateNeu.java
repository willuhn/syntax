/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungstemplateNeu.java,v $
 * $Revision: 1.6 $
 * $Date: 2010/06/01 23:51:56 $
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
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateDelete;
import de.willuhn.jameica.fibu.gui.controller.BuchungstemplateControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Erzeugt eine neue Buchung oder bearbeitet eine existierende.
 * @author willuhn
 */
public class BuchungstemplateNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    // Headline malen
		GUI.getView().setTitle(i18n.tr("Buchungsvorlage bearbeiten"));

    final BuchungstemplateControl control = new BuchungstemplateControl(this);

    // Gruppe Konto erzeugen
    LabelGroup kontoGroup = new LabelGroup(getParent(),i18n.tr("Eigenschaften"));

    kontoGroup.addLabelPair(i18n.tr("Mandant"),                     control.getMandant());
    kontoGroup.addLabelPair(i18n.tr("Kontenrahmen"),                control.getKontenrahmen());
    kontoGroup.addLabelPair(i18n.tr("Bezeichnung der Vorlage"),     control.getBezeichnung());
    kontoGroup.addSeparator();
    kontoGroup.addLabelPair(i18n.tr("Soll-Konto"),                  control.getSollKontoAuswahl());
    kontoGroup.addLabelPair(i18n.tr("Haben-Konto"),                 control.getHabenKontoAuswahl());
    kontoGroup.addLabelPair(i18n.tr("Text"),                        control.getText());
    kontoGroup.addLabelPair(i18n.tr("Brutto-Betrag"),               control.getBetrag());
    kontoGroup.addLabelPair(i18n.tr("Steuersatz"),                  control.getSteuer());

    // wir machen das Datums-Feld zu dem mit dem Focus.
    control.getSollKontoAuswahl().focus();

    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = kontoGroup.createButtonArea(3);
    buttonArea.addButton(new Back());

    buttonArea.addButton(new Button(i18n.tr("Löschen"), new BuchungstemplateDelete(), getCurrentObject()));

    Button store = new Button(i18n.tr("Speichern"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore(false);
      }
    },null,true);
    buttonArea.addButton(store);
  }
}

/*********************************************************************
 * $Log: BuchungstemplateNeu.java,v $
 * Revision 1.6  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/