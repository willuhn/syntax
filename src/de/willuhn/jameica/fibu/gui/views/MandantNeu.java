/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantNeu.java,v $
 * $Revision: 1.25 $
 * $Date: 2010/02/08 15:39:48 $
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
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
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

    TabFolder folder = new TabFolder(getParent(), SWT.NONE);
    folder.setLayoutData(new GridData(GridData.FILL_BOTH));
    folder.setBackground(Color.BACKGROUND.getSWTColor());

    TabGroup jahre = new TabGroup(folder,i18n.tr("Vorhandene Geschäftsjahre"));
    TablePart t1 = new GeschaeftsjahrList(control.getMandant(), new GeschaeftsjahrNeu());
    t1.paint(jahre.getComposite());

    TabGroup konten = new TabGroup(folder,i18n.tr("Benutzerdefinierte Konten"),false,1);
    DBIterator kontenList = Settings.getActiveGeschaeftsjahr().getKontenrahmen().getKonten();
    kontenList.addFilter("mandant_id = " + control.getMandant().getID());
    KontoList t2 = new KontoList(kontenList, new KontoNeu());
    t2.setFilterVisible(false);
    t2.paint(konten.getComposite());

    TabGroup steuern = new TabGroup(folder,i18n.tr("Steuersätze"));
    DBIterator steuerList = Settings.getDBService().createList(Steuer.class);
    steuerList.addFilter("mandant_id = " + control.getMandant().getID());
    steuerList.setOrder("order by name");
    TablePart t3 = new SteuerList(steuerList, new SteuerNeu());
    t3.paint(steuern.getComposite());
    
    TabGroup vorlagen = new TabGroup(folder,i18n.tr("Buchungsvorlagen"));
    DBIterator vorlagenList = Settings.getDBService().createList(Buchungstemplate.class);
    steuerList.addFilter("mandant_id = " + control.getMandant().getID());
    vorlagenList.setOrder("order by name");
    TablePart t4 = new BuchungstemplateList(vorlagenList, new BuchungstemplateNeu());
    t4.paint(vorlagen.getComposite());


    ButtonArea buttonArea2 = new ButtonArea(getParent(),2);
    buttonArea2.addButton(i18n.tr("Zurück"), new Back(), null, !control.storeAllowed());
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
  }
}

/*********************************************************************
 * $Log: MandantNeu.java,v $
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
 *
 * Revision 1.23  2006/06/19 22:54:34  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.20  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.19  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.18  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.17  2005/08/29 14:26:57  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 * Revision 1.16  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.15  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.13  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.12  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.4  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.3  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.2  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/