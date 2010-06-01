/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart2CreateFinanzamt.java,v $
 * $Revision: 1.5 $
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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.controller.FirstStartControl;
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
 * View zum Erstellen des Finanz-Amtes.
 */
public class FirstStart2CreateFinanzamt extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final FirstStartControl control = (FirstStartControl) getCurrentObject();

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 2 von 4 - Festlegen des Finanzamtes"));

    Container group = new LabelGroup(getParent(),i18n.tr("Neues Finanzamt"),true);
    group.addLabelPair(i18n.tr("Name")    , control.getFinanzamtControl().getName());
    group.addLabelPair(i18n.tr("Strasse") , control.getFinanzamtControl().getStrasse());
    group.addLabelPair(i18n.tr("Postfach"), control.getFinanzamtControl().getPostfach());
    group.addLabelPair(i18n.tr("PLZ")     , control.getFinanzamtControl().getPLZ());
    group.addLabelPair(i18n.tr("Ort")     , control.getFinanzamtControl().getOrt());

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
 * $Log: FirstStart2CreateFinanzamt.java,v $
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/