/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/GeldKontoSearchDialog.java,v $
 * $Revision: 1.9 $
 * $Date: 2004/02/18 17:14:11 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.server.GeldKontoImpl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.SearchDialog;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class GeldKontoSearchDialog extends SearchDialog
{

  /**
   * @param object
   */
  public GeldKontoSearchDialog()
  {
    // wir setzten die Liste, die der Dialog anzeigen soll.
    try {
      // TODO: Falsche Impl
      setList(Settings.getDatabase().createList(GeldKontoImpl.class));
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to init geldkonto search dialog.",e);
      GUI.setActionText(I18N.tr("Fehler beim Öffnen des Such-Dialogs."));
    }
    
    // und definieren noch die Spalten, die im Dialog angezeigt werden sollen.
    addColumn(I18N.tr("Kontonummer"),"kontonummer");
    addColumn(I18N.tr("Name"),"name");
    addColumn(I18N.tr("Kontoart"),"kontoart_id");
    addColumn(I18N.tr("Steuer"),"steuer_id");

    // und wir definieren noch einen passenden Titel
    setTitle(I18N.tr("Auswahl des Geld-Kontos"));
  }

  /**
   * @see de.willuhn.jameica.views.SearchDialog#load(java.lang.String)
   */
  protected String load(String id)
  {
    if (id == null || "".equals(id))
      return "";
    try {
      GeldKonto gk = (GeldKonto) Settings.getDatabase().createObject(GeldKonto.class,id);
      return gk.getKontonummer();
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load selected object.",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen des ausgewählten Geldkontos."));
      return "";
    }
  }
}

/*********************************************************************
 * $Log: GeldKontoSearchDialog.java,v $
 * Revision 1.9  2004/02/18 17:14:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/29 00:13:20  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/10 00:47:29  willuhn
 * @N SearchDialog for Konto works now ;)
 *
 * Revision 1.2  2003/12/08 16:19:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/08 15:41:25  willuhn
 * @N searchInput
 *
 **********************************************************************/