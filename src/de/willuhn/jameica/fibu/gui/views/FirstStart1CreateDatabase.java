/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
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
 * View zum Erstellen der Datenbank.
 */
public class FirstStart1CreateDatabase extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final FirstStartControl control = (FirstStartControl) getCurrentObject();

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 1 von 4 - Einrichtung der Datenbank"));
    
    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Datenbank-Einstellungen"),true);
    group.addLabelPair(i18n.tr("Typ der Datenbank"),control.getDBType());
    group.addLabelPair(i18n.tr("Name der Datenbank"),control.getDBName());
    group.addLabelPair(i18n.tr("Username"),control.getUsername());
    group.addLabelPair(i18n.tr("Passwort"),control.getPassword());
    group.addLabelPair(i18n.tr("Passwortwiederholung"),control.getPassword2());
    group.addLabelPair(i18n.tr("Hostname der Datenbank"),control.getHostname());
    group.addLabelPair(i18n.tr("TCP-Port"),control.getPort());
    
    group.addHeadline(i18n.tr("Erstellung der Datenbank"));
    group.addPart(control.getProgressMonitor());

    ButtonArea buttons = group.createButtonArea(1);
    buttons.addButton(i18n.tr("Weiter"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    },null,true,"go-next.png");
  }

}


/*********************************************************************
 * $Log: FirstStart1CreateDatabase.java,v $
 * Revision 1.6  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/