/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/AnlagevermoegenNeu.java,v $
 * $Revision: 1.9 $
 * $Date: 2006/05/08 15:41:57 $
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
import de.willuhn.jameica.gui.internal.action.Back;
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
    buttonArea.addButton(i18n.tr("Zurück"), new Back());
    buttonArea.addButton(i18n.tr("Löschen"), new AnlagevermoegenDelete(), getCurrentObject());

    Button b = new Button(i18n.tr("Ausserplanmäßige Abschreibung"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        new AnlagevermoegenAbschreiben().handleAction(context);
        
        // Seite neu laden, damit die Abschreibung angezeigt wird
        GUI.startView(GUI.getCurrentView().getClass(),getCurrentObject());
      }
    }, getCurrentObject());
    b.setEnabled(!control.getAnlagevermoegen().isNewObject() && control.getAnlagevermoegen().getRestwert(Settings.getActiveGeschaeftsjahr()) > 0.0d);
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

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }

}


/*********************************************************************
 * $Log: AnlagevermoegenNeu.java,v $
 * Revision 1.9  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 * Revision 1.8  2006/01/04 00:53:48  willuhn
 * @B bug 166 Ausserplanmaessige Abschreibungen
 *
 * Revision 1.7  2006/01/03 23:58:36  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.6  2006/01/03 00:05:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2006/01/02 23:50:58  willuhn
 * *** empty log message ***
 *
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