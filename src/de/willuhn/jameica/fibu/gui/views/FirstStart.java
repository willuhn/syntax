/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/FirstStart.java,v $
 * $Revision: 1.2 $
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
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.FormTextPart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;

/**
 * View mit dem Wizard fuer den ersten Start.
 */
public class FirstStart extends AbstractFirstStart
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    super.bind();

    GUI.getView().setTitle(i18n.tr("SynTAX: Installation"));
    
    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Willkommen"));
    group.addText(i18n.tr("Sie starten SynTAX zum ersten Mal. Dieser Wizard wird Sie bei " +
        "der Einrichtung der Datenbank sowie Ihrer Stammdaten unterstützen."),true);
    
    FormTextPart t = new FormTextPart();
    t.setText("<form>" +
        "<li>Schritt 1: Auswahl der Datenbank</li>" +
        "<li>Schritt 2: Einrichtung der Datenbank</li>" +
        "<li>Schritt 3: Anlegen eines Mandanten</li>" +
        "<li>Schritt 4: Erstellen eines Geschäftsjahres</li>" +
        "</form>");

    group.addPart(t);
    ButtonArea buttons = group.createButtonArea(1);
    buttons.addButton(i18n.tr("Weiter >>"),new FirstStart1ChooseDatabase(), getController());
    
    
  }

}


/*********************************************************************
 * $Log: FirstStart.java,v $
 * Revision 1.2  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 * Revision 1.1  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 **********************************************************************/