/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/AnfangsbestandNeu.java,v $
 * $Revision: 1.5 $
 * $Date: 2005/08/30 22:33:45 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandDelete;
import de.willuhn.jameica.fibu.gui.controller.AnfangsbestandControl;
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
 * Legt einen neuen Anfangsbestand an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class AnfangsbestandNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

		GUI.getView().setTitle(i18n.tr("Anfangsbestand"));

    final AnfangsbestandControl control = new AnfangsbestandControl(this);
    
    Container group = new LabelGroup(getParent(),i18n.tr("Eigenschaften"));

    group.addLabelPair(i18n.tr("Geschäftsjahr"), control.getGeschaeftsjahr());
    group.addLabelPair(i18n.tr("Konto"),         control.getKontoAuswahl());
    group.addLabelPair(i18n.tr("Anfangsbestand"),control.getBetrag());

    boolean closed = Settings.getActiveGeschaeftsjahr().isClosed();
    if (closed) GUI.getView().setErrorText(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

    ButtonArea buttonArea = group.createButtonArea(3);
    buttonArea.addButton(i18n.tr("Zurück"), new Back());

    Button delete = new Button(i18n.tr("Löschen"), new AnfangsbestandDelete(), getCurrentObject());
    delete.setEnabled(!closed);
    buttonArea.addButton(delete);
    
    Button store = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);
    store.setEnabled(!closed);
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
 * $Log: AnfangsbestandNeu.java,v $
 * Revision 1.5  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.3  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 * Revision 1.2  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/