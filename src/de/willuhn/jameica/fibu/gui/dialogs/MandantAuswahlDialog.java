/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/dialogs/MandantAuswahlDialog.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/09 23:53:34 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.dialogs;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.part.MandantList;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Ein Dialog, ueber den man einen Mandanten auswaehlen kann.
 */
public class MandantAuswahlDialog extends AbstractDialog
{

	private I18N i18n;
	private Mandant choosen = null;

  /**
   * ct.
   * @param position
   */
  public MandantAuswahlDialog(int position)
  {
    super(position);

    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
		this.setTitle(i18n.tr("Mandant"));
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
		LabelGroup group = new LabelGroup(parent,i18n.tr("Verfügbare Mandanten"));
			
		group.addText(i18n.tr("Bitte wählen Sie den in dieser Sitzung zu verwendenden Mandanten aus."),true);

    Action a = new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof Mandant))
          return;
        choosen = (Mandant) context;
        close();
      }
    };    
		final MandantList mandanten = new MandantList(a);
    mandanten.setContextMenu(null);
    mandanten.setMulti(false);
    mandanten.setSummary(false);
    mandanten.paint(parent);

		ButtonArea b = new ButtonArea(parent,2);
		b.addButton(i18n.tr("Übernehmen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
				Object o = mandanten.getSelection();
        if (o == null || !(o instanceof Mandant))
          return;

        choosen = (Mandant) o;
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
   * Liefert den ausgewaehlten Mandanten zurueck oder <code>null</code> wenn der
   * Abbrechen-Knopf gedrueckt wurde.
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return choosen;
  }

}


/**********************************************************************
 * $Log: MandantAuswahlDialog.java,v $
 * Revision 1.1  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.5  2005/06/27 15:35:27  web0
 * @B bug 84
 *
 * Revision 1.4  2005/06/23 23:03:20  web0
 * @N much better KontoAuswahlDialog
 *
 * Revision 1.3  2004/10/25 23:12:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2004/10/19 23:33:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2004/10/17 16:28:46  willuhn
 * @N Die ersten Dauerauftraege abgerufen ;)
 *
 **********************************************************************/