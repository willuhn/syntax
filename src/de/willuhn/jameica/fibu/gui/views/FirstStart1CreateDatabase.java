/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart1CreateDatabase.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 23:05:47 $
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
 * View zum Erstellen der Datenbank.
 */
public class FirstStart1CreateDatabase extends AbstractFirstStart
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    super.bind();
    
    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 1 von 3 - Einrichtung der Datenbank"));
    
    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Datenbank-Einstellungen"));
    group.addLabelPair(i18n.tr("Typ der Datenbank"),getController().getDBType());
    group.addLabelPair(i18n.tr("Name der Datenbank"),getController().getDBName());
    group.addLabelPair(i18n.tr("Username"),getController().getUsername());
    group.addLabelPair(i18n.tr("Passwort"),getController().getPassword());
    group.addLabelPair(i18n.tr("Passwortwiederholung"),getController().getPassword2());
    group.addLabelPair(i18n.tr("Hostname der Datenbank"),getController().getHostname());
    group.addLabelPair(i18n.tr("TCP-Port"),getController().getPort());
    
    group.addHeadline(i18n.tr("Erstellung der Datenbank"));
    group.addPart(getController().getProgressMonitor());

    ButtonArea buttons = group.createButtonArea(2);
    buttons.addButton(i18n.tr("<< Zurück"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        getController().handleFirstStart();
      }
    });
    buttons.addButton(i18n.tr("Datenbank einrichten >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        getController().handle1CreateDatabase();
      }
    });
  }

}


/*********************************************************************
 * $Log: FirstStart1CreateDatabase.java,v $
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