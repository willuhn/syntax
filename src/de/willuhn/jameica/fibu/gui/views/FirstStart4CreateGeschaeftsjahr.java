/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FirstStart4CreateGeschaeftsjahr.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/06/20 18:09:46 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
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
    buttons.addButton(i18n.tr("Fertigstellen >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    });
  }

}


/*********************************************************************
 * $Log: FirstStart4CreateGeschaeftsjahr.java,v $
 * Revision 1.2  2006/06/20 18:09:46  willuhn
 * @N Wizard seems to work now
 *
 * Revision 1.1  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
 **********************************************************************/