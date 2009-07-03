/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Einstellungen.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/07/03 10:52:18 $
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
import de.willuhn.jameica.fibu.gui.controller.EinstellungenControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Einstellungsdialog.
 * @author willuhn
 */
public class Einstellungen extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    // Headline malen
    GUI.getView().setTitle(i18n.tr("Einstellungen"));

    final EinstellungenControl control = new EinstellungenControl(this);

    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Buchungsrelevante Einstellungen"));
    group.addLabelPair(i18n.tr("Vorgabe Abschreibungskonto"),control.getAbschreibungsKonto());
    group.addLabelPair(i18n.tr("Vorgabe Abschreibungskonto für GWG"),control.getAbschreibungsKontoGWG());
    group.addLabelPair(i18n.tr("Nettogrenze GWG"),control.getGwgWert());

    ButtonArea buttonArea = group.createButtonArea(2);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
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
 * $Log: Einstellungen.java,v $
 * Revision 1.4  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.3.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 * Revision 1.3  2006/01/03 23:58:36  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.2  2005/10/13 15:44:33  willuhn
 * @B bug 139
 *
 * Revision 1.1  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 *********************************************************************/