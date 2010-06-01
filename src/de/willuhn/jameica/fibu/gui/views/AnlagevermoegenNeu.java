/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/AnlagevermoegenNeu.java,v $
 * $Revision: 1.12 $
 * $Date: 2010/06/01 23:51:56 $
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
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenAbschreiben;
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenDelete;
import de.willuhn.jameica.fibu.gui.controller.AnlagevermoegenControl;
import de.willuhn.jameica.fibu.gui.part.AbschreibungList;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.parts.Button;
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
    group.addLabelPair(i18n.tr("Anschaffungskosten (netto)"),   control.getKosten());

    Buchung buchung = control.getAnlagevermoegen().getBuchung();
    if (buchung != null)
      group.addLabelPair(i18n.tr("Zugehörige Buchung"), control.getBuchungLink());

    Container afa = new LabelGroup(getParent(),i18n.tr("Abschreibung"));
    afa.addLabelPair(i18n.tr("Abschreibungskonto"),           control.getAbschreibungsKonto());
    afa.addLabelPair(i18n.tr("Nutzungsdauer in Jahren"),      control.getLaufzeit());
    afa.addLabelPair(i18n.tr("Restwert"),                     control.getRestwert());
    afa.addLabelPair("", control.getHinweis());

    ButtonArea buttonArea = new ButtonArea(getParent(),4);
    buttonArea.addButton(new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new AnlagevermoegenDelete(), getCurrentObject());

    Button b = new Button(i18n.tr("Ausserplanmäßige Abschreibung..."),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        new AnlagevermoegenAbschreiben().handleAction(context);
        
        // Seite neu laden, damit die Abschreibung angezeigt wird
        GUI.startView(GUI.getCurrentView().getClass(),getCurrentObject());
      }
    }, getCurrentObject());
    b.setEnabled(!control.getAnlagevermoegen().isNewObject() && !Settings.getActiveGeschaeftsjahr().isClosed() && control.getAnlagevermoegen().getRestwert(Settings.getActiveGeschaeftsjahr()) > 0.0d);
    buttonArea.addButton(b);
    buttonArea.addButton(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);

    new Headline(getParent(),i18n.tr("Bereits gebuchte Abschreibungen"));
    TablePart table = new AbschreibungList(control.getAnlagevermoegen(),null);
    table.paint(getParent());
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenNeu.java,v $
 * Revision 1.12  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.11  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/