/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.19 $
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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.BuchungListe;
import de.willuhn.jameica.fibu.gui.views.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.server.KontoImpl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.controller.AbstractControl;
import de.willuhn.jameica.gui.dialogs.ListDialog;
import de.willuhn.jameica.gui.parts.CurrencyFormatter;
import de.willuhn.jameica.gui.parts.DateFormatter;
import de.willuhn.jameica.gui.parts.DecimalInput;
import de.willuhn.jameica.gui.parts.Input;
import de.willuhn.jameica.gui.parts.SearchInput;
import de.willuhn.jameica.gui.parts.Table;
import de.willuhn.jameica.gui.parts.TextInput;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer Buchungen.
 */
public class BuchungControl extends AbstractControl
{
	
	// Fachobjekte
	private Buchung buchung 		= null;
	private Konto konto					= null;
	private GeldKonto geldkonto = null;

	// Eingabe-Felder
	private Input datum					   = null;
	private Input kontoAuswahl     = null;
	private Input geldKontoAuswahl = null;
	private Input	text					   = null;
	private Input belegnummer		   = null;
	private Input betrag				   = null;
	private Input steuer				   = null;

  /**
   * @param view
   */
  public BuchungControl(AbstractView view)
  {
    super(view);
  }

	/**
	 * Liefert die Buchung.
   * @return die Buchung.
   * @throws RemoteException
   */
  public Buchung getBuchung() throws RemoteException
	{
		if (buchung != null)
			return buchung;
		
		buchung = (Buchung) getCurrentObject();
		if (buchung != null)
			return buchung;
		
		buchung = (Buchung) Settings.getDatabase().createObject(Buchung.class,null);
		return buchung;
		
	}

	/**
	 * Liefert das Konto der Buchung.
   * @return Konto.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException
	{	
		if (konto != null)
			return konto;

		konto = getBuchung().getKonto();
		if (konto != null)
			return konto;
		
		konto = (Konto) Settings.getDatabase().createObject(Konto.class,null);
		return konto;
	}
	
	/**
	 * Liefert das Geld-Konto der Buchung.
	 * @return Geld-Konto.
	 * @throws RemoteException
	 */
	public GeldKonto getGeldKonto() throws RemoteException
	{	
		if (geldkonto != null)
			return geldkonto;

		geldkonto = getBuchung().getGeldKonto();
		if (geldkonto != null)
			return geldkonto;
		
		geldkonto = (GeldKonto) Settings.getDatabase().createObject(GeldKonto.class,null);
		return geldkonto;
	}

	/**
	 * Liefert eine Tabelle mit den Buchungen.
   * @return Tabelle.
   * @throws RemoteException
   */
  public Table getBuchungListe() throws RemoteException
	{
		DBIterator list = Settings.getDatabase().createList(Buchung.class);
		list.setOrder("order by id desc");

		Table table = new Table(list,this);
		table.addColumn(I18N.tr("Datum"),"datum", new DateFormatter(Fibu.DATEFORMAT));
		table.addColumn(I18N.tr("Konto"),"konto_id");
		table.addColumn(I18N.tr("Geldkonto"),"geldkonto_id");
		table.addColumn(I18N.tr("Text"),"buchungstext");
		table.addColumn(I18N.tr("Beleg"),"belegnummer");
		table.addColumn(I18N.tr("Netto-Betrag"),"betrag",new CurrencyFormatter(Settings.getCurrency(), Fibu.DECIMALFORMAT));
		return table;		
	}


