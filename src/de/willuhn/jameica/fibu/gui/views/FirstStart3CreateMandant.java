/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart3CreateMandant.java,v $
 * $Revision: 1.4 $
 * $Date: 2010/06/01 16:37:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.controller.FirstStartControl;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * View zum Erstellen des Mandanten.
 */
public class FirstStart3CreateMandant extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final FirstStartControl control = (FirstStartControl) getCurrentObject();

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 3 von 4 - Einrichtung des Mandanten"));

    Container group = null;

    DBIterator list = Settings.getDBService().createList(Mandant.class);
    if (list.size() > 0)
    {
      group = new LabelGroup(getParent(),i18n.tr("Bereits existierende Mandanten"),true);
      // Es existieren schon Mandanten. Dann soll er einen auswaehlen.
      control.getMandantList().paint(group.getComposite());
    }
    else
    {
      group = new LabelGroup(getParent(),i18n.tr("Neuer Mandant"));
      group.addLabelPair(i18n.tr("Name 1")  , control.getMandantControl().getName1());
      group.addLabelPair(i18n.tr("Name 2")  , control.getMandantControl().getName2());
      group.addLabelPair(i18n.tr("Firma")   , control.getMandantControl().getFirma());
      group.addLabelPair(i18n.tr("Strasse") , control.getMandantControl().getStrasse());
      group.addLabelPair(i18n.tr("PLZ")     , control.getMandantControl().getPLZ());
      group.addLabelPair(i18n.tr("Ort")     , control.getMandantControl().getOrt());
  
      group.addHeadline(i18n.tr("Buchhalterische Daten"));
      
      group.addLabelPair(i18n.tr("Finanzamt"),    control.getMandantControl().getFinanzamtAuswahl());
      group.addLabelPair(i18n.tr("Steuernummer"), control.getMandantControl().getSteuernummer());
      group.addLabelPair(i18n.tr("Währungsbezeichnung"), control.getMandantControl().getWaehrung());
    }

    ButtonArea buttons = group.createButtonArea(1);
    buttons.addButton(i18n.tr("Weiter >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    });
  }

}


/*********************************************************************
 * $Log: FirstStart3CreateMandant.java,v $
 * Revision 1.4  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/