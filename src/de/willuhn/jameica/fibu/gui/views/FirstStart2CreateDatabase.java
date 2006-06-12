/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/FirstStart2CreateDatabase.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 14:08:29 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.gui.action.FirstStart1ChooseDatabase;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;

/**
 * View zum Erstellen der Datenbank.
 */
public class FirstStart2CreateDatabase extends AbstractFirstStart
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    super.bind();

    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 2 von 4 - Einrichten der Datenbank"));
    
    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Datenbank"));
    
    ButtonArea buttons = group.createButtonArea(2);
    buttons.addButton(i18n.tr("<< Zurück"),new FirstStart1ChooseDatabase(), getController());
    buttons.addButton(i18n.tr("Weiter >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        getController().handleCreateDatabase();
      }
    });
  }

}


/*********************************************************************
 * $Log: FirstStart2CreateDatabase.java,v $
 * Revision 1.1  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/