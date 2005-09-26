/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/KontoControl.java,v $
 * $Revision: 1.22 $
 * $Date: 2005/09/26 15:15:39 $
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
import java.util.ArrayList;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
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
import de.willuhn.logging.Logger;
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
  private Input saldo        = null;
  
  private I18N i18n = null;

  /**
   * @param view
   */
  public KontoControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

	/**
	 * Liefert das Konto.
   * @return Liefert das Konto.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException
	{
		if (konto != null)
			return konto;
		
		konto = (Konto) getCurrentObject();
		if (konto != null)
			return konto;

		konto = (Konto) Settings.getDBService().createObject(Konto.class,null);
		return konto;
	}

  /**
   * Liefert ein Anzeige-Feld fuer den Saldo.
   * @return Anzeige-Feld.
   * @throws RemoteException
   */
  public Input getSaldo() throws RemoteException
  {
    if (this.saldo != null)
      return this.saldo;
    saldo = new LabelInput(Fibu.DECIMALFORMAT.format(getKonto().getSaldo()));
    saldo.setComment(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung());
    return saldo;
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
    {
      DBIterator list = Settings.getDBService().createList(Steuer.class);
      Kontenrahmen kr = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
      ArrayList found = new ArrayList();
      while (list.hasNext())
      {
        Steuer s = (Steuer) list.next();
        Konto k = s.getSteuerKonto();
        if (k == null)
          continue;
        Kontenrahmen kr2 = k.getKontenrahmen();
        if (kr2 == null)
          continue;
        if (kr2.equals(kr))
          found.add(s);
      }
      GenericIterator i = PseudoIterator.fromArray((Steuer[])found.toArray(new Steuer[found.size()]));
      steuer = new SelectInput(i,getKonto().getSteuer());
    }
		else
			steuer = new LabelInput(i18n.tr("Konto besitzt keinen Steuersatz."));
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
		kontoart = new LabelInput(ka.getName());
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
		kontenrahmen = new LabelInput(k.getName());
		return kontenrahmen;
	}

  /**
   * Speichert das Konto.
   */
  public void handleStore()
  {
    try {

      getKonto().setName((String) getName().getValue());
      getKonto().setKontonummer((String) getKontonummer().getValue());

      // Kontenrahmen und Kontoart darf nicht geaendert werden

      Input i = getSteuer();
      if (i instanceof SelectInput)
        getKonto().setSteuer((Steuer)i.getValue());

      // und jetzt speichern wir.
      getKonto().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Konto gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (Exception e)
    {
			Logger.error("unable to store konto",e);
      GUI.getView().setErrorText("Fehler beim Speichern des Kontos.");
    }
    
  }
}

/*********************************************************************
 * $Log: KontoControl.java,v $
 * Revision 1.22  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.20  2005/08/25 23:00:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.19  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2005/08/15 23:38:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2005/08/10 17:48:03  willuhn
 * @C refactoring
 *
 * Revision 1.16  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
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