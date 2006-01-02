/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungstemplateListe.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/01/02 23:35:33 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.BuchungstemplateNeu;
import de.willuhn.jameica.fibu.gui.part.BuchungstemplateList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Zeigt die Liste der Buchungs-Vorlagen an.
 * @author willuhn
 */
public class BuchungstemplateListe extends AbstractView
{


  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Buchungsvorlagen"));

    Part p = new BuchungstemplateList(new BuchungstemplateNeu());
    p.paint(getParent());
    
    ButtonArea buttons = new ButtonArea(getParent(),2);
    buttons.addButton(i18n.tr("Zurück"), new Back());
    buttons.addButton(new Button(i18n.tr("Neue Buchungsvorlage"), new BuchungstemplateNeu(),null,true));
  }


  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: BuchungstemplateListe.java,v $
 * Revision 1.3  2006/01/02 23:35:33  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/01/02 22:56:50  willuhn
 * @B typo
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/