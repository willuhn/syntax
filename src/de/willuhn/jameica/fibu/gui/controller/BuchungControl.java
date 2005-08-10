/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.25 $
 * $Date: 2005/08/10 17:48:03 $
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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.dialogs.KontoAuswahlDialog;
import de.willuhn.jameica.fibu.gui.views.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer Buchungen.
 */
public class BuchungControl extends AbstractControl
{
	
	// Fachobjekte
	private Buchung buchung 		= null;

	// Eingabe-Felder
	private Input datum					   = null;
	private Input	text					   = null;
	private Input belegnummer		   = null;
	private Input betrag				   = null;
	private Input steuer				   = null;

  private DialogInput kontoAuswahl     = null;
  private DialogInput geldKontoAuswahl = null;
  
  private I18N i18n;

  /**
   * @param view
   */
  public BuchungControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
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
		
		buchung = (Buchung) Settings.getDBService().createObject(Buchung.class,null);
		return buchung;
		
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
		datum.setComment("");
		datum.addListener(new WochentagListener());
		return datum;
	}

	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Kontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DialogInput getKontoAuswahl() throws RemoteException
	{
		if (kontoAuswahl != null)
			return kontoAuswahl;
		
		DBIterator list = Settings.getDBService().createList(Konto.class);
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
		d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        Konto k = (Konto) event.data;
        if (k == null)
          return;
				try {
          kontoAuswahl.setComment(i18n.tr("Saldo: {0} {1}",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung()}));
					kontoAuswahl.setValue(k.getKontonummer());
          kontoAuswahl.setText(k.getKontonummer());
          if (k.getSteuer() == null)
          {
            getSteuer().disable();
          }
          else
          {
            getSteuer().enable();
            getSteuer().setValue(Fibu.DECIMALFORMAT.format(k.getSteuer().getSatz()));
          }
				}
				catch (RemoteException e)
				{
					Logger.error("unable to load konto",e);
				}

      }
    });
		
    Konto k = getBuchung().getKonto();
    kontoAuswahl = new DialogInput(k == null ? null : k.getKontonummer(),d);
    kontoAuswahl.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1}",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung()}));
    if (k == null || k.getSteuer() == null)
    {
      getSteuer().disable();
    }
    else
    {
      getSteuer().enable();
      getSteuer().setValue(Fibu.DECIMALFORMAT.format(k.getSteuer().getSatz()));
    }
    return kontoAuswahl;
	}


	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Geld-Kontos.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public DialogInput getGeldKontoAuswahl() throws RemoteException
	{
		if (geldKontoAuswahl != null)
			return geldKontoAuswahl;
		
    DBIterator list = Settings.getDBService().createList(GeldKonto.class);
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        GeldKonto k = (GeldKonto) event.data;
        if (k == null)
          return;
        try {
          geldKontoAuswahl.setComment(i18n.tr("Saldo: {0} {1}",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung()}));
          geldKontoAuswahl.setValue(k.getKontonummer());
          geldKontoAuswahl.setText(k.getKontonummer());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load konto",e);
        }

      }
    });
    
    GeldKonto k = getBuchung().getGeldKonto();
    geldKontoAuswahl = new DialogInput(k == null ? null : k.getKontonummer(),d);
    geldKontoAuswahl.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1}",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung()}));
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
		
		belegnummer = new IntegerInput(getBuchung().getBelegnummer());
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
		
		betrag = new DecimalInput(getBuchung().getBetrag(), Fibu.DECIMALFORMAT);
		betrag.setComment(Settings.getActiveMandant().getWaehrung());
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

		steuer = new DecimalInput(getBuchung().getSteuer(),Fibu.DECIMALFORMAT);
		steuer.setComment("%");
		return steuer;
	}

  /**
   * Oeffnet den Dialog fuer eine neue Buchung.
   * @throws ApplicationException
   */
  public void handleNew() throws ApplicationException
  {
    new de.willuhn.jameica.fibu.gui.action.BuchungNeu().handleAction(null);
  }
  
  /**
   * Speichert die Buchung.
   */
  public void handleStore()
  {
    try {

      //////////////////////////////////////////////////////////////////////////
      // Belegnummer checken
      try {
        getBuchung().setBelegnummer(((Integer) getBelegnummer().getValue()).intValue());
      }
      catch (Exception e)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Belegnummer ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Betrag checken
      try {
        getBuchung().setBetrag(((Double)getBetrag().getValue()).doubleValue());
      }
      catch (Exception e)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Betrag ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      

      //////////////////////////////////////////////////////////////////////////
      // Steuer checken
      try {
				getBuchung().setSteuer(((Double)getSteuer().getValue()).doubleValue());
      }
      catch (Exception e)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Steuersatz ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Datum checken
      
      String d = (String) getDatum().getValue();
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
            Calendar cal = Calendar.getInstance();
            cal.setTime(Settings.getActiveMandant().getGeschaeftsjahrVon());
						getBuchung().setDatum(Fibu.FASTDATEFORMAT.parse(d + "" + cal.get(Calendar.YEAR)));
          }
          catch (ParseException e3)
          {
            GUI.getStatusBar().setErrorText(i18n.tr("Datum ungültig."));
            return;
          }
        }
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Konto checken
      
      Konto k = null;
      Object o = getKontoAuswahl().getValue();
      if (o == null)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Bitten geben Sie ein Konto ein."));
        return;
      }
      if (o instanceof String)
      {
        DBIterator konten = Settings.getDBService().createList(Konto.class);
        konten.addFilter("kontonummer = '"+o.toString()+"'");
        if (!konten.hasNext())
        {
          GUI.getStatusBar().setErrorText(i18n.tr("Ausgewähltes Konto existiert nicht."));
          return;
        }
        k = (Konto) konten.next();
      }
      else
      {
        k = (Konto) o;
      }
			getBuchung().setKonto(k);
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // GeldKonto checken
      
      GeldKonto gk = null;
      Object go = getGeldKontoAuswahl().getValue();
      if (go == null)
      {
        GUI.getStatusBar().setErrorText(i18n.tr("Bitten geben Sie ein Geldkonto ein."));
        return;
      }
      if (go instanceof String)
      {
        DBIterator konten = Settings.getDBService().createList(GeldKonto.class);
        konten.addFilter("kontonummer = '"+go.toString()+"'");
        if (!konten.hasNext())
        {
          GUI.getStatusBar().setErrorText(i18n.tr("Ausgewähltes Geldkonto existiert nicht."));
          return;
        }
        gk = (GeldKonto) konten.next();
      }
      else
      {
        gk = (GeldKonto) go;
      }
      getBuchung().setGeldKonto(gk);
      //
      //////////////////////////////////////////////////////////////////////////

			getBuchung().setText((String)getText().getValue());
      
      // wir speichern grundsaetzlich den aktiven Mandanten als Inhaber der Buchung
			getBuchung().setMandant(Settings.getActiveMandant());

      // und jetzt speichern wir.
			getBuchung().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr.") + " " + getBuchung().getBelegnummer() + " " + i18n.tr("gespeichert."));
      GUI.startView(BuchungNeu.class.getName(),getBuchung());
    }
    catch (ApplicationException e1)
    {
      GUI.getStatusBar().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Logger.error("unable to store buchung",e);
      GUI.getStatusBar().setErrorText("Fehler beim Speichern der Buchung.");
    }
    
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
            Calendar cal = Calendar.getInstance();
            cal.setTime(Settings.getActiveMandant().getGeschaeftsjahrVon());
            d = Fibu.FASTDATEFORMAT.parse(datum + "" + cal.get(Calendar.YEAR));
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
        getDatum().setComment(i18n.tr(Fibu.WEEKDAYS[i]));
      }
      catch (RemoteException e1)
      {
      	Logger.error("unable to update week day",e1);
      	GUI.getStatusBar().setErrorText(i18n.tr("Fehler bei der Ermittlung des Wochentags"));
      }
		}
	}
}

/*********************************************************************
 * $Log: BuchungControl.java,v $
 * Revision 1.25  2005/08/10 17:48:03  willuhn
 * @C refactoring
 *
 * Revision 1.24  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.23  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.22  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.21  2004/02/26 18:46:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.20  2004/02/25 23:11:29  willuhn
 * *** empty log message ***
 *
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