/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoListe.java,v $
 * $Revision: 1.13 $
 * $Date: 2005/08/16 23:14:36 $
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
import de.willuhn.jameica.fibu.gui.action.KontoNeu;
import de.willuhn.jameica.fibu.gui.part.KontoList;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Zeigt eine Liste aller Konten an.
 * @author willuhn
 */
public class KontoListe extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Liste der Konten des Mandanten"));

    Part p = new KontoList(Settings.getDBService().createList(Konto.class),new KontoNeu());
    p.paint(getParent());
    
    ButtonArea buttons = new ButtonArea(getParent(),2);
    buttons.addButton(i18n.tr("Zurück"), new Back());
    buttons.addButton(i18n.tr("Neues Konto"), new KontoNeu(),null,true);

  }


  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: KontoListe.java,v $
 * Revision 1.13  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.12  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.11  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.10  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/28 00:31:34  willuhn
 * *** empty log message ***
 *
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