/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/dialogs/GeschaeftsjahrAuswahlDialog.java,v $
 * $Revision: 1.4 $
 * $Date: 2006/06/29 15:11:31 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.dialogs;

import java.rmi.RemoteException;

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
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.LabelGroup;
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

	private I18N i18n;
  
  private TablePart jahre = null;
  private DialogInput ma          = null;

  private Mandant mandant         = null;
	private Geschaeftsjahr choosen  = null;

  /**
   * ct.
   * @param position
   */
  public GeschaeftsjahrAuswahlDialog(int position)
  {
    super(position);

    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
		this.setTitle(i18n.tr("Auswahl des Geschäftsjahres"));
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    LabelGroup group = new LabelGroup(parent,i18n.tr("Auswahl"));
			
    // Wir laden per Default den ersten Mandanten, den wir finden
    DBIterator list = Settings.getDBService().createList(Mandant.class);
    if (list.hasNext())
      mandant = (Mandant) list.next();

    group.addText(i18n.tr("Bitte wählen Sie Mandant und Geschäftsjahr."),true);

    MandantAuswahlDialog d = new MandantAuswahlDialog(MandantAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        try
        {
          mandant = (Mandant) event.data;
          choosen = null;
          
          ma.setValue(mandant.getFirma());
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

    ma = new DialogInput(mandant == null ? "" : mandant.getFirma(),d);
    ma.setComment(mandant == null ? "" : i18n.tr("Steuernummer: {0}",mandant.getSteuernummer()));
    ma.disableClientControl();
    group.addLabelPair(i18n.tr("Mandant"),ma);
    

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

		ButtonArea b = new ButtonArea(parent,2);
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
    });
		b.addButton(i18n.tr("Abbrechen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				throw new OperationCanceledException();
      }
    });
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