/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FinanzamtControl.java,v $
 * $Revision: 1.12 $
 * $Date: 2005/08/08 21:35:46 $
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.FinanzamtListe;
import de.willuhn.jameica.fibu.gui.views.FinanzamtNeu;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * 
 */
public class FinanzamtControl extends AbstractControl
{

	// Fach-Objekte
	private Finanzamt finanzamt   = null;

	// Eingabe-Felder
	private Input name		 	= null;
	private Input postfach 	= null;
	private Input strasse		= null;
	private Input plz				= null;
	private Input ort				= null;

  private I18N i18n;

  /**
   * @param view
   */
  public FinanzamtControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

	/**
	 * Liefert das Finanzamt.
   * @return das Finanzamt.
   * @throws RemoteException
   */
  public Finanzamt getFinanzamt() throws RemoteException
	{
		if (finanzamt != null)
			return finanzamt;

		finanzamt = (Finanzamt) getCurrentObject();
		if (finanzamt != null)
			return finanzamt;
			
		finanzamt = (Finanzamt) Settings.getDatabase().createObject(Finanzamt.class,null);
		return finanzamt;
	}

	/**
	 * Liefert eine Tabelle mit den eingerichteten Finanzaemtern.
   * @return Tabelle.
   * @throws RemoteException
   */
  public TablePart getFinanzamtListe() throws RemoteException
	{
		DBIterator list = Settings.getDatabase().createList(Finanzamt.class);
		list.setOrder("order by name desc");

		TablePart table = new TablePart(list,new de.willuhn.jameica.fibu.gui.action.FinanzamtNeu());
		table.addColumn(i18n.tr("Name"),"name");
		table.addColumn(i18n.tr("Strasse"),"strasse");
		table.addColumn(i18n.tr("Postfach"),"postfach");
		table.addColumn(i18n.tr("PLZ"),"plz");
		table.addColumn(i18n.tr("Ort"),"ort");
		return table;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Namen.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getName() throws RemoteException
	{
		if (name != null)
			return name;
		name = new TextInput(getFinanzamt().getName());
		return name;
	}

	/**
	 * Liefert das Eingabe-Feld fuer das Postfach.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getPostfach() throws RemoteException
	{
		if (postfach != null)
			return postfach;
		postfach = new TextInput(getFinanzamt().getPostfach());
		return postfach;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Strasse.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getStrasse() throws RemoteException
	{
		if (strasse != null)
			return strasse;
		strasse = new TextInput(getFinanzamt().getStrasse());
		return strasse;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die PLZ.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getPLZ() throws RemoteException
	{
		if (plz != null)
			return plz;
		plz = new TextInput(getFinanzamt().getPLZ());
		return plz;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Ort.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getOrt() throws RemoteException
	{
		if (ort != null)
			return ort;
		ort = new TextInput(getFinanzamt().getOrt());
		return ort;
	}

  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleStore()
   */
  public void handleStore()
  {
    try {

      getFinanzamt().setName(getName().getValue());
      getFinanzamt().setStrasse(getStrasse().getValue());
      getFinanzamt().setPLZ(getPLZ().getValue());
      getFinanzamt().setPostfach(getPostfach().getValue());
      getFinanzamt().setOrt(getOrt().getValue());

      
      // und jetzt speichern wir.
      getFinanzamt().store();
      GUI.setActionText(i18n.tr("Daten des Finanzamtes gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Application.getLog().error("unable to store finanzamt",e);
      GUI.setActionText("Fehler beim Speichern der Daten des Finanzamtes.");
    }
    
  }
}

/*********************************************************************
 * $Log: FinanzamtControl.java,v $
 * Revision 1.12  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.11  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/29 00:06:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.8  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/16 02:27:32  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.1  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/