	/**
	 * Liefert das Eingabe-Feld fuer das Datum.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getDatum() throws RemoteException
	{
		if (datum != null)
			return datum;
		
		datum = new TextInput(Fibu.DATEFORMAT.format(getBuchung().getDatum()));
		datum.setComment(I18N.tr("Wochentag: "));
		datum.addListener(new WochentagListener());
		return datum;
	}

	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Kontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getKontoAuswahl() throws RemoteException
	{
		if (kontoAuswahl != null)
			return kontoAuswahl;
		
		// TODO richtige Impl wird nicht gefunden.
		DBIterator list = Settings.getDatabase().createList(KontoImpl.class);
		ListDialog d = new ListDialog(list,ListDialog.POSITION_MOUSE);
		d.addColumn(I18N.tr("Kontonummer"),"kontonummer");
		d.addColumn(I18N.tr("Name"),"name");
		d.addColumn(I18N.tr("Kontoart"),"kontoart_id");
		d.addColumn(I18N.tr("Steuer"),"steuer_id");
		d.setTitle(I18N.tr("Auswahl des Kontos"));
		d.addListener(new Listener() {
      public void handleEvent(Event event) {
        Konto k = (Konto) event.data;
				try {
					kontoAuswahl.setValue(k.getKontonummer());
				}
				catch (RemoteException e)
				{
					Application.getLog().error("unable to load konto",e);
				}

      }
    });
		
		kontoAuswahl = new SearchInput(getKonto().getKontonummer(),d);
		kontoAuswahl.addListener(new SaldoListener(kontoAuswahl,true));
		kontoAuswahl.setComment(I18N.tr("Saldo") + ": " + 
														Fibu.DECIMALFORMAT.format(getKonto().getSaldo()) + " " + 
														Settings.getCurrency());
		return kontoAuswahl;
	}


	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Geld-Kontos.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getGeldKontoAuswahl() throws RemoteException
	{
		if (geldKontoAuswahl != null)
			return geldKontoAuswahl;
		
		// TODO richtige Impl wird nicht gefunden.
		DBIterator list = Settings.getDatabase().createList(KontoImpl.class);
		ListDialog d = new ListDialog(list,ListDialog.POSITION_MOUSE);
		d.addColumn(I18N.tr("Kontonummer"),"kontonummer");
		d.addColumn(I18N.tr("Name"),"name");
		d.addColumn(I18N.tr("Kontoart"),"kontoart_id");
		d.addColumn(I18N.tr("Steuer"),"steuer_id");
		d.setTitle(I18N.tr("Auswahl des Kontos"));
		d.addListener(new Listener() {
			public void handleEvent(Event event) {
				Konto k = (Konto) event.data;
				try {
					geldKontoAuswahl.setValue(k.getKontonummer());
				}
				catch (RemoteException e)
				{
					Application.getLog().error("unable to load konto",e);
				}

			}
		});

		geldKontoAuswahl = new SearchInput(getGeldKonto().getKontonummer(),d);
		geldKontoAuswahl.addListener(new SaldoListener(geldKontoAuswahl,false));
		geldKontoAuswahl.setComment(I18N.tr("Saldo") + ": " +
															  Fibu.DECIMALFORMAT.format(getGeldKonto().getSaldo()) + " " +
															  Settings.getCurrency());
		return geldKontoAuswahl;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Buchungstext.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getText() throws RemoteException
	{
		if (text != null)
			return text;
		
		text = new TextInput(getBuchung().getText());
		return text;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Belegnummer.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getBelegnummer() throws RemoteException
	{
		if (belegnummer != null)
			return belegnummer;
		
		belegnummer = new TextInput(""+getBuchung().getBelegnummer());
		return belegnummer;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Betrag.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBetrag() throws RemoteException
	{
		if (betrag != null)
			return betrag;
		
		betrag = new DecimalInput(Fibu.DECIMALFORMAT.format(getBuchung().getBetrag()));
		betrag.setComment(Settings.getCurrency());
		return betrag;
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

		steuer = new DecimalInput(Fibu.DECIMALFORMAT.format(getBuchung().getSteuer()));
		steuer.setComment("%");
		return steuer;
	}


  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleDelete()
   */
  public void handleDelete()
  {

    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
    box.setText(I18N.tr("Buchung wirklich stornieren?"));
    box.setMessage(I18N.tr("Wollen Sie diese Buchung wirklich stornieren?"));

    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt und wechseln zurueck zur Buchungsliste
      try {
      	int beleg = getBuchung().getBelegnummer();
        getBuchung().delete();
        GUI.setActionText(I18N.tr("Buchung Nr. " + beleg + " storniert."));
      }
      catch (ApplicationException e1)
      {
        GUI.setActionText(e1.getLocalizedMessage());
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Fehler beim Stornieren der Buchung."));
        Application.getLog().error("unable to delete buchung");
      }
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView(BuchungListe.class.getName(),null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    try {

      //////////////////////////////////////////////////////////////////////////
      // Belegnummer checken
      try {
        getBuchung().setBelegnummer(Integer.parseInt(getBelegnummer().getValue()));
      }
      catch (NumberFormatException e)
      {
        GUI.setActionText(I18N.tr("Belegnummer ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Betrag checken
      try {
        getBuchung().setBetrag(Fibu.DECIMALFORMAT.parse(getBetrag().getValue()).doubleValue());
      }
      catch (NumberFormatException e)
      {
        GUI.setActionText(I18N.tr("Betrag ungültig."));
        return;
      }
      catch (ParseException e)
      {
        GUI.setActionText(I18N.tr("Betrag ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      

      //////////////////////////////////////////////////////////////////////////
      // Steuer checken
      try {
				getBuchung().setSteuer(Fibu.DECIMALFORMAT.parse(getSteuer().getValue()).doubleValue());
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
      // Datum checken
      
      String d = getDatum().getValue();
      try {
				getBuchung().setDatum(Fibu.DATEFORMAT.parse(d));
      }
      catch (ParseException e)
      {
        // ok, evtl. ein Datum in Kurzformat, wir versuchen's mal
        try {
					getBuchung().setDatum(Fibu.FASTDATEFORMAT.parse(d));
        }
        catch (ParseException e2)
        {
          try {
            // ok, evtl. 4-stelliges Datum mit GJ vom Mandanten
						getBuchung().setDatum(Fibu.FASTDATEFORMAT.parse(d + Settings.getActiveMandant().getGeschaeftsjahr()));
          }
          catch (ParseException e3)
          {
            GUI.setActionText(I18N.tr("Datum ungültig."));
            return;
          }
        }
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Konto checken
      
      DBIterator konten = Settings.getDatabase().createList(Konto.class);
      konten.addFilter("kontonummer = '"+getKontoAuswahl().getValue()+"'");
      if (!konten.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewähltes Konto existiert nicht."));
        return;
      }
			getBuchung().setKonto((Konto) konten.next());
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // GeldKonto checken
      
      DBIterator geldkonten = Settings.getDatabase().createList(GeldKonto.class);
      geldkonten.addFilter("kontonummer = '"+getGeldKontoAuswahl().getValue()+"'");
      if (!geldkonten.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewähltes Geld-Konto existiert nicht."));
        return;
      }
			getBuchung().setGeldKonto((GeldKonto) geldkonten.next());
      //
      //////////////////////////////////////////////////////////////////////////

			getBuchung().setText(getText().getValue());
      
      // wir speichern grundsaetzlich den aktiven Mandanten als Inhaber der Buchung
			getBuchung().setMandant(Settings.getActiveMandant());

      // und jetzt speichern wir.
			getBuchung().store();
      GUI.setActionText(I18N.tr("Buchung Nr.") + " " + getBuchung().getBelegnummer() + " " + I18N.tr("gespeichert."));
      // jetzt machen wir die Buchung leer, damit sie beim naechsten Druck
      // auf Speichern als neue Buchung gespeichert wird.
			getBuchung().clear();
      GUI.startView(BuchungNeu.class.getName(),getBuchung());

    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Application.getLog().error("unable to store buchung",e);
      GUI.setActionText("Fehler beim Speichern der Buchung.");
    }
    
  }

  /**
   * @see de.willuhn.jameica.gui.controller.AbstractControl#handleOpen(java.lang.Object)
   */
  public void handleOpen(Object o)
  {
    GUI.startView(BuchungNeu.class.getName(),o);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView(BuchungNeu.class.getName(),null);
  }




	/**
   * Listener, der hinter dem Buchungsdatum den Wochentag anzeigt.
   */
  private class WochentagListener implements Listener
	{

		/**
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event)
		{
			Text t = (Text) event.widget;
			if (t == null)
				return;
			String datum = t.getText();

			Date d = null;
			try {
				d = Fibu.DATEFORMAT.parse(datum);
			}
			catch (ParseException e)
			{
				// ok, evtl. ein Datum in Kurzformat, wir versuchen's mal
				try {
					d = Fibu.FASTDATEFORMAT.parse(datum);
				}
				catch (ParseException e2)
				{
					try {
						// ok, evtl. 4-stelliges Datum mit GJ vom Mandanten
						d = Fibu.FASTDATEFORMAT.parse(datum + Settings.getActiveMandant().getGeschaeftsjahr());
					}
					catch (Exception e3)
					{
						// Ne, hat keinen Zweck.
						return;
					}
				}
			}

			if (d == null)
				return;

			Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());
			cal.setTime(d);
			int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (i < 0 || i >= Fibu.WEEKDAYS.length)
				return;
			try
      {
        getDatum().setComment(I18N.tr("Wochentag: ") + I18N.tr(Fibu.WEEKDAYS[i]));
      }
      catch (RemoteException e1)
      {
      	Application.getLog().error("unable to update week day",e1);
      	GUI.setActionText(I18N.tr("Fehler bei der Ermittlung des Wochentags"));
      }
		}
	}

	/**
	 * Listener, der an die Auswahlbox des Kontos angehaengt wurden und
	 * den Saldo von dem gerade ausgewaehlten Konto als Kommentar anzeigt.
	 */
	private class SaldoListener implements Listener
	{

		private Input k = null;
		private boolean changeSteuer = false;
		
    /**
		 * ct.
     * @param k Das Eingabe-Feld des Kontos.
     * @param b true, wenn das Steuer-Eingabe-Feld mit dem Steuersatz des
     * ausgewaehlten Kontos ueberschrieben werden soll.
     */
    SaldoListener(Input k, boolean b)
		{
			this.k = k;
			this.changeSteuer = b;
		}

		/**
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event)
		{
			if (!(event.widget instanceof Text))
				return;
			try {
				Text t = (Text) event.widget;
				if (t == null)
					return;
				String kontonummer = t.getText();
				if (kontonummer == null || "".equals(kontonummer))
				{
					return; // Kontonummer fehlt oder ist leer -> keine Saldenermittlung moeglich
				}

				DBIterator list = Settings.getDatabase().createList(Konto.class);
				list.addFilter("kontonummer = '" + kontonummer + "'");
				if (!list.hasNext())
				{
					GUI.setActionText(I18N.tr("Das ausgewählte Konto existiert nicht."));
					return;
				} 
				Konto myKonto = (Konto) list.next();
				k.setComment(I18N.tr("Saldo") + ": " +  
														 Fibu.DECIMALFORMAT.format(myKonto.getSaldo()) +
														 " " + Settings.getCurrency());

				GUI.setActionText(I18N.tr("Ausgewähltes Konto: ") + myKonto.getName());
      
				if (changeSteuer) // Steuer soll geaendert werden
				{
					if (myKonto.getSteuer() == null) // ausgewaehltes Konto hat keine Steuer.
					{
						getSteuer().disable();
					}
					else {
						getSteuer().setValue(Fibu.DECIMALFORMAT.format(myKonto.getSteuer().getSatz()));
						getSteuer().enable();
					}
				}
			}
			catch (RemoteException es)
			{
				GUI.setActionText(I18N.tr("Fehler bei der Saldenermittlung des Kontos."));
			}
		}

	}

}

/*********************************************************************
 * $Log: BuchungControl.java,v $
 * Revision 1.19  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2004/01/29 01:11:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.16  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.15  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.13  2003/12/16 02:27:32  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.12  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.9  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.8  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.5  2003/11/25 01:23:19  willuhn
 * @N added Menu shortcuts
 *
 * Revision 1.4  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.3  2003/11/24 17:27:53  willuhn
 * @N Context menu in table
 *
 * Revision 1.2  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 **********************************************************************/