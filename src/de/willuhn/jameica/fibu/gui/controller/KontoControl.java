/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/KontoControl.java,v $
 * $Revision: 1.8 $
 * $Date: 2004/01/27 21:38:06 $
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
import de.willuhn.jameica.fibu.gui.views.KontoListe;
import de.willuhn.jameica.fibu.gui.views.KontoNeu;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.controller.AbstractControl;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;
import de.willuhn.jameica.gui.views.parts.Input;
import de.willuhn.jameica.gui.views.parts.LabelInput;
import de.willuhn.jameica.gui.views.parts.SelectInput;
import de.willuhn.jameica.gui.views.parts.TextInput;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer das Konto.
 */
public class KontoControl extends AbstractControl
{

	// Fach-Objekte
	private Konto konto			= null;

	// Eingabe-Felder
	private Input name			   = null;
	private Input kontonummer  = null;
	private Input steuer			 = null;
	private Input kontoart		 = null;
	private Input kontenrahmen = null;

  /**
   * @param view
   */
  public KontoControl(AbstractView view)
  {
    super(view);
  }

	/**
	 * Liefert das Konto.
   * @return
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException
	{
		if (konto != null)
			return konto;
		
		konto = (Konto) getCurrentObject();
		if (konto != null)
			return konto;

		konto = (Konto) Settings.getDatabase().createObject(Konto.class,null);
		return konto;
	}

	/**
	 * Liefert eine Tabelle mit allen Konten.
   * @return Tabelle.
   * @throws RemoteException
   */
  public Table getKontoListe() throws RemoteException
	{

		DBIterator list = Settings.getDatabase().createList(Konto.class);

		Table table = new Table(list,this);
		table.addColumn(I18N.tr("Kontonummer"),"kontonummer");
		table.addColumn(I18N.tr("Name"),"name");
		table.addColumn(I18N.tr("Kontoart"),"kontoart");
		table.addColumn(I18N.tr("Steuer"),"steuer_id");
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
		name = new TextInput(getKonto().getName());
		return name;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Kontonummer.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getKontonummer() throws RemoteException
	{
		if (kontonummer != null)
			return kontonummer;
		kontonummer = new TextInput(getKonto().getKontonummer());
		return kontonummer;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Steuer.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;

		if (getKonto().getKontoArt().isSteuerpflichtig())
			steuer = new SelectInput(getKonto().getSteuer());
		else
			steuer = new LabelInput("Konto besitzt keinen Steuersatz.");
		return steuer;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Konto-Art.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getKontoart() throws RemoteException
	{
		if (kontoart != null)
			return kontoart;

		Kontoart ka = getKonto().getKontoArt();
		kontoart = new LabelInput((String) ka.getField(ka.getPrimaryField()));
		return kontoart;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Kontenrahmen.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getKontenrahmen() throws RemoteException
	{
		if (kontenrahmen != null)
			return kontenrahmen;

		Kontenrahmen k = getKonto().getKontenrahmen();
		kontenrahmen = new LabelInput((String) k.getField(k.getPrimaryField()));
		return kontenrahmen;
	}

  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleDelete()
   */
  public void handleDelete()
  {
    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.OK);
    box.setText(I18N.tr("Fehler"));
    box.setMessage(I18N.tr("Konten dürfen nicht gelöscht werden."));
    box.open();
    return;
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView(KontoListe.class.getName(),null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    try {

      getKonto().setName(getName().getValue());
      getKonto().setKontonummer(getKontonummer().getValue());

      // Kontenrahmen und Kontoart darf nicht geaendert werden

			if (getKonto().getKontoArt().isSteuerpflichtig())
			{
				String s = getSteuer().getValue();
				Steuer steuer = null;
				if (s != null)
					steuer = (Steuer) Settings.getDatabase().createObject(Steuer.class,s);

				getKonto().setSteuer(steuer);
			}

      // und jetzt speichern wir.
      getKonto().store();
      GUI.setActionText(I18N.tr("Konto gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Application.getLog().error("unable to store konto",e);
      GUI.setActionText("Fehler beim Speichern des Kontos.");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Konto konto = (Konto) Settings.getDatabase().createObject(Konto.class,id);
      GUI.startView(KontoNeu.class.getName(),konto);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load konto with id " + id);
      GUI.setActionText(I18N.tr("Konto wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.OK);
    box.setText(I18N.tr("Fehler"));
    box.setMessage(I18N.tr("Neue Konten können nicht angelegt werden."));
    box.open();
    return;
  }

}

/*********************************************************************
 * $Log: KontoControl.java,v $
 * Revision 1.8  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.7  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
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
 * Revision 1.2  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/