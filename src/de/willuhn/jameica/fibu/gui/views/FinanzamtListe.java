/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtListe.java,v $
 * $Revision: 1.13 $
 * $Date: 2005/08/09 23:53:34 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.FinanzamtNeu;
import de.willuhn.jameica.fibu.gui.part.FinanzamtList;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Zeigt die Liste der Finanzaemter an.
 * @author willuhn
 */
public class FinanzamtListe extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Liste der Finanzämter."));

    Container container = new LabelGroup(getParent(),"Vorhandene Finanzämter");
    
    Mandant m = Settings.getActiveMandant();
    container.addText(i18n.tr("Mandant: {0}, Geschäftsjahr: {1}", new String[]{m.getFirma(),(String)m.getAttribute("geschaeftsjahr")}),false);

    Part p = new FinanzamtList(new FinanzamtNeu());
    p.paint(getParent());
    
    ButtonArea buttons = new ButtonArea(getParent(),2);
    buttons.addButton(i18n.tr("Neues Finanzamt erfassen"), new FinanzamtNeu(),null,true);
    buttons.addButton(i18n.tr("Zurück"), new Back());
  }


  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: FinanzamtListe.java,v $
 * Revision 1.13  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.12  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 21:38:05  willuhn
 * @C refactoring finished
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/19 01:43:43  willuhn
 * @C small fixes
 *
 * Revision 1.5  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.2  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/