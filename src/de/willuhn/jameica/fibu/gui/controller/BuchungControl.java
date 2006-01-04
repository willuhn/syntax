/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.64 $
 * $Date: 2006/01/04 17:05:32 $
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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenNeu;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.CalendarDialog;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
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
	
  private de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(BuchungControl.class);

  // Fachobjekte
	private Buchung buchung 		= null;

	// Eingabe-Felder
  private Input template         = null;
	private Input	text					   = null;
	private Input belegnummer		   = null;
	private Input betrag				   = null;

  private DecimalInput steuer				    = null;
  private DialogInput datum             = null;
  private KontoInput sollKontoAuswahl   = null;
  private KontoInput habenKontoAuswahl  = null;
  
  private CheckboxInput anlageVermoegen = null;
  
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
    buchung.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
		return buchung;
		
	}

  /**
   * Liefert eine Auswahlbox mit Buchungsvorlagen.
   * @return  Auswahlbox mit Buchungsvorlagen.
   * @throws RemoteException
   */
  public Input getBuchungstemplate() throws RemoteException
  {
    if (this.template != null)
      return this.template;
    DBIterator list = Settings.getDBService().createList(Buchungstemplate.class);
    if (list.size() == 0)
    {
      this.template = new LabelInput(i18n.tr("Keine Buchungsvorlagen vorhanden"));
    }
    else
    {
      this.template = new SelectInput(list,null);
      ((SelectInput)this.template).setPleaseChoose(i18n.tr("Bitte wählen..."));
      this.template.addListener(new Listener() {
        public void handleEvent(Event event)
        {
          try
          {
            Object o = template.getValue();
            if (o == null || !(o instanceof Buchungstemplate))
              return;
            Buchungstemplate t = (Buchungstemplate) o;
            getSollKontoAuswahl().setValue(t.getSollKonto());
            getHabenKontoAuswahl().setValue(t.getHabenKonto());
            getText().setValue(t.getText());
            getBetrag().setValue(new Double(t.getBetrag()));
            getSteuer().setValue(new Double(t.getSteuer()));
            
            // Das Setzen des Focus geht zwar. Aber danach laesst sich die
            // Combo-Box nicht mehr bedienen. Scheint ein SWT-Bug zu sein.
            // getBetrag().focus();
            new KontoListener().handleEvent(null);
          }
          catch (RemoteException e)
          {
            Logger.error("unable to apply tenplate data",e);
            GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Übernehmen der Vorlage"));
          }
        }
      });
    }
    return this.template;
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
    {
      // BUGZILLA 122
      String s = settings.getString("buchung.date.last." + Settings.getActiveGeschaeftsjahr().getID(),null);
      if (s != null && s.length() > 0)
      {
        try
        {
          d = Fibu.DATEFORMAT.parse(s);
        }
        catch (ParseException e)
        {
          Logger.error("unable to parse date",e);
        }
      }
      else
      {
        d = new Date();
      }
    }

    CalendarDialog cd = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    cd.setTitle(i18n.tr("Datum"));
    cd.setText(i18n.tr("Bitte wählen Sie das Datum für diese Buchung"));
    cd.setDate(d);
    cd.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        String s = Fibu.DATEFORMAT.format((Date)event.data);
        datum.setText(s);
        datum.setValue(s);
      }
    });
    String s = Fibu.DATEFORMAT.format(d);
    datum = new DialogInput(s,cd);
    datum.setValue(s);
    datum.setComment("");
    datum.enableClientControl();
    datum.addListener(new WochentagListener());
    if (getBuchung().getGeschaeftsjahr().isClosed())
      datum.disable();
    return datum;
  
  }

	/**
	 * Liefert das Eingabe-Feld zur Auswahl des SollKontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public KontoInput getSollKontoAuswahl() throws RemoteException
	{
		if (sollKontoAuswahl != null)
			return sollKontoAuswahl;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    sollKontoAuswahl = new KontoInput(jahr.getKontenrahmen().getKonten(), getBuchung().getSollKonto());
    sollKontoAuswahl.addListener(new KontoListener());
    if (getBuchung().getGeschaeftsjahr().isClosed())
      sollKontoAuswahl.disable();
    return sollKontoAuswahl;
  }


	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Haben-Kontos.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public KontoInput getHabenKontoAuswahl() throws RemoteException
	{
    if (habenKontoAuswahl != null)
      return habenKontoAuswahl;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    habenKontoAuswahl = new KontoInput(jahr.getKontenrahmen().getKonten(), getBuchung().getHabenKonto());
    habenKontoAuswahl.addListener(new KontoListener());
    if (getBuchung().getGeschaeftsjahr().isClosed())
      habenKontoAuswahl.disable();
    return habenKontoAuswahl;
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
    if (getBuchung().getGeschaeftsjahr().isClosed())
      text.disable();
		return text;
	}

  /**
   * Liefert eine Checkbox, mit der ausgewaehlt werden kann, ob zu der Buchung
   * gleich ein Datensatz im Anlagevermoegen angelegt werden soll.
   * @return Checkbox.
   * @throws RemoteException
   */
  public CheckboxInput getAnlageVermoegen() throws RemoteException
  {
    if (this.anlageVermoegen != null)
      return this.anlageVermoegen;
    this.anlageVermoegen = new CheckboxInput(false);
    this.anlageVermoegen.disable();
    if (getBuchung().getGeschaeftsjahr().isClosed())
      anlageVermoegen.disable();
    return this.anlageVermoegen;
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
    if (getBuchung().getGeschaeftsjahr().isClosed())
      belegnummer.disable();
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
		
		betrag = new DecimalInput(getBuchung().getBruttoBetrag(), Fibu.DECIMALFORMAT);
		betrag.setComment(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung());
    betrag.addListener(new SteuerListener());
    if (getBuchung().getGeschaeftsjahr().isClosed())
      betrag.disable();
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
    SteuerListener sl = new SteuerListener();
    steuer.addListener(sl);
    sl.handleEvent(null);
    if (getBuchung().getGeschaeftsjahr().isClosed())
      steuer.disable();
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
        throw new ApplicationException(i18n.tr("Belegnummer ungültig."));
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
        throw new ApplicationException(i18n.tr("Steuersatz ungültig."));
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Betrag checken
      try {
        double brutto = ((Double)getBetrag().getValue()).doubleValue();
        Math m = new Math();
        getBuchung().setBetrag(m.netto(brutto,getBuchung().getSteuer()));
      }
      catch (Exception e)
      {
        Logger.error("unable to set betrag",e);
        throw new ApplicationException(i18n.tr("Betrag ungültig."));
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Datum checken
      
      Object o = getDatum().getValue();
      Date   d = null;
      if (o instanceof Date)
      {
        d = (Date) o;
      }
      else
      {
        try {
          d = parse(o.toString());
        }
        catch (ParseException e)
        {
          throw new ApplicationException(i18n.tr("Datum ungültig."));
        }
      }
      if (d == null)
      {
        Logger.warn("no date given, using actual date");
        d = new Date();
      }
      getBuchung().setDatum(d);
      settings.setAttribute("buchung.date.last." + Settings.getActiveGeschaeftsjahr().getID(),Fibu.DATEFORMAT.format(d));
      //
      //////////////////////////////////////////////////////////////////////////
      
      getBuchung().setSollKonto((Konto) getSollKontoAuswahl().getValue());
      getBuchung().setHabenKonto((Konto) getHabenKontoAuswahl().getValue());
      getBuchung().setText((String)getText().getValue());
      
      // und jetzt speichern wir.
			getBuchung().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. {0} gespeichert.",""+getBuchung().getBelegnummer()));
      
      //////////////////////////////////////////////////////////////////////////
      // Anlagevermoegen
      if (getAnlageVermoegen().isEnabled() && ((Boolean)getAnlageVermoegen().getValue()).booleanValue())
      {
        Anlagevermoegen av = (Anlagevermoegen) Settings.getDBService().createObject(Anlagevermoegen.class,null);
        av.setAnschaffungsDatum(getBuchung().getDatum());
        av.setAnschaffungskosten(getBuchung().getBetrag());
        av.setKonto(getBuchung().getSollKonto());
        av.setName(getBuchung().getText());
        av.setMandant(Settings.getActiveGeschaeftsjahr().getMandant());
        new AnlagevermoegenNeu().handleAction(av);
      }
      else if (startNew)
        new de.willuhn.jameica.fibu.gui.action.BuchungNeu().handleAction(null);
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (Throwable t)
    {
			Logger.error("unable to store buchung",t);
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
        if (!getSteuer().isEnabled())
          return;
        
        Double d = (Double) getSteuer().getValue();
        if (d == null)
          return;
        double satz = d.doubleValue();
          
        if (satz == 0.0d)
          return;

        Math math = new Math();
        Double betrag = (Double) getBetrag().getValue();
        double brutto = betrag == null ? getBuchung().getBruttoBetrag() : betrag.doubleValue();
        double netto  = math.netto(brutto,satz);
        double steuer = math.steuer(brutto,satz);
        String curr = Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung();
        getBetrag().setComment(i18n.tr("{0} [Netto: {1} {0}]", new String[]{curr,Fibu.DECIMALFORMAT.format(netto)}));
        getSteuer().setComment(i18n.tr("% [Betrag: {0} {1}]", new String[]{Fibu.DECIMALFORMAT.format(steuer),curr}));
      }
      catch (RemoteException e)
      {
        Logger.error("unable to determine steuer",e);
        GUI.getView().setErrorText(i18n.tr("Fehler beim Ermitten des Steuersatzes für das Konto"));
      }
    }
    
  }

  /**
   * Listener, der fuer beide Konto-Auswahlfelder taugt und jeweils nach Auswahl
   * des Kontos den Kommentar, Saldo und Text erzeugt.
   * @author willuhn
   */
  private class KontoListener implements Listener
  {
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      // Texte und Kommentare ergaenzen
      try {

        Konto sk = (Konto) getSollKontoAuswahl().getValue();
        Konto hk = (Konto) getHabenKontoAuswahl().getValue();
        
        Kontoart ska = sk == null ? null : sk.getKontoArt();
        Kontoart hka = hk == null ? null : hk.getKontoArt();
        
        // Text-Vervollstaendigung
        // BUGZILLA 122
        try
        {
          String text = (String) getText().getValue();
          if (text == null || text.length() == 0)
          {
            String t = null;
            if (ska != null && ska.getKontoArt() != Kontoart.KONTOART_GELD)
              t = sk.getName();
            else if (hka != null && hka.getKontoArt() != Kontoart.KONTOART_GELD)
              t = hk.getName();
            if (t != null)
            getText().setValue(t);
          }
        }
        catch (Exception e2)
        {
          Logger.error("unable to autocomplete text",e2);
        }

        // Steuerkonto checken
        try
        {
          Steuer ss = sk == null ? null : sk.getSteuer();
          Steuer hs = hk == null ? null : hk.getSteuer();
          Steuer s = (ss != null ? ss : hs);
          if (s == null)
          {
            // keines der Konten hat einen Steuersatz
            getSteuer().disable();
          }
          else
          {
            double satz = s.getSatz();
            if (satz == 0.0d)
            {
              getSteuer().disable();
            }
            else
            {
              getSteuer().enable();
              getSteuer().setValue(new Double(satz));
              new SteuerListener().handleEvent(null);
              GUI.getView().setSuccessText(i18n.tr("Steuersatz wurde auf {0}% geändert", Fibu.DECIMALFORMAT.format(satz)));
            }
          }
        }
        catch (Exception e)
        {
          Logger.error("unable to determine steuer",e);
          GUI.getView().setErrorText(i18n.tr("Fehler beim Ermitten des Steuersatzes für das Konto"));
        }
        
        // Anlagevermoegen (nur Soll-Konto)
        try
        {
          if (sk != null && ska != null && getBuchung().isNewObject() && ska.getKontoArt() == Kontoart.KONTOART_ANLAGE)
          {
            getAnlageVermoegen().enable();
            getAnlageVermoegen().setValue(Boolean.TRUE);
          }
          else
          {
            getAnlageVermoegen().setValue(Boolean.FALSE);
            getAnlageVermoegen().disable();
          }
        }
        catch (RemoteException e)
        {
          Logger.error("unable to determine anlagekonto",e);
          GUI.getView().setErrorText(i18n.tr("Fehler beim Prüfen auf Anlagevermögen"));
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to execute kontolistener",e);
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
      try
      {
        Text t = (Text) event.widget;
        if (t == null)
          return;

        // Parsen
        Date d = parse(t.getText());

        // Wochentag ergaenzen
        Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());
        cal.setTime(d);
        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (i < 0 || i >= Fibu.WEEKDAYS.length)
          return;

        String s = Fibu.DATEFORMAT.format(d);
        getDatum().setText(s);
        getDatum().setValue(s);
        getDatum().setComment(i18n.tr(Fibu.WEEKDAYS[i]));
      }
      catch (Exception e)
      {
        GUI.getView().setErrorText(i18n.tr("Datumsformat ungültig"));
        Logger.error("unable to update week day",e);
      }
		}
	}

  /**
   * Parst ein Datum.
   * @param datum
   * @return das Datum.
   * @throws ParseException
   * @throws RemoteException
   */
  private Date parse(String datum) throws ParseException, RemoteException
  {
    // BUGZILLA 122
    DateFormat df = null;
    switch (datum.length())
    {
      case 10:
        df = Fibu.DATEFORMAT;
        break;
      case 8:
        df = Fibu.FASTDATEFORMAT;
        break;
      case 6:
        df = Fibu.BUCHUNGDATEFORMAT;
        break;
      case 4:
        Calendar cal = Calendar.getInstance();
        cal.setTime(Settings.getActiveGeschaeftsjahr().getBeginn());
        datum += cal.get(Calendar.YEAR);
        df = Fibu.FASTDATEFORMAT;
        break;
      default:
        throw new ParseException("unknown date format: " + datum,0);
    }

    // Parsen
    return df.parse(datum);
    
  }
}

