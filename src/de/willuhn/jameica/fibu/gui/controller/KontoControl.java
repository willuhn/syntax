/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/KontoControl.java,v $
 * $Revision: 1.15 $
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

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.KontoListe;
import de.willuhn.jameica.fibu.gui.views.KontoNeu;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
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
				Steuer s = (Steuer) Settings.getDatabase().createObject(Steuer.class,getSteuer().getValue());
				if (s.isNewObject())
				{
					GUI.setActionText(I18N.tr("Bitte wählen Sie einen Steuersatz aus."));
					return;
				}
				getKonto().setSteuer(s);
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
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleOpen(java.lang.Object)
   */
  public void handleOpen(Object o)
  {
    GUI.startView(KontoNeu.class.getName(),o);
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
 * Revision 1.15  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.14  2004/02/25 23:11:29  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2004/02/18 13:51:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/28 00:37:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 23:54:18  willuhn
 * *** empty log message ***
 *
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