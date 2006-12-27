/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/GeschaeftsjahrNeu.java,v $
 * $Revision: 1.6 $
 * $Date: 2006/12/27 15:23:33 $
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
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrClose;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrDelete;
import de.willuhn.jameica.fibu.gui.controller.GeschaeftsjahrControl;
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
 * View zum Bearbeiten eines Geschaeftsjahres.
 */
public class GeschaeftsjahrNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Geschäftsjahr bearbeiten"));

    final GeschaeftsjahrControl control = new GeschaeftsjahrControl(this);

    Container group = new LabelGroup(getParent(),i18n.tr("Eigenschaften"));

    group.addLabelPair(i18n.tr("Kontenrahmen"), control.getKontenrahmenAuswahl());
    group.addLabelPair(i18n.tr("Beginn des Geschäftsjahres"),control.getBeginn());
    group.addLabelPair(i18n.tr("Ende des Geschäftsjahres"),control.getEnde());

    ButtonArea buttonArea = new ButtonArea(getParent(),4);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new GeschaeftsjahrDelete());
    
    Button close = new Button(i18n.tr("Geschäftsjahr abschliessen"), new GeschaeftsjahrClose(), control.getCurrentObject());
    close.setEnabled(!control.getGeschaeftsjahr().isClosed());
    buttonArea.addButton(close);
    
    Button store = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);
    store.setEnabled(!control.getGeschaeftsjahr().isClosed());
    buttonArea.addButton(store);
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrNeu.java,v $
 * Revision 1.6  2006/12/27 15:23:33  willuhn
 * @C merged update 1.3 and 1.4 to 1.3
 *
 * Revision 1.5  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/29 22:59:17  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 * Revision 1.2  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/