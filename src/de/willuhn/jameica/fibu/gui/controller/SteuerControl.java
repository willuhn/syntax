/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/SteuerControl.java,v $
 * $Revision: 1.10 $
 * $Date: 2004/01/29 01:21:51 $
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
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.SteuerKontoSearchDialog;
import de.willuhn.jameica.fibu.gui.views.SteuerListe;
import de.willuhn.jameica.fibu.gui.views.SteuerNeu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.controller.AbstractControl;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.parts.*;
import de.willuhn.jameica.gui.parts.DecimalInput;
import de.willuhn.jameica.gui.parts.Input;
import de.willuhn.jameica.gui.parts.Table;
import de.willuhn.jameica.gui.parts.TextInput;
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
  /**
   * @param view
   */
  public SteuerControl(AbstractView view)
  {
    super(view);
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
  public Table getSteuerListe() throws RemoteException
	{
		DBIterator list = Settings.getDatabase().createList(Steuer.class);
		list.setOrder("order by name desc");

		Table table = new Table(list,this);
		table.addColumn(I18N.tr("Name"),"name");
		table.addColumn(I18N.tr("Steuersatz"),"satz",new CurrencyFormatter("%",Fibu.DECIMALFORMAT));
		table.addColumn(I18N.tr("Steuer-Sammelkonto"),"steuerkonto_id");
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
		satz = new DecimalInput(Fibu.DECIMALFORMAT.format(getSteuer().getSatz()));
		satz.addComment("%",null);
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
		kontoauswahl = new SearchInput(getKonto().getKontonummer(), new SteuerKontoSearchDialog());
		return kontoauswahl;
	}


  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleDelete()
   */
  public void handleDelete()
  {
    try {

      MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
      box.setText(I18N.tr("Steuersatz wirklich löschen?"));
      box.setMessage(I18N.tr("Wollen Sie diesen Steuersatz wirklich löschen?"));
      if (box.open() != SWT.YES)
        return;

      // ok, wir loeschen das Objekt
      getSteuer().delete();
      GUI.setActionText(I18N.tr("Steuersatz gelöscht."));
    }
    catch (RemoteException e)
    {
      GUI.setActionText(I18N.tr("Fehler beim Löschen des Steuersatzes."));
      Application.getLog().error("unable to delete steuer");
    }
    catch (ApplicationException ae)
    {
    	GUI.setActionText(ae.getLocalizedMessage());
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView(SteuerListe.class.getName(),null);
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
        GUI.setActionText(I18N.tr("Steuersatz ungültig."));
        return;
      }
      catch (ParseException e)
      {
        GUI.setActionText(I18N.tr("Steuersatz ungültig."));
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
        GUI.setActionText(I18N.tr("Ausgewähltes Steuerkonto existiert nicht."));
        return;
      }
      getSteuer().setSteuerKonto((SteuerKonto) steuerkonten.next());
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      getSteuer().store();
      GUI.setActionText(I18N.tr("Steuersatz gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      GUI.setActionText("Fehler beim Speichern des Steuersatzes.");
      Application.getLog().error("unable to store steuer",e);
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Steuer steuer = (Steuer) Settings.getDatabase().createObject(Steuer.class,id);
      GUI.startView(SteuerNeu.class.getName(),steuer);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load steuer with id " + id);
      GUI.setActionText(I18N.tr("Steuersatz wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView(SteuerNeu.class.getName(),null);
  }

}

/*********************************************************************
 * $Log: SteuerControl.java,v $
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