/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/Settings.java,v $
 * $Revision: 1.8 $
 * $Date: 2004/01/29 00:06:46 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.gui.controller.SettingsControl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.LabelGroup;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class Settings extends AbstractView
{


  /**
   * @param parent
   */
  public Settings(Composite parent)
  {
    super(parent);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    SettingsControl control = new SettingsControl(this);

    // Headline malen
    addHeadline("Einstellungen");

    // Gruppe fachliche Einstellungen erzeugen
    LabelGroup group = new LabelGroup(getParent(),I18N.tr("fachliche Einstellungen"));

    try {

      group.addLabelPair(I18N.tr("aktiver Mandant"),		control.getMandant());
      group.addLabelPair(I18N.tr("Währungsbezeichnung"),control.getCurrency());
    }
    catch (RemoteException e)
    {
			Application.getLog().error("error while reading settings",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Einstellungen."));
    }

    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea(getParent(),control.storeAllowed() ? 2 : 1);
    buttonArea.addCancelButton(control);

    if (control.storeAllowed()) buttonArea.addStoreButton(control);
    
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }

}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.8  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.6  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.4  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/