/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/SteuerNeu.java,v $
 * $Revision: 1.19 $
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
import de.willuhn.jameica.fibu.gui.action.SteuerDelete;
import de.willuhn.jameica.fibu.gui.controller.SteuerControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Legt einen neuen Steuersatz an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class SteuerNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Steuersatz bearbeiten"));

    final SteuerControl control = new SteuerControl(this);

    Container steuerGroup = new LabelGroup(getParent(),i18n.tr("Steuersatz"));

    steuerGroup.addLabelPair(i18n.tr("Mandant"),            control.getMandant());
    steuerGroup.addLabelPair(i18n.tr("Name")      , 				control.getName());
    steuerGroup.addLabelPair(i18n.tr("Steuersatz"), 				control.getSatz());
    steuerGroup.addLabelPair(i18n.tr("Steuer-Sammelkonto"), control.getKontoAuswahl());

    ButtonArea buttonArea = steuerGroup.createButtonArea(3);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    
    if (!control.getSteuer().canChange())
      GUI.getView().setErrorText(i18n.tr("System-Steuerkonto darf nicht geändert werden."));
    
    Button delete = new Button(i18n.tr("Löschen"), new SteuerDelete(),control.getSteuer());
    delete.setEnabled(control.getSteuer().canChange());
    buttonArea.addButton(delete);
    
    Button store = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);
    store.setEnabled(control.getSteuer().canChange());
    buttonArea.addButton(store);
  }
}

/*********************************************************************
 * $Log: SteuerNeu.java,v $
 * Revision 1.19  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.18  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 **********************************************************************/