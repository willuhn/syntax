/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/SteuerKontoSearchDialog.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/10 23:51:52 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.views;

import java.rmi.RemoteException;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.objects.SteuerKonto;
import de.willuhn.jameica.views.SearchDialog;

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
      setList(Application.getDefaultDatabase().createList(SteuerKonto.class));
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      Application.getLog().error("unable to init steuerkonto search dialog.");
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
      SteuerKonto k = (SteuerKonto) Application.getDefaultDatabase().createObject(SteuerKonto.class,id);
      return k.getKontonummer();
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      Application.getLog().error("unable to load selected object.");
      GUI.setActionText(I18N.tr("Fehler beim Lesen des ausgewählten Steuer-Sammelkontos."));
      return "";
    }
  }
}

/*********************************************************************
 * $Log: SteuerKontoSearchDialog.java,v $
 * Revision 1.1  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 **********************************************************************/