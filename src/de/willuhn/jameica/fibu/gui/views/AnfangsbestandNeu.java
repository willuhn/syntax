/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/AnfangsbestandNeu.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/22 21:44:09 $
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
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandDelete;
import de.willuhn.jameica.fibu.gui.controller.AnfangsbestandControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Legt einen neuen Anfangsbestand an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class AnfangsbestandNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

		GUI.getView().setTitle(i18n.tr("Anfangsbestand"));

    final AnfangsbestandControl control = new AnfangsbestandControl(this);
    
    // Gruppe Kontaktdaten erzeugen
    Container contactGroup = new LabelGroup(getParent(),i18n.tr("Eigenschaften"));

    contactGroup.addLabelPair(i18n.tr("Mandant") ,      control.getMandant());
    contactGroup.addLabelPair(i18n.tr("Konto")   ,      control.getKontoAuswahl());
    contactGroup.addLabelPair(i18n.tr("Anfangsbestand"),control.getBetrag());

    ButtonArea buttonArea = contactGroup.createButtonArea(3);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new AnfangsbestandDelete(), getCurrentObject());
    buttonArea.addButton(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
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
 * $Log: AnfangsbestandNeu.java,v $
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/