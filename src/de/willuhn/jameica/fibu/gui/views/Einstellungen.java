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
import de.willuhn.jameica.fibu.gui.action.KontenrahmenClone;
import de.willuhn.jameica.fibu.gui.controller.EinstellungenControl;
import de.willuhn.jameica.fibu.gui.part.KontenrahmenList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Einstellungsdialog.
 * @author willuhn
 */
public class Einstellungen extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N(); 

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    // Headline malen
    GUI.getView().setTitle(i18n.tr("Einstellungen"));

    final EinstellungenControl control = new EinstellungenControl(this);

    Container group = new SimpleContainer(getParent());

    group.addHeadline(i18n.tr("System-Einstellungen"));
    group.addInput(control.getSystemDataWritable());
    if (Application.getPluginLoader().isInstalled("de.willuhn.jameica.hbci.HBCI"))
      group.addInput(control.getSyncCheckmarks());
    
    group.addHeadline(i18n.tr("Buchungsrelevante Einstellungen"));
    group.addInput(control.getAbschreibungsKonto());
    group.addInput(control.getAbschreibungsKontoGWG());
    group.addInput(control.getGwgWert());
    
    ButtonArea buttonArea = new ButtonArea();
    buttonArea.addButton(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true,"document-save.png");
    buttonArea.paint(this.getParent());

    final KontenrahmenList list = new KontenrahmenList();
    new Headline(getParent(),i18n.tr("Kontenrahmen"));
    list.paint(getParent());

    ButtonArea buttonArea2 = new ButtonArea();
    buttonArea2.addButton(i18n.tr("Kontenrahmen duplizieren..."), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        new KontenrahmenClone().handleAction(list.getSelection());
      }
    },null,false,"edit-copy.png");
    buttonArea2.paint(this.getParent());
  }
}


/*********************************************************************
 * $Log: Einstellungen.java,v $
 * Revision 1.10  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.9  2011-03-21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 * Revision 1.8  2010-06-03 14:26:16  willuhn
 * @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
 * @C Code-Cleanup
 *
 * Revision 1.7  2010/06/02 00:02:59  willuhn
 * @N Mehr Icons
 *
 * Revision 1.6  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.4  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.3.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *********************************************************************/