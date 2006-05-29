/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/FirstStart.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/05/29 23:05:07 $
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
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.FormTextPart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * View mit dem Wizard fuer den ersten Start.
 */
public class FirstStart extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    GUI.getView().setTitle(i18n.tr("SynTAX: Installation"));
    
    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Willkommen"));
    group.addText(i18n.tr("Sie starten SynTAX zum ersten Mal. Dieser Wizard wird Sie bei " +
        "der Einrichtung der Datenbank sowie Ihrer Stammdaten unterstützen."),true);
    
    FormTextPart t = new FormTextPart();
    t.setText("<form>" +
        "<li>Schritt 1: Auswahl des Datenbank-Typs</li>" +
        "<li>Schritt 2: Einrichtung der Datenbank</li>" +
        "<li>Schritt 3: Anlegen eines Mandanten</li>" +
        "<li>Schritt 4: Erstellen eines Geschäftsjahres</li>" +
        "</form>");

    group.addPart(t);
    ButtonArea buttons = group.createButtonArea(1);
    buttons.addButton(i18n.tr("Weiter >>"),null/*new FirstStart1.class*/);
    
    
  }

}


/*********************************************************************
 * $Log: FirstStart.java,v $
 * Revision 1.1  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 **********************************************************************/