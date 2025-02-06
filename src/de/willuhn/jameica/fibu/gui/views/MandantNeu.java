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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;

import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateNeu;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrClose;
import de.willuhn.jameica.fibu.gui.action.GeschaeftsjahrNeu;
import de.willuhn.jameica.fibu.gui.action.KontoNeu;
import de.willuhn.jameica.fibu.gui.action.KontozuordnungNeu;
import de.willuhn.jameica.fibu.gui.action.MandantDelete;
import de.willuhn.jameica.fibu.gui.action.SteuerNeu;
import de.willuhn.jameica.fibu.gui.controller.MandantControl;
import de.willuhn.jameica.fibu.gui.part.BuchungstemplateList;
import de.willuhn.jameica.fibu.gui.part.GeschaeftsjahrList;
import de.willuhn.jameica.fibu.gui.part.KontoList;
import de.willuhn.jameica.fibu.gui.part.KontoZuordnungList;
import de.willuhn.jameica.fibu.gui.part.SteuerList;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
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
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  private static Integer activeTab = null;
  private TabFolder tabs           = null;

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    GUI.getView().setTitle(i18n.tr("Mandant bearbeiten"));

    final MandantControl control = new MandantControl(this);

    ColumnLayout cols = new ColumnLayout(getParent(),2);
    
    Container group = new SimpleContainer(cols.getComposite());
    group.addHeadline(i18n.tr("Eigenschaften"));
    group.addLabelPair(i18n.tr("Name 1")  , control.getName1());
    group.addLabelPair(i18n.tr("Name 2")  , control.getName2());
    group.addLabelPair(i18n.tr("Kürzel")  , control.getKuerzel());
    group.addLabelPair(i18n.tr("Firma")   , control.getFirma());
    group.addLabelPair(i18n.tr("Strasse") , control.getStrasse());
    group.addLabelPair(i18n.tr("PLZ")     , control.getPLZ());
    group.addLabelPair(i18n.tr("Ort")     , control.getOrt());

    Container group2 = new SimpleContainer(cols.getComposite());
    group2.addHeadline(i18n.tr("Buchhalterische Daten"));
    group2.addLabelPair(i18n.tr("Finanzamt"),		control.getFinanzamtAuswahl());
    group2.addLabelPair(i18n.tr("Steuernummer"),	control.getSteuernummer());
    group2.addLabelPair(i18n.tr("Währungsbezeichnung"), control.getWaehrung());

    ButtonArea buttonArea = new ButtonArea();

    boolean canClose = false;
    boolean canDelete = true;
    final Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    final Mandant mandant = control.getMandant();
    if (current != null)
    {
      Mandant cm = current.getMandant();
      canDelete = !cm.equals(mandant);
      canClose = !canDelete && !current.isClosed();
    }
    
    Button close = new Button(i18n.tr("Aktuelles Geschäftsjahr abschließen..."),new GeschaeftsjahrClose(),current,false,"go-next.png");
    close.setEnabled(canClose);
    buttonArea.addButton(close);
    
    Button delete = new Button(i18n.tr("Mandant löschen"),new MandantDelete(),getCurrentObject(),false,"user-trash-full.png");
    delete.setEnabled(canDelete);
    buttonArea.addButton(delete);
    Button button1 = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,control.storeAllowed(),"document-save.png");
    button1.setEnabled(control.storeAllowed());
    buttonArea.addButton(button1);
    
    buttonArea.paint(getParent());

    this.tabs = new TabFolder(getParent(), SWT.NONE);
    this.tabs.setLayoutData(new GridData(GridData.FILL_BOTH));
    this.tabs.setBackground(Color.BACKGROUND.getSWTColor());

    TabGroup jahre = new TabGroup(this.tabs,i18n.tr("Vorhandene Geschäftsjahre"));
    TablePart t1 = new GeschaeftsjahrList(mandant, new GeschaeftsjahrNeu());
    t1.paint(jahre.getComposite());

    TabGroup konten = new TabGroup(this.tabs,i18n.tr("Benutzerdefinierte Konten"),false,1);
    
    DBIterator kontenList = null;
    {
      // Als Kontenrahmen nehmen wir den des letzten Geschaeftsjahres
      DBIterator i = mandant.getGeschaeftsjahre();
      i.setOrder("order by beginn desc");
      if (i.hasNext())
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) i.next();
        kontenList = jahr.getKontenrahmen().getKonten();
        // Wir muessen trotzdem noch auf die benutzerdefinierten Konten einschraenken
        kontenList.addFilter("mandant_id = " + mandant.getID());
      }
    }
    KontoList t2 = new KontoList(mandant, kontenList != null ? kontenList : PseudoIterator.fromArray(new Konto[0]), new KontoNeu());
    t2.setFilterVisible(false);
    t2.paint(konten.getComposite());

    TabGroup steuern = new TabGroup(this.tabs,i18n.tr("Steuersätze"));
    TablePart t3 = new SteuerList(mandant, new SteuerNeu());
    t3.paint(steuern.getComposite());
    
    TabGroup vorlagen = new TabGroup(this.tabs,i18n.tr("Buchungsvorlagen"));
    DBIterator vorlagenList = Settings.getDBService().createList(Buchungstemplate.class);
    vorlagenList.addFilter("mandant_id = " + mandant.getID());
    vorlagenList.setOrder("order by name");
    TablePart t4 = new BuchungstemplateList(mandant, vorlagenList, new BuchungstemplateNeu());
    t4.paint(vorlagen.getComposite());
    
    if (Application.getPluginLoader().getPlugin("de.willuhn.jameica.hbci.HBCI") != null)
    {
	    TabGroup kontenZuordnungen = new TabGroup(this.tabs,i18n.tr("Zuordnung Bankkonten"));
	    DBIterator kontoZuordnungList = Settings.getDBService().createList(de.willuhn.jameica.fibu.rmi.Kontozuordnung.class);
	    kontoZuordnungList.addFilter("mandant_id = " + mandant.getID());
	    kontoZuordnungList.setOrder("order by name");
	    TablePart t5 = new KontoZuordnungList(mandant, kontoZuordnungList, new KontozuordnungNeu());
	    t5.paint(kontenZuordnungen.getComposite());
    }

    if (activeTab != null)
      this.tabs.setSelection(activeTab);
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
