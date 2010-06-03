/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/GeschaeftsjahrNeu.java,v $
 * $Revision: 1.11 $
 * $Date: 2010/06/03 14:26:16 $
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
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrClose;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrDelete;
import de.willuhn.jameica.fibu.gui.controller.GeschaeftsjahrControl;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
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

    Container group = new SimpleContainer(getParent());
    group.addHeadline(i18n.tr("Eigenschaften"));
    group.addLabelPair(i18n.tr("Kontenrahmen"), control.getKontenrahmenAuswahl());
    group.addLabelPair(i18n.tr("Beginn des Geschäftsjahres"),control.getBeginn());
    group.addLabelPair(i18n.tr("Ende des Geschäftsjahres"),control.getEnde());

    ButtonArea buttonArea = new ButtonArea(getParent(),4);
    buttonArea.addButton(new Back());
    
    boolean canDelete = true;
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    if (current != null) canDelete = !current.equals(control.getGeschaeftsjahr());
    Button delete = new Button(i18n.tr("Löschen"),new GeschaeftsjahrDelete(),getCurrentObject(),false,"user-trash-full.png");
    delete.setEnabled(canDelete);
    buttonArea.addButton(delete);
    
    Button close = new Button(i18n.tr("Geschäftsjahr abschliessen"), new GeschaeftsjahrClose(), control.getCurrentObject());
    close.setEnabled(!control.getGeschaeftsjahr().isClosed());
    buttonArea.addButton(close);
    
    Button store = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true,"document-save.png");
    store.setEnabled(!control.getGeschaeftsjahr().isClosed());
    buttonArea.addButton(store);
  }
}


/*********************************************************************
 * $Log: GeschaeftsjahrNeu.java,v $
 * Revision 1.11  2010/06/03 14:26:16  willuhn
 * @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
 * @C Code-Cleanup
 *
 * Revision 1.10  2010/06/02 00:02:58  willuhn
 * @N Mehr Icons
 *
 * Revision 1.9  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.8  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.7  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.6.2.1  2008/09/08 09:03:51  willuhn
 * @C aktiver Mandant/aktives Geschaeftsjahr kann nicht mehr geloescht werden
 **********************************************************************/