/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/SteuerKontoSearchDialog.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/01/03 18:07:22 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.SearchDialog;

/**
 * @author willuhn
 */
public class SteuerKontoSearchDialog extends SearchDialog
{

  /**
   * @param object
   */
  public SteuerKontoSearchDialog()
  {
    // wir setzten die Liste, die der Dialog anzeigen soll.
    try {
      setList(Settings.getDatabase().createList(SteuerKonto.class));
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to init steuerkonto search dialog.",e);
      GUI.setActionText(I18N.tr("Fehler beim Öffnen des Such-Dialogs."));
    }
    
    // und definieren noch die Spalten, die im Dialog angezeigt werden sollen.
    addColumn(I18N.tr("Kontonummer"),"kontonummer");
    addColumn(I18N.tr("Name"),"name");

    // und wir definieren noch einen passenden Titel
    setTitle(I18N.tr("Auswahl des Steuer-Sammelkontos"));
  }

  /**
   * @see de.willuhn.jameica.views.SearchDialog#load(java.lang.String)
   */
  protected String load(String id)
  {
    if (id == null || "".equals(id))
      return "";
    try {
      SteuerKonto k = (SteuerKonto) Settings.getDatabase().createObject(SteuerKonto.class,id);
      return k.getKontonummer();
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load selected object.",e);
      GUI.setActionText(I18N.tr("Fehler beim Lesen des ausgewählten Steuer-Sammelkontos."));
      return "";
    }
  }
}

/*********************************************************************
 * $Log: SteuerKontoSearchDialog.java,v $
 * Revision 1.4  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.3  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 **********************************************************************/