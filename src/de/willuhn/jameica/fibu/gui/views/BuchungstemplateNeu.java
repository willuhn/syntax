/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungstemplateNeu.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/02 15:18:29 $
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
import de.willuhn.jameica.gui.internal.action.Back;
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
    buttonArea.addButton(i18n.tr("Zurück"), new Back());

    buttonArea.addButton(new Button(i18n.tr("Löschen"), new BuchungstemplateDelete(), getCurrentObject()));

    Button store = new Button(i18n.tr("Speichern"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore(false);
      }
    },null,true);
    buttonArea.addButton(store);
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: BuchungstemplateNeu.java,v $
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/