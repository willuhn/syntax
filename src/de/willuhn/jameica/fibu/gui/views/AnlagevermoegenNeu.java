/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/AnlagevermoegenNeu.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/29 22:52:04 $
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
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenDelete;
import de.willuhn.jameica.fibu.gui.controller.AnlagevermoegenControl;
import de.willuhn.jameica.fibu.gui.part.AbschreibungList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * View zum Bearbeiten eines Anlage-Gutes.
 */
public class AnlagevermoegenNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Anlagevermögen"));

    final AnlagevermoegenControl control = new AnlagevermoegenControl(this);
    
    Container group = new LabelGroup(getParent(),i18n.tr("Eigenschaften"));

    group.addLabelPair(i18n.tr("Mandant"),                      control.getMandant());
    group.addLabelPair(i18n.tr("Bezeichnung"),                  control.getName());
    group.addLabelPair(i18n.tr("Bestandskonto"),                control.getKonto());
    group.addLabelPair(i18n.tr("Anschaffungsdatum"),            control.getDatum());
    group.addLabelPair(i18n.tr("Anschaffungskosten"),           control.getKosten());

    Container afa = new LabelGroup(getParent(),i18n.tr("Abschreibung"));
    afa.addLabelPair(i18n.tr("Abschreibungskonto"),           control.getAbschreibungsKonto());
    afa.addLabelPair(i18n.tr("Nutzungsdauer in Jahren"),      control.getLaufzeit());
    afa.addLabelPair(i18n.tr("Restwert"),                     control.getRestwert());

    new Headline(getParent(),i18n.tr("Bereits gebuchte Abschreibungen"));
    TablePart table = new AbschreibungList(control.getAnlagevermoegen(),null);
    table.paint(getParent());
    
    ButtonArea buttonArea = group.createButtonArea(3);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new AnlagevermoegenDelete(), getCurrentObject());
    buttonArea.addButton(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
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
 * $Log: AnlagevermoegenNeu.java,v $
 * Revision 1.4  2005/08/29 22:52:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/