/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FinanzamtControl.java,v $
 * $Revision: 1.10 $
 * $Date: 2004/01/29 00:06:47 $
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
import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.FinanzamtListe;
import de.willuhn.jameica.fibu.gui.views.FinanzamtNeu;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.controller.AbstractControl;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.Input;
import de.willuhn.jameica.gui.parts.Table;
import de.willuhn.jameica.gui.parts.TextInput;
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


  /**
   * @param view
   */
  public FinanzamtControl(AbstractView view)
  {
    super(view);
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
  public Table getFinanzamtListe() throws RemoteException
	{
		DBIterator list = Settings.getDatabase().createList(Finanzamt.class);
		list.setOrder("order by name desc");

		Table table = new Table(list,this);
		table.addColumn(I18N.tr("Name"),"name");
		table.addColumn(I18N.tr("Strasse"),"strasse");
		table.addColumn(I18N.tr("Postfach"),"postfach");
		table.addColumn(I18N.tr("PLZ"),"plz");
		table.addColumn(I18N.tr("Ort"),"ort");
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
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleDelete()
   */
  public void handleDelete()
  {

    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
    box.setText(I18N.tr("Finanzamt wirklich löschen?"));
    box.setMessage(I18N.tr("Wollen Sie die Daten dieses Finanzamtes wirklich löschen?"));
    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt
      try {
        getFinanzamt().delete();
        GUI.setActionText(I18N.tr("Daten des Finanzamtes gelöscht."));
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Fehler beim Löschen der Daten des Finanzamtes."));
        Application.getLog().error("unable to delete finanzamt");
      }
			catch (ApplicationException e1)
			{
				GUI.setActionText(e1.getLocalizedMessage());
			}
    }
  }

  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView(FinanzamtListe.class.getName(),null);
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
      GUI.setActionText(I18N.tr("Daten des Finanzamtes gespeichert."));
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

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Finanzamt fa = (Finanzamt) Settings.getDatabase().createObject(Finanzamt.class,id);
      GUI.startView(FinanzamtNeu.class.getName(),fa);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load finanzamt with id " + id);
      GUI.setActionText(I18N.tr("Daten des zu ladenden Finanzamtes wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView(FinanzamtNeu.class.getName(),null);
  }

}

/*********************************************************************
 * $Log: FinanzamtControl.java,v $
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