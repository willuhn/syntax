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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

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
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Ein Dialog, ueber den man ein Konto auswaehlen kann.
 */
public class KontoAuswahlDialog extends AbstractDialog
{
	private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
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
    this.setSize(SWT.DEFAULT,350);
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    Action a = new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof Konto))
          return;
        choosen = (Konto) context;
        close();
      }
    };
    this.konten.begin();
		final KontoList konten = new KontoList(null,this.konten,this.query,a);
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
    },null,true);
		b.addButton(i18n.tr("Abbrechen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				throw new OperationCanceledException();
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

}
