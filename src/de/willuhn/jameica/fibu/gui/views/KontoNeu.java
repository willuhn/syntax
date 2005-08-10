/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoNeu.java,v $
 * $Revision: 1.10 $
 * $Date: 2005/08/10 17:48:02 $
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
import de.willuhn.jameica.fibu.gui.action.KontoDelete;
import de.willuhn.jameica.fibu.gui.controller.KontoControl;
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
 * Bearbeitet ein Konto.
 * @author willuhn
 */
public class KontoNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    final KontoControl control = new KontoControl(this);

		GUI.getView().setTitle(i18n.tr("Konto bearbeiten"));

    Container group = new LabelGroup(getParent(),i18n.tr("Eigenschaften des Kontos"));

    group.addLabelPair(i18n.tr("Name")        , control.getName());
    group.addLabelPair(i18n.tr("Kontonummer") , control.getKontonummer());
    group.addLabelPair(i18n.tr("Steuersatz")  , control.getSteuer());
    group.addLabelPair(i18n.tr("Kontoart")    , control.getKontoart());
    group.addLabelPair(i18n.tr("Kontenrahmen"), control.getKontenrahmen());


    ButtonArea buttons = group.createButtonArea(3);
    buttons.addButton(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);
    buttons.addButton(i18n.tr("Löschen"), new KontoDelete(),getCurrentObject());
    buttons.addButton(i18n.tr("Zurück"), new Back());
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: KontoNeu.java,v $
 * Revision 1.10  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.9  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/