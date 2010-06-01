/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/AnfangsbestandListe.java,v $
 * $Revision: 1.5 $
 * $Date: 2010/06/01 16:37:22 $
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
import de.willuhn.jameica.fibu.gui.action.AnfangsbestandNeu;
import de.willuhn.jameica.fibu.gui.part.AnfangsbestandList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * View fuer die Anfangsbestaende.
 */
public class AnfangsbestandListe extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Anfangsbestände"));

    Part p = new AnfangsbestandList(new AnfangsbestandNeu());
    p.paint(getParent());

    ButtonArea buttons = new ButtonArea(getParent(),2);
    buttons.addButton(i18n.tr("Zurück"), new Back());
    
    Button button = new Button(i18n.tr("Neuer Anfangsbestand"),new AnfangsbestandNeu(),null,true);
    button.setEnabled(!Settings.getActiveGeschaeftsjahr().isClosed());
    buttons.addButton(button);
  }
}


/*********************************************************************
 * $Log: AnfangsbestandListe.java,v $
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/