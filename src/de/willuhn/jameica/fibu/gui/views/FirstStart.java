/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/FirstStart.java,v $
 * $Revision: 1.4 $
 * $Date: 2006/06/13 22:52:10 $
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
import de.willuhn.jameica.gui.parts.FormTextPart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;

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
    group.addText("\n" + i18n.tr("Sie starten SynTAX zum ersten Mal. Dieser Assistent wird Sie bei " +
        "der Einrichtung der Datenbank sowie Ihrer Stammdaten unterstützen."),true);
    
    FormTextPart t = new FormTextPart();
    t.setText("<form><p></p>" +
        "<li>Einrichtung der Datenbank</li>" +
        "<li>Anlegen eines Mandanten</li>" +
        "<li>Erstellen eines Geschäftsjahres</li>" +
        "<p></p></form>");

    group.addPart(t);
    ButtonArea buttons = group.createButtonArea(1);

    buttons.addButton(i18n.tr("Weiter >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        getController().handleForward();
      }
    });
    
    
  }

}


/*********************************************************************
 * $Log: FirstStart.java,v $
 * Revision 1.4  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.3  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 * Revision 1.1  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 **********************************************************************/