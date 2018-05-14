/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.dialogs;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.part.GeschaeftsjahrList;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Ein Dialog, ueber den das Geschaeftsjahr ausgewaehlt werden kann.
 */
public class GeschaeftsjahrAuswahlDialog extends AbstractDialog
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  private TablePart jahre         = null;
  private SelectInput ma          = null;

  private Mandant mandant         = null;
	private Geschaeftsjahr choosen  = null;

  /**
   * ct.
   * @param position
   */
  public GeschaeftsjahrAuswahlDialog(int position)
  {
    super(position);
		this.setTitle(i18n.tr("Auswahl des Geschäftsjahres"));
		setSize(500,SWT.DEFAULT);
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    Container group = new SimpleContainer(parent);
			
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    if (jahr != null)
      mandant = jahr.getMandant();
    else
    {
      // Wir laden per Default den ersten Mandanten, den wir finden
      DBIterator list = Settings.getDBService().createList(Mandant.class);
      if (list.hasNext())
        mandant = (Mandant) list.next();
    }

    group.addText(i18n.tr("Bitte wählen Sie Mandant und Geschäftsjahr."),true);

    ma = new SelectInput(Settings.getDBService().createList(Mandant.class),mandant);
    ma.setAttribute("firma");
    ma.setComment(mandant == null ? "" : i18n.tr("Steuernummer: {0}",mandant.getSteuernummer()));
    ma.setName(i18n.tr("Mandant"));
    ma.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        try
        {
          mandant = (Mandant) ma.getValue();
          if (mandant == null)
            return;
          
          choosen = null;
          
          ma.setComment(i18n.tr("Steuernummer: {0}",mandant.getSteuernummer()));
          if (jahre != null)
            jahre.removeAll();
          
          DBIterator list = mandant.getGeschaeftsjahre();
          while (list.hasNext())
          {
            jahre.addItem(list.next());
          }
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading gj",e);
          GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Lesen der Geschäftsjahre"));
        }
      }
    });
    group.addInput(ma);
    

    Action a = new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof Geschaeftsjahr))
          return;
        choosen = (Geschaeftsjahr) context;
        close();
      }
    };    

    new Headline(parent,i18n.tr("Geschäftsjahre"));
    
    jahre = new GeschaeftsjahrList(mandant,a);
    jahre.setContextMenu(null);
    jahre.setMulti(false);
    jahre.setSummary(false);
    jahre.paint(parent);

		ButtonArea b = new ButtonArea();
		b.addButton(i18n.tr("Übernehmen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				Object o = jahre.getSelection();
        if (o == null || !(o instanceof Geschaeftsjahr))
          return;

        choosen = (Geschaeftsjahr) o;
        close();
      }
    },null,true,"ok.png");
		b.addButton(i18n.tr("Abbrechen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				throw new OperationCanceledException();
      }
    },null,true,"process-stop.png");
		
		b.paint(parent);
  }

  /**
   * Liefert das ausgewaehlte Geschaeftsjahr oder <code>null</code> wenn der
   * Abbrechen-Knopf gedrueckt wurde.
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return choosen;
  }

}


/**********************************************************************
 * $Log: GeschaeftsjahrAuswahlDialog.java,v $
 * Revision 1.6  2011/05/12 09:10:32  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.5  2009-07-03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.4.2.1  2008/07/03 10:37:08  willuhn
 * @N Effektivere Erzeugung neuer Buchungsnummern
 * @B Nach Wechsel des Geschaeftsjahres nicht Dialog "Geschaeftsjahr bearbeiten" oeffnen
 *
 * Revision 1.4  2006/06/29 15:11:31  willuhn
 * @N Setup-Wizard fertig
 * @N Auswahl des Geschaeftsjahres
 *
 * Revision 1.3  2005/10/21 15:59:06  willuhn
 * @C getActiveGeschaeftsjahr cleanup
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/