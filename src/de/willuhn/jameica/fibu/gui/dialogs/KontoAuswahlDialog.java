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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.part.KontoList;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.parts.table.FeatureSummary;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Ein Dialog, ueber den man ein Konto auswaehlen kann.
 */
public class KontoAuswahlDialog extends AbstractDialog
{
	private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static Settings settings = new Settings(KontoAuswahlDialog.class);
  
  private final static int WINDOW_WIDTH = 650;
  private final static int WINDOW_HEIGHT = 350;

	private Konto choosen = null;
  private DBIterator konten = null;
  private String query = null;

  /**
   * ct.
   * @param konten
   * @param position
   * @param query optionale Angabe des Suchbegriffes fuer die Kontosuche.
   */
  public KontoAuswahlDialog(DBIterator konten, int position, String query)
  {
    super(position);
    this.konten = konten;
    this.query = query;
		this.setTitle(i18n.tr("Konto-Auswahl"));
    setSize(settings.getInt("window.width",WINDOW_WIDTH),settings.getInt("window.height",WINDOW_HEIGHT));
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    this.konten.begin();
		final KontoAuswahlList konten = new KontoAuswahlList();
    konten.setContextMenu(null);
    konten.setMulti(false);
    konten.removeFeature(FeatureSummary.class);
    konten.paint(parent);

		ButtonArea b = new ButtonArea(parent,2);
		b.addButton(i18n.tr("Übernehmen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				Object o = konten.getSelection();
        if (o == null || !(o instanceof Konto))
          return;

        choosen = (Konto) o;
        close();
      }
    },null,true,"ok.png");
		b.addButton(i18n.tr("Abbrechen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				throw new OperationCanceledException();
      }
    },null,false,"process-stop.png");
		
    getShell().setMinimumSize(WINDOW_WIDTH,WINDOW_HEIGHT);
    
    getShell().addDisposeListener(new DisposeListener() {
      
      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        Shell shell = getShell();
        if (shell == null || shell.isDisposed())
          return;
        
        Point size = shell.getSize();
        Logger.debug("saving window size: " + size.x + "x" + size.y);
        settings.setAttribute("window.width",size.x);
        settings.setAttribute("window.height",size.y);
      }
    });

  }

  /**
   * Liefert das ausgewaehlte Konto zurueck oder <code>null</code> wenn der
   * Abbrechen-Knopf gedrueckt wurde.
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return choosen;
  }
  
  /**
   * Überschrieben, damit die Tabelle eine eigene ID bekommt und damit auch eigene Spaltenbreiten haben kann.
   */
  private class KontoAuswahlList extends KontoList
  {
    private KontoAuswahlList() throws RemoteException
    {
      super(null,konten,query,context -> {
        if (context == null || !(context instanceof Konto))
          return;
        choosen = (Konto) context;
        close();
      });
      
      setRememberColWidths(false);
      setRememberState(false);
    }
  }

}
