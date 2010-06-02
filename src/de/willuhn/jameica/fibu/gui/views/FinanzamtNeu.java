/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtNeu.java,v $
 * $Revision: 1.16 $
 * $Date: 2010/06/02 00:02:58 $
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
import de.willuhn.jameica.fibu.gui.action.FinanzamtDelete;
import de.willuhn.jameica.fibu.gui.controller.FinanzamtControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Legt ein neues Finanzamt an oder bearbeitet ein existierendes.
 * @author willuhn
 */
public class FinanzamtNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

		GUI.getView().setTitle(i18n.tr("Daten des Finanzamtes bearbeiten"));

    final FinanzamtControl control = new FinanzamtControl(this);
    
    // Gruppe Kontaktdaten erzeugen
    Container contactGroup = new LabelGroup(getParent(),i18n.tr("Anschriftsdaten"));

    contactGroup.addLabelPair(i18n.tr("Name")    , control.getName());
    contactGroup.addLabelPair(i18n.tr("Strasse") , control.getStrasse());
    contactGroup.addLabelPair(i18n.tr("Postfach"), control.getPostfach());
    contactGroup.addLabelPair(i18n.tr("PLZ")     , control.getPLZ());
    contactGroup.addLabelPair(i18n.tr("Ort")     , control.getOrt());


    ButtonArea buttonArea = contactGroup.createButtonArea(3);
    buttonArea.addButton(new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new FinanzamtDelete(), getCurrentObject(),false,"user-trash-full.png");
    buttonArea.addButton(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true,"document-save.png");
  }
}

/*********************************************************************
 * $Log: FinanzamtNeu.java,v $
 * Revision 1.16  2010/06/02 00:02:58  willuhn
 * @N Mehr Icons
 *
 * Revision 1.15  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.14  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/