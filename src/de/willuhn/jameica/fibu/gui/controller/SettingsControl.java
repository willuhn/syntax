/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/Attic/SettingsControl.java,v $
 * $Revision: 1.10 $
 * $Date: 2004/02/24 22:48:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.Welcome;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.controller.AbstractControl;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.Input;
import de.willuhn.jameica.gui.parts.LabelInput;
import de.willuhn.jameica.gui.parts.SelectInput;
import de.willuhn.jameica.gui.parts.TextInput;
import de.willuhn.util.I18N;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Einstellungen".
 * @author willuhn
 */
public class SettingsControl extends AbstractControl
{

	// Eingabe-Felder
	private Input mandant   = null;
	private Input currency  = null;

	private boolean storeAllowed = false;

  /**
   * @param view
   */
  public SettingsControl(AbstractView view)
  {
    super(view);
  }


	/**
	 * Liefert das Eingabe-Feld fuer die Auswahl des Mandanten.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getMandant() throws RemoteException
	{
		if (mandant != null)
			return mandant;

		Mandant m = de.willuhn.jameica.fibu.Settings.getActiveMandant();
		if (mandant == null)
			m = (Mandant) de.willuhn.jameica.fibu.Settings.getDatabase().createObject(Mandant.class,null);

		DBIterator list = m.getList();
		if (list.hasNext())
		{
			storeAllowed = true;
			mandant = new SelectInput(m);
		}
		else {
			mandant = new LabelInput(I18N.tr("Kein Mandant vorhanden. Bitte richten Sie zunächst einen ein."));
		}
		return mandant;
	}
      
	/**
	 * Liefert das Eingabe-Feld fuer die Waehrung.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getCurrency() throws RemoteException
	{
		if (currency != null)
			return currency;

		currency = new TextInput(de.willuhn.jameica.fibu.Settings.getCurrency());
		return currency;
	}

	/**
	 * Prueft, ob das Speichern freigegeben ist.
   * @return true, wenn das Speichern freigegeben ist.
   */
  public boolean storeAllowed()
	{
		return storeAllowed;
	}

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete(java.lang.String)
   */
  public void handleDelete(String id)
  {
    // nothing to delete
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete()
   */
  public void handleDelete()
  {
    // nothing to delete
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView(Welcome.class.getName(),null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
  	if (!storeAllowed)
  	{
			GUI.setActionText(I18N.tr("Bitte wählen Sie einen Mandanten aus."));
  		return;
  	}
    try {
			Settings.setCurrency(getCurrency().getValue());

      Mandant m = (Mandant) Settings.getDatabase().createObject(Mandant.class,getMandant().getValue());
      if (m.isNewObject())
      {
      	GUI.setActionText(I18N.tr("Bitte wählen Sie einen Mandanten aus."));
      	return;
      }
      Settings.setActiveMandant(m);
			GUI.setActionText(I18N.tr("Einstellungen gespeichert."));
    }
    catch (RemoteException e)
    {
      Application.getLog().error("error while saving the settings",e);
      GUI.setActionText(I18N.tr("Fehler beim Speichern der Einstellungen"));
    }

  }

  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleOpen(java.lang.Object)
   */
  public void handleOpen(Object o)
  {
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
  }

}

/*********************************************************************
 * $Log: SettingsControl.java,v $
 * Revision 1.10  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/27 23:54:18  willuhn
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
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/