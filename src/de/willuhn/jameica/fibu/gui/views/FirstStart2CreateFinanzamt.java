/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart2CreateFinanzamt.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/19 16:25:42 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;

/**
 * View zum Erstellen des Finanz-Amtes.
 */
public class FirstStart2CreateFinanzamt extends AbstractFirstStart
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    super.bind();
    
    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 2 von 4 - Festlegen des Finanzamtes"));

    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Finanzamt"));
    
    ButtonArea buttons = group.createButtonArea(2);
    buttons.addButton(i18n.tr("<< Zurück"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        getController().handleBack();
      }
    });
    buttons.addButton(i18n.tr("Weiter >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        getController().handleForward();
      }
    });
  }

}


/*********************************************************************
 * $Log: FirstStart2CreateFinanzamt.java,v $
 * Revision 1.1  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 **********************************************************************/