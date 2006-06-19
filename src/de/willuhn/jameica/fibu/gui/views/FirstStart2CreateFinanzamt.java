/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart2CreateFinanzamt.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/06/19 23:00:47 $
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

    // Gruppe Kontaktdaten erzeugen
    Container group = new LabelGroup(getParent(),i18n.tr("Anschriftsdaten"),true);

//    DBIterator list = Settings.getDBService().createList(Finanzamt.class);
//    if (list.size() > 0)
//    {
//      new FinanzamtList(new Action() {
//        public void handleAction(Object context) throws ApplicationException
//        {
//        }
//      }).paint(getParent());
//    }
//    else
//    {

      group.addLabelPair(i18n.tr("Name")    , control.getFinanzamtControl().getName());
      group.addLabelPair(i18n.tr("Strasse") , control.getFinanzamtControl().getStrasse());
      group.addLabelPair(i18n.tr("Postfach"), control.getFinanzamtControl().getPostfach());
      group.addLabelPair(i18n.tr("PLZ")     , control.getFinanzamtControl().getPLZ());
      group.addLabelPair(i18n.tr("Ort")     , control.getFinanzamtControl().getOrt());
//    }

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
 * Revision 1.3  2006/06/19 23:00:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
 * Revision 1.1  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 **********************************************************************/