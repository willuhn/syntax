/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/SteuerControl.java,v $
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
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.SteuerListe;
import de.willuhn.jameica.fibu.gui.views.SteuerNeu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.ListDialog;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class SteuerControl extends AbstractControl
{

	// Fach-Objekte.
	private Steuer steuer = null;
	private Konto konto   = null;

	// Eingabe-Felder
	private Input name					= null;
	private Input satz    			= null;
	private Input kontoauswahl	= null;
  
  private I18N i18n;
  
  /**
   * @param view
   */
  public SteuerControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

	/**
	 * Liefert die Steuer.
   * @return Steuer.
   * @throws RemoteException
   */
  public Steuer getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;
			
		steuer = (Steuer) getCurrentObject();
		if (steuer != null)
			return steuer;

		steuer = (Steuer) Settings.getDatabase().createObject(Steuer.class,null);
		return steuer;

	}

	/**
	 * Liefert das Steuerkonto.
   * @return Steuerkonto.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException
	{
		if (konto != null)
			return konto;
			
		konto = getSteuer().getSteuerKonto();
		if (konto != null)
			return konto;

		konto = (SteuerKonto) Settings.getDatabase().createObject(SteuerKonto.class,null);
			return konto;
	}

	/**
	 * Liefert eine Tabelle mit den Steuersaetzen.
   * @return Tabelle.
   * @throws RemoteException
   */
  public TablePart getSteuerListe() throws RemoteException
	{
		DBIterator list = Settings.getDatabase().createList(Steuer.class);
		list.setOrder("order by name desc");

		TablePart table = new TablePart(list,new de.willuhn.jameica.fibu.gui.action.SteuerNeu());
		table.addColumn(i18n.tr("Name"),"name");
		table.addColumn(i18n.tr("Steuersatz"),"satz",new CurrencyFormatter("%",Fibu.DECIMALFORMAT));
		table.addColumn(i18n.tr("Steuer-Sammelkonto"),"steuerkonto_id");
		return table;
	}
      
	/**
	 * Liefert ein Eingabe-Feld fuer den Namen des Steuersatzes.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getName() throws RemoteException
	{
		if (name != null)
			return name;
		name = new TextInput(getSteuer().getName());
		return name;
	}

	/**
	 * Liefert ein Eingabe-Feld fuer den Steuersatz.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getSatz() throws RemoteException
	{
		if (satz != null)
			return satz;
		satz = new DecimalInput(getSteuer().getSatz(), Fibu.DECIMALFORMAT);
		satz.setComment("%");
		return satz;
	}

	/**
	 * Liefert ein Auswahl-Feld fuer das Steuer-Konto.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getKontoAuswahl() throws RemoteException
	{
		if (kontoauswahl != null)
			return kontoauswahl;

		DBIterator list = Settings.getDatabase().createList(SteuerKonto.class);
		ListDialog d = new ListDialog(list,ListDialog.POSITION_MOUSE);
		d.addColumn(i18n.tr("Kontonummer"),"kontonummer");
		d.addColumn(i18n.tr("Name"),"name");
		d.setTitle(i18n.tr("Auswahl des Steuer-Sammelkontos"));
		d.addListener(new Listener() {
      public void handleEvent(Event event) {
      	SteuerKonto k = (SteuerKonto) event.data;
      	try {
					kontoauswahl.setValue(k.getKontonummer());
      	}
      	catch (RemoteException e)
      	{
      		Application.getLog().error("unable to load konto",e);
      	}
      }
    });
		kontoauswahl = new SearchInput(getKonto().getKontonummer(),d);
		return kontoauswahl;
	}


  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    try {

      getSteuer().setName(getName().getValue());

      //////////////////////////////////////////////////////////////////////////
      // Steuersatz checken
      try {
        getSteuer().setSatz(Fibu.DECIMALFORMAT.parse(getSatz().getValue()).doubleValue());
      }
      catch (NumberFormatException e)
      {
        GUI.getView().setErrorText(i18n.tr("Steuersatz ungültig."));
        return;
      }
      catch (ParseException e)
      {
        GUI.getView().setErrorText(i18n.tr("Steuersatz ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Steuerkonto checken
      
      DBIterator steuerkonten = Settings.getDatabase().createList(SteuerKonto.class);
      steuerkonten.addFilter("kontonummer='"+getKontoAuswahl().getValue() + "'");
      // TODO: Geht grad nicht
      if (!steuerkonten.hasNext())
      {
        GUI.getView().setErrorText(i18n.tr("Ausgewähltes Steuerkonto existiert nicht."));
        return;
      }
      getSteuer().setSteuerKonto((SteuerKonto) steuerkonten.next());
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      getSteuer().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Steuersatz gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.getStatusBar().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      GUI.getStatusBar().setErrorText("Fehler beim Speichern des Steuersatzes.");
      Logger.error("unable to store steuer",e);
    }
    
  }
}

/*********************************************************************
 * $Log: SteuerControl.java,v $
 * Revision 1.12  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.11  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/29 01:21:51  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/29 01:14:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
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
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/