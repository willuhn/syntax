/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart3CreateMandant.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/06/19 22:23:47 $
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
import de.willuhn.jameica.fibu.gui.controller.FirstStartControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
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

    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Mandant"));
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
 * Revision 1.2  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
 * Revision 1.1  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.1  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/12 15:41:18  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/