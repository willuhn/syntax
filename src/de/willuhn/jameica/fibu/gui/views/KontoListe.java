/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoListe.java,v $
 * $Revision: 1.6 $
 * $Date: 2004/01/27 21:38:05 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.gui.controller.KontoControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class KontoListe extends AbstractView
{


  /**
   * @param parent
   */
  public KontoListe(Composite parent)
  {
    super(parent);
  }


  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    addHeadline("Liste der Konten des aktiven Mandanten.");

		KontoControl control = new KontoControl(this);

    try {
      control.getKontoListe().paint(getParent());
    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading konto list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Konten."));
      e.printStackTrace();
    }
  }


  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }
}

/*********************************************************************
 * $Log: KontoListe.java,v $
 * Revision 1.6  2004/01/27 21:38:05  willuhn
 * @C refactoring finished
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/