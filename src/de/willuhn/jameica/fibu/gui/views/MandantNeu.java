/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantNeu.java,v $
 * $Revision: 1.27 $
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateNeu;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrNeu;
import de.willuhn.jameica.fibu.gui.action.KontoNeu;
import de.willuhn.jameica.fibu.gui.action.MandantDelete;
import de.willuhn.jameica.fibu.gui.action.SteuerNeu;
import de.willuhn.jameica.fibu.gui.controller.MandantControl;
import de.willuhn.jameica.fibu.gui.part.BuchungstemplateList;
import de.willuhn.jameica.fibu.gui.part.GeschaeftsjahrList;
import de.willuhn.jameica.fibu.gui.part.KontoList;
import de.willuhn.jameica.fibu.gui.part.SteuerList;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.gui.util.TabGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Legt einen neuen Mandanten an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class MandantNeu extends AbstractView
{
  private static Integer activeTab = null;
  private TabFolder tabs           = null;

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Mandant bearbeiten"));

    final MandantControl control = new MandantControl(this);

    ColumnLayout cols = new ColumnLayout(getParent(),2);
    
    Container group = new LabelGroup(cols.getComposite(),i18n.tr("Eigenschaften"));

    group.addLabelPair(i18n.tr("Name 1")  , control.getName1());
    group.addLabelPair(i18n.tr("Name 2")  , control.getName2());
    group.addLabelPair(i18n.tr("Firma")   , control.getFirma());
    group.addLabelPair(i18n.tr("Strasse") , control.getStrasse());
    group.addLabelPair(i18n.tr("PLZ")     , control.getPLZ());
    group.addLabelPair(i18n.tr("Ort")     , control.getOrt());

    Container group2 = new LabelGroup(cols.getComposite(),i18n.tr("Buchhalterische Daten"));
    group2.addLabelPair(i18n.tr("Finanzamt"),		control.getFinanzamtAuswahl());
    group2.addLabelPair(i18n.tr("Steuernummer"),	control.getSteuernummer());
    group2.addLabelPair(i18n.tr("Währungsbezeichnung"), control.getWaehrung());

    ButtonArea buttonArea = new ButtonArea(getParent(),2);
    
    boolean canDelete = true;
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    if (current != null)
    {
      Mandant cm = current.getMandant();
      canDelete = !cm.equals(control.getMandant());
    }
    
    Button delete = new Button(i18n.tr("Löschen"),new MandantDelete(),getCurrentObject());
    delete.setEnabled(canDelete);
    buttonArea.addButton(delete);
    Button button1 = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,control.storeAllowed());
    button1.setEnabled(control.storeAllowed());
    buttonArea.addButton(button1);

    this.tabs = new TabFolder(getParent(), SWT.NONE);
    this.tabs.setLayoutData(new GridData(GridData.FILL_BOTH));
    this.tabs.setBackground(Color.BACKGROUND.getSWTColor());

    TabGroup jahre = new TabGroup(this.tabs,i18n.tr("Vorhandene Geschäftsjahre"));
    TablePart t1 = new GeschaeftsjahrList(control.getMandant(), new GeschaeftsjahrNeu());
    t1.paint(jahre.getComposite());

    TabGroup konten = new TabGroup(this.tabs,i18n.tr("Benutzerdefinierte Konten"),false,1);
    DBIterator kontenList = Settings.getActiveGeschaeftsjahr().getKontenrahmen().getKonten();
    kontenList.addFilter("mandant_id = " + control.getMandant().getID());
    KontoList t2 = new KontoList(kontenList, new KontoNeu());
    t2.setFilterVisible(false);
    t2.paint(konten.getComposite());

    TabGroup steuern = new TabGroup(this.tabs,i18n.tr("Steuersätze"));
    TablePart t3 = new SteuerList(control.getMandant(), new SteuerNeu());
    t3.paint(steuern.getComposite());
    
    TabGroup vorlagen = new TabGroup(this.tabs,i18n.tr("Buchungsvorlagen"));
    DBIterator vorlagenList = Settings.getDBService().createList(Buchungstemplate.class);
    vorlagenList.addFilter("mandant_id = " + control.getMandant().getID());
    vorlagenList.setOrder("order by name");
    TablePart t4 = new BuchungstemplateList(vorlagenList, new BuchungstemplateNeu());
    t4.paint(vorlagen.getComposite());

    if (activeTab != null)
      this.tabs.setSelection(activeTab);

    ButtonArea buttonArea2 = new ButtonArea(getParent(),2);
    buttonArea2.addButton(new Back(!control.storeAllowed()));
    Button button2 = new Button(i18n.tr("Geschäftsjahr anlegen"), new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleNewGJ();
      }
    });
    button2.setEnabled(control.storeAllowed());
    buttonArea2.addButton(button2);
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
    if (this.tabs != null && !this.tabs.isDisposed())
      activeTab = this.tabs.getSelectionIndex();
  }
}

/*********************************************************************
 * $Log: MandantNeu.java,v $
 * Revision 1.27  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.26  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.25  2010/02/08 15:39:48  willuhn
 * @N Option "Geschaeftsjahr abschliessen" in Kontextmenu des Geschaeftsjahres
 * @N Zweispaltiges Layout in Mandant-Details - damit bleibt mehr Platz fuer die Reiter unten drunter
 * @N Anzeige von Pflichtfeldern
 *
 * Revision 1.24  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.23.2.1  2008/09/08 09:03:51  willuhn
 * @C aktiver Mandant/aktives Geschaeftsjahr kann nicht mehr geloescht werden
 **********************************************************************/