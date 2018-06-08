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
import de.willuhn.jameica.fibu.gui.controller.AuswertungControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * View fuer die Auswertungen.
 */
public class Auswertungen extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    GUI.getView().setTitle(i18n.tr("Auswertungen"));

    final AuswertungControl control = new AuswertungControl(this);
    
    Container group = new SimpleContainer(getParent());

    group.addHeadline(i18n.tr("Eigenschaften"));
    group.addInput(control.getAuswertungen());
    
    group.addHeadline(i18n.tr("Zeitraum"));
    group.addInput(control.getJahr());
    group.addInput(control.getStart());
    group.addInput(control.getEnd());
    group.addInput(control.getNotiz());
    
    group.addHeadline(i18n.tr("Konten"));
    group.addInput(control.getStartKonto());
    group.addInput(control.getEndKonto());
    group.addInput(control.getLeereKonten());
    
    group.addSeparator();
    group.addInput(control.getOpenAfterCreation());
    
    ButtonArea buttonArea = new ButtonArea();
    buttonArea.addButton(control.getStartButton());
    buttonArea.paint(getParent());
  }
}
