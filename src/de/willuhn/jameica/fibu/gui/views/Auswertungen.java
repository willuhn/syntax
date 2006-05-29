/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Auswertungen.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/05/29 17:30:26 $
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
import de.willuhn.jameica.fibu.gui.controller.AuswertungControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * View fuer die Auswertungen.
 */
public class Auswertungen extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Auswertungen"));

    final AuswertungControl control = new AuswertungControl(this);
    
    Container group = new LabelGroup(getParent(),i18n.tr("Auswertungen"));
    group.addLabelPair(i18n.tr("Art der Auswertung"), control.getAuswertungen());
    
    group.addHeadline(i18n.tr("Zeitraum"));
    group.addLabelPair(i18n.tr("Geschäftsjahr"), control.getJahr());
    group.addLabelPair(i18n.tr("Start-Datum"), control.getStart());
    group.addLabelPair(i18n.tr("End-Datum"), control.getEnd());
    
    group.addHeadline(i18n.tr("Konten"));
    group.addLabelPair(i18n.tr("von"), control.getStartKonto());
    group.addLabelPair(i18n.tr("bis"), control.getEndKonto());
    
    ButtonArea buttonArea = group.createButtonArea(3);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    buttonArea.addButton(i18n.tr("Erstellen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleExecute();
      }
    },null,true);

  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }

}


/*********************************************************************
 * $Log: Auswertungen.java,v $
 * Revision 1.3  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.2  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.1  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 **********************************************************************/