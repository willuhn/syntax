/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.30 $
 * $Date: 2005/08/16 17:39:24 $
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
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.BaseKonto;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.CalendarDialog;
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
	private BaseBuchung buchung 		= null;

	// Eingabe-Felder
	private Input	text					   = null;
	private Input belegnummer		   = null;
	private Input betrag				   = null;

  private DecimalInput steuer				   = null;
  private DialogInput datum            = null;
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
  public BaseBuchung getBuchung() throws RemoteException
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
  public DialogInput getDatum() throws RemoteException
	{
		if (datum != null)
			return datum;
		
    Date d = getBuchung().getDatum();
    if (d == null)
      d = new Date();

    CalendarDialog cd = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    cd.setTitle(i18n.tr("Datum"));
    cd.setText(i18n.tr("Bitte wählen Sie das Datum für diese Buchung"));
    cd.setDate(d);
    cd.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        datum.setText(Fibu.DATEFORMAT.format((Date)event.data));
      }
    });
    String s = Fibu.DATEFORMAT.format(d);
    datum = new DialogInput(s,cd);
    datum.setValue(s);
    datum.setComment("");
    datum.enableClientControl();
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
		
    final SteuerListener sl = new SteuerListener();
		DBIterator list = Settings.getDBService().createList(Konto.class);
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
		d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        BaseKonto k = (BaseKonto) event.data;
        if (k == null)
          return;
				try {
          kontoAuswahl.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung(), k.getName()}));
					kontoAuswahl.setValue(k.getKontonummer());
          kontoAuswahl.setText(k.getKontonummer());
          sl.handleEvent(event);
				}
				catch (RemoteException e)
				{
					Logger.error("unable to load konto",e);
				}

      }
    });
		
    BaseKonto k = getBuchung().getKonto();
    kontoAuswahl = new DialogInput(k == null ? null : k.getKontonummer(),d);
    kontoAuswahl.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung(), k.getName()}));
    kontoAuswahl.addListener(sl);
    sl.handleEvent(null);
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
          geldKontoAuswahl.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung(), k.getName()}));
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
    geldKontoAuswahl.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung(), k.getName()}));
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
  public DecimalInput getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;

		steuer = new DecimalInput(getBuchung().getSteuer(),Fibu.DECIMALFORMAT);
		steuer.setComment("%");
		return steuer;
	}

  /**
   * Speichert die Buchung.
   * @param startNew legt fest, ob danach sofort der Dialog zum Erfassen einer neuen Buchung geoeffnet werden soll.
   */
  public void handleStore(boolean startNew)
  {
    try {

      //////////////////////////////////////////////////////////////////////////
      // Belegnummer checken
      try {
        getBuchung().setBelegnummer(((Integer) getBelegnummer().getValue()).intValue());
      }
      catch (Exception e)
      {
        Logger.error("unable to set belegnummer",e);
        GUI.getView().setErrorText(i18n.tr("Belegnummer ungültig."));
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
        Logger.error("unable to set betrag",e);
        GUI.getView().setErrorText(i18n.tr("Betrag ungültig."));
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
        Logger.error("unable to set steuer",e);
        GUI.getView().setErrorText(i18n.tr("Steuersatz ungültig."));
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
            GUI.getView().setErrorText(i18n.tr("Datum ungültig."));
            return;
          }
        }
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Konto checken
      
      String s = (String) getKontoAuswahl().getText();
      if (s == null || s.length() == 0)
      {
        GUI.getView().setErrorText(i18n.tr("Bitten geben Sie ein Konto ein."));
        return;
      }
      DBIterator konten = Settings.getDBService().createList(Konto.class);
      konten.addFilter("kontonummer = '" + s + "'");
      if (!konten.hasNext())
      {
        GUI.getView().setErrorText(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));
        return;
      }
      getBuchung().setKonto((Konto) konten.next());
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // GeldKonto checken
      
      s = (String) getGeldKontoAuswahl().getText();
      if (s == null || s.length() == 0)
      {
        GUI.getView().setErrorText(i18n.tr("Bitten geben Sie ein Geldkonto ein."));
        return;
      }
      konten = Settings.getDBService().createList(GeldKonto.class);
      konten.addFilter("kontonummer = '" + s + "'");
      if (!konten.hasNext())
      {
        GUI.getView().setErrorText(i18n.tr("Das Geldkonto \"{0}\" existiert nicht.",s));
        return;
      }
      getBuchung().setGeldKonto((GeldKonto) konten.next());
      //
      //////////////////////////////////////////////////////////////////////////

			getBuchung().setText((String)getText().getValue());
      
      // wir speichern grundsaetzlich den aktiven Mandanten als Inhaber der Buchung
			getBuchung().setMandant(Settings.getActiveMandant());

      // und jetzt speichern wir.
			getBuchung().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr.") + " " + getBuchung().getBelegnummer() + " " + i18n.tr("gespeichert."));

      if (startNew)
        new de.willuhn.jameica.fibu.gui.action.BuchungNeu().handleAction(null);

    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Logger.error("unable to store buchung",e);
      GUI.getView().setErrorText("Fehler beim Speichern der Buchung.");
    }
    
  }

  /**
   * Listener, der das Feld fuer die Steuer aktualisiert.
   */
  private class SteuerListener implements Listener
  {

    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      try
      {
        String s = (String) getKontoAuswahl().getText();
        if (s == null || s.length() == 0)
        {
          getSteuer().disable();
          return;
        }

        DBIterator konten = Settings.getDBService().createList(Konto.class);
        konten.addFilter("kontonummer = '" + s + "'");
        if (!konten.hasNext())
        {
          GUI.getView().setErrorText(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));
          getSteuer().disable();
          return;
        }
        Konto k = (Konto) konten.next();
        Steuer ss = k.getSteuer();
        if (ss == null)
        {
          getSteuer().disable();
          return;
        }

        double satz = ss.getSatz();
        if (satz == 0.0d)
        {
          getSteuer().disable();
          return;
        }
        getSteuer().enable();
        getSteuer().setValue(new Double(satz));
      }
      catch (RemoteException e)
      {
        Logger.error("unable to determine steuer",e);
        GUI.getView().setErrorText(i18n.tr("Fehler beim Ermitten des Steuersatzes für das Konto"));
      }
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
        getDatum().setText(Fibu.DATEFORMAT.format(d));
        getDatum().setComment(i18n.tr(Fibu.WEEKDAYS[i]));
      }
      catch (RemoteException e1)
      {
      	Logger.error("unable to update week day",e1);
      }
		}
	}
}

/*********************************************************************
 * $Log: BuchungControl.java,v $
 * Revision 1.30  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.29  2005/08/15 23:38:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.28  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.27  2005/08/12 16:43:08  willuhn
 * @B DecimalInput
 *
 * Revision 1.26  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
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