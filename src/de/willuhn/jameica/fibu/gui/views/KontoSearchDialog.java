/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/KontoSearchDialog.java,v $
 * $Revision: 1.4 $
 * $Date: 2003/12/11 21:00:34 $
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
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.SearchDialog;

/**
 * @author willuhn
 */
public class KontoSearchDialog extends SearchDialog
{

  /**
   * @param object
   */
  public KontoSearchDialog()
  {
    // wir setzten die Liste, die der Dialog anzeigen soll.
    try {
      setList(Application.getDefaultDatabase().createList(Konto.class));
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      Application.getLog().error("unable to init geldkonto search dialog.");
      GUI.setActionText(I18N.tr("Fehler beim Öffnen des Such-Dialogs."));
    }
    
    // und definieren noch die Spalten, die im Dialog angezeigt werden sollen.
    addColumn(I18N.tr("Kontonummer"),"kontonummer");
    addColumn(I18N.tr("Name"),"name");
    addColumn(I18N.tr("Kontoart"),"kontoart");
    addColumn(I18N.tr("Steuer"),"steuer_id");
    
    // und wir definieren noch einen passenden Titel
    setTitle(I18N.tr("Auswahl des Kontos"));
  }

  /**
   * @see de.willuhn.jameica.views.SearchDialog#load(java.lang.String)
   */
  protected String load(String id)
  {
    if (id == null || "".equals(id))
      return "";
    try {
      Konto gk = (Konto) Application.getDefaultDatabase().createObject(Konto.class,id);
      return gk.getKontonummer();
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      Application.getLog().error("unable to load selected object.");
      GUI.setActionText(I18N.tr("Fehler beim Lesen des ausgewählten Geldkontos."));
      return "";
    }
  }
}

/*********************************************************************
 * $Log: KontoSearchDialog.java,v $
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