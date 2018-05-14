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

import de.willuhn.datasource.rmi.DBIterator;
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
 * View zum Erstellen des Geschaeftsjahres.
 */
public class FirstStart4CreateGeschaeftsjahr extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final FirstStartControl control = (FirstStartControl) getCurrentObject();
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    GUI.getView().setTitle(i18n.tr("SynTAX: Schritt 4 von 4 - Geschäftsjahr, Mandant: {0}", control.getMandant().getFirma()));

    Container group = null;

    DBIterator list = control.getMandant().getGeschaeftsjahre();
    if (list.size() > 0)
    {
      group = new LabelGroup(getParent(),i18n.tr("Bereits existierende Geschäftsjahre"),true);
      // Es existieren schon Mandanten. Dann soll er einen auswaehlen.
      control.getGeschaeftsjahrList().paint(group.getComposite());
    }
    else
    {
      group = new LabelGroup(getParent(),i18n.tr("Neues Geschäftsjahr"));
  
      group.addLabelPair(i18n.tr("Kontenrahmen"), control.getGeschaeftsjahrControl().getKontenrahmenAuswahl());
      group.addLabelPair(i18n.tr("Beginn des Geschäftsjahres"),control.getGeschaeftsjahrControl().getBeginn());
      group.addLabelPair(i18n.tr("Ende des Geschäftsjahres"),control.getGeschaeftsjahrControl().getEnde());
    }
      
    ButtonArea buttons = group.createButtonArea(1);
    buttons.addButton(i18n.tr("Fertigstellen"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    },null,true,"ok.png");
  }

}


/*********************************************************************
 * $Log: FirstStart4CreateGeschaeftsjahr.java,v $
 * Revision 1.3  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/