/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoListe.java,v $
 * $Revision: 1.19 $
 * $Date: 2006/12/27 15:23:33 $
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
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
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

    String kr = i18n.tr("unbekannt");
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    try
    {
      kr = jahr.getKontenrahmen().getName();
    }
    catch (Exception e)
    {
      Logger.error("error while reading kr",e);
    }
    GUI.getView().setTitle(i18n.tr("Liste der Konten des Mandanten. Kontenrahmen: {0}",kr));

    Part p = new KontoList(jahr.getKontenrahmen().getKonten(),new KontoNeu());
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
 * Revision 1.19  2006/12/27 15:23:33  willuhn
 * @C merged update 1.3 and 1.4 to 1.3
 *
 * Revision 1.18  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.17  2005/09/01 21:08:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.16  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 * Revision 1.14  2005/08/25 23:00:02  willuhn
 * *** empty log message ***
 *
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