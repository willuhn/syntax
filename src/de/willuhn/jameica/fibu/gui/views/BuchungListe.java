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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.part.BuchungList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Zeigt die Liste der Buchungen an.
 * @author willuhn
 */
public class BuchungListe extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();



  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    GUI.getView().setTitle(i18n.tr("Buchungen"));

    Part p = new BuchungList(new BuchungNeu());
    p.paint(getParent());
    
    ButtonArea buttons = new ButtonArea();
    
    Button create = new Button(i18n.tr("Neue Buchung"), new BuchungNeu(),null,true,"list-add.png");
    create.setEnabled(!Settings.getActiveGeschaeftsjahr().isClosed());
    buttons.addButton(create);
    
    buttons.paint(getParent());
  }
}

/*********************************************************************
 * $Log: BuchungListe.java,v $
 * Revision 1.30  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.29  2010-06-02 00:02:59  willuhn
 * @N Mehr Icons
 *
 * Revision 1.28  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.27  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/