/*********************************************************************
 * $Log: BuchungControl.java,v $
 * Revision 1.64  2006/01/04 17:05:32  willuhn
 * @B bug 170
 *
 * Revision 1.63  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.62  2006/01/02 16:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.61  2006/01/02 15:51:12  willuhn
 * @B NPE
 *
 * Revision 1.60  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.59  2005/10/13 15:44:33  willuhn
 * @B bug 139
 *
 * Revision 1.58  2005/10/06 22:27:16  willuhn
 * @N KontoInput
 *
 * Revision 1.57  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 * Revision 1.56  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.55  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.54  2005/10/03 14:22:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.53  2005/09/30 17:12:06  willuhn
 * @B bug 122
 *
 * Revision 1.52  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.51  2005/09/25 22:18:22  willuhn
 * @B bug 122
 *
 * Revision 1.50  2005/09/24 13:00:13  willuhn
 * @B bugfixes according to bugzilla
 *
 * Revision 1.49  2005/09/05 13:47:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.48  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.47  2005/09/02 13:27:35  willuhn
 * @C transaction behavior
 *
 * Revision 1.46  2005/09/02 11:26:54  willuhn
 * *** empty log message ***
 *
 * Revision 1.45  2005/09/02 11:26:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.44  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.43  2005/09/01 21:18:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.42  2005/08/29 22:52:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.41  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.40  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.39  2005/08/29 15:20:51  willuhn
 * @B bugfixing
 *
 * Revision 1.38  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.37  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.36  2005/08/29 00:20:29  willuhn
 * @N anlagevermoegen
 *
 * Revision 1.35  2005/08/25 21:58:57  willuhn
 * @N SKR04
 *
 * Revision 1.34  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.33  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.32  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.31  2005/08/22 13:31:03  willuhn
 * *** empty log message ***
 *
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