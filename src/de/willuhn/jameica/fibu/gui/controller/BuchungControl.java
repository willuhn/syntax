/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.54 $
 * $Date: 2005/10/03 14:22:11 $
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
import de.willuhn.jameica.fibu.gui.dialogs.KontoAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
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
	private Input	text					   = null;
	private Input belegnummer		   = null;
	private Input betrag				   = null;

  private DecimalInput steuer				    = null;
  private DialogInput datum             = null;
  private DialogInput sollKontoAuswahl  = null;
  private DialogInput habenKontoAuswahl = null;
  
  private CheckboxInput anlageVermoegen = null;
  private Input laufzeit                = null;
  private DialogInput afaKonto          = null;
  
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
    return datum;
  
  }

	/**
	 * Liefert das Eingabe-Feld zur Auswahl des SollKontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DialogInput getSollKontoAuswahl() throws RemoteException
	{
		if (sollKontoAuswahl != null)
			return sollKontoAuswahl;
		
    DBIterator list = Settings.getDBService().createList(Konto.class);
    list.addFilter("kontenrahmen_id = " + Settings.getActiveGeschaeftsjahr().getKontenrahmen().getID());
    list.setOrder("order by kontonummer");
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);

    Konto k = getBuchung().getSollKonto();

    sollKontoAuswahl      = new DialogInput(k == null ? null : k.getKontonummer(),d);
    KontoListener kl      = new KontoListener(sollKontoAuswahl,true);
    SollKontoListener skl = new SollKontoListener();

    sollKontoAuswahl.addListener(kl);
    sollKontoAuswahl.addListener(skl);
    sollKontoAuswahl.setComment("");

    d.addCloseListener(kl);
    d.addCloseListener(skl);
    
    // Einmal ausloesen, damit das Ding sofort beim Oeffnen aktiv wird.
    kl.handleEvent(new Event());

    return sollKontoAuswahl;

  }


	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Haben-Kontos.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public DialogInput getHabenKontoAuswahl() throws RemoteException
	{
		if (habenKontoAuswahl != null)
			return habenKontoAuswahl;
		
    DBIterator list = Settings.getDBService().createList(Konto.class);
    list.addFilter("kontenrahmen_id = " + Settings.getActiveGeschaeftsjahr().getKontenrahmen().getID());
    list.setOrder("order by kontonummer");
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);

    Konto k = getBuchung().getHabenKonto();

    habenKontoAuswahl     = new DialogInput(k == null ? null : k.getKontonummer(),d);
    KontoListener kl      = new KontoListener(habenKontoAuswahl,false);

    habenKontoAuswahl.addListener(kl);
    habenKontoAuswahl.setComment("");

    d.addCloseListener(kl);
    
    // Einmal ausloesen, damit das Ding sofort beim Oeffnen aktiv wird.
    kl.handleEvent(new Event());

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
		return text;
	}

  /**
   * Liefert eine Checkbox, mit der ausgewaehlt werden kann, ob zu der Buchung
   * gleich ein Datensatz im Anlagevermoegen angelegt werden soll.
   * @return Checkbox.
   */
  public CheckboxInput getAnlageVermoegen()
  {
    if (this.anlageVermoegen != null)
      return this.anlageVermoegen;
    this.anlageVermoegen = new CheckboxInput(false);
    this.anlageVermoegen.disable();
    this.anlageVermoegen.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        try
        {
          if (anlageVermoegen.isEnabled())
          {
            getLaufzeit().enable();
            getAbschreibungsKonto().enable();
          }
          else
          {
            getLaufzeit().disable();
            getAbschreibungsKonto().disable();
          }
        }
        catch (RemoteException e)
        {
          Logger.error("error while enabling/disabling afa controls",e);
        }
      }
    });
    return this.anlageVermoegen;
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer die Laufzeit.
   * @return Eingabe-Feld.
   */
  public Input getLaufzeit()
  {
    if (this.laufzeit != null)
      return this.laufzeit;
    this.laufzeit = new IntegerInput(1);
    this.laufzeit.setComment(i18n.tr("Nutzungsdauer des Anlagegutes in Jahren"));
    this.laufzeit.disable();
    return this.laufzeit;
  }
  
  /**
   * Liefert das Eingabe-Feld zur Auswahl des Abschreibungskontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DialogInput getAbschreibungsKonto() throws RemoteException
  {
    if (afaKonto != null)
      return afaKonto;
    
    final Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    final String waehrung = jahr.getMandant().getWaehrung();
    DBIterator list = Settings.getDBService().createList(Konto.class);
    list.addFilter("kontoart_id = " + Kontoart.KONTOART_AUFWAND);
    list.addFilter("kontenrahmen_id = " + jahr.getKontenrahmen().getID());
    list.setOrder("order by kontonummer");
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        Konto k = (Konto) event.data;
        if (k == null)
          return;
        try {
          afaKonto.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo(jahr)), waehrung, k.getName()}));
          afaKonto.setValue(k.getKontonummer());
          afaKonto.setText(k.getKontonummer());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load konto",e);
        }

      }
    });
    
    afaKonto = new DialogInput(null,d);
    afaKonto.setComment("");
    afaKonto.disable();
    return afaKonto;
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
		
		betrag = new DecimalInput(getBuchung().getBruttoBetrag(), Fibu.DECIMALFORMAT);
		betrag.setComment(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung());
    betrag.addListener(new SteuerListener());
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
		return steuer;
	}

  /**
   * Speichert die Buchung.
   * @param startNew legt fest, ob danach sofort der Dialog zum Erfassen einer neuen Buchung geoeffnet werden soll.
   */
  public void handleStore(boolean startNew)
  {
    try {

      getBuchung().transactionBegin();
      
      Kontenrahmen kr = Settings.getActiveGeschaeftsjahr().getKontenrahmen();

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
      
      //////////////////////////////////////////////////////////////////////////
      // Soll-Konto checken
      
      String s = (String) getSollKontoAuswahl().getText();
      if (s == null || s.length() == 0)
        throw new ApplicationException(i18n.tr("Bitten geben Sie ein Soll-Konto ein."));

      DBIterator konten = kr.getKonten();
      konten.addFilter("kontonummer = '" + s + "'");
      if (!konten.hasNext())
        throw new ApplicationException(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));

      getBuchung().setSollKonto((Konto) konten.next());
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Haben-Konto checken
      
      s = (String) getHabenKontoAuswahl().getText();
      if (s == null || s.length() == 0)
        throw new ApplicationException(i18n.tr("Bitten geben Sie ein Haben-Konto ein."));

      konten = kr.getKonten();
      konten.addFilter("kontonummer = '" + s + "'");
      if (!konten.hasNext())
        throw new ApplicationException(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));

      Konto k = (Konto) konten.next();
      getBuchung().setHabenKonto(k);
      //
      //////////////////////////////////////////////////////////////////////////

			getBuchung().setText((String)getText().getValue());
      
      // wir speichern grundsaetzlich den aktiven Mandanten als Inhaber der Buchung
			getBuchung().setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());

      // und jetzt speichern wir.
			getBuchung().store();
      
      if (((Boolean)getAnlageVermoegen().getValue()).booleanValue())
      {
        int laufzeit = ((Integer)getLaufzeit().getValue()).intValue();
        if (laufzeit == 0)
          throw new ApplicationException(i18n.tr("Bitte geben Sie eine Laufzeit für die Abschreibung ein"));
        
        Anlagevermoegen av = (Anlagevermoegen) Settings.getDBService().createObject(Anlagevermoegen.class,null);

        //////////////////////////////////////////////////////////////////////////
        // Abschreibungskonto checken
        s = (String) getAbschreibungsKonto().getText();
        if (s == null || s.length() == 0)
          throw new ApplicationException(i18n.tr("Bitten geben Sie ein Konto für die Abschreibungen ein."));

        konten = kr.getKonten();
        konten.addFilter("kontonummer = '" + s + "'");
        if (!konten.hasNext())
          throw new ApplicationException(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));

        av.setAbschreibungskonto((Konto) konten.next());
        //
        //////////////////////////////////////////////////////////////////////////

        av.setAnschaffungsDatum(getBuchung().getDatum());
        av.setAnschaffungskosten(getBuchung().getBetrag());
        av.setBuchung(getBuchung());
        av.setKonto(getBuchung().getSollKonto());
        av.setName(getBuchung().getText());
        av.setNutzungsdauer(laufzeit);
        av.setMandant(Settings.getActiveGeschaeftsjahr().getMandant());
        av.store();
        
        GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. {0} und Anlagevermögen gespeichert.",""+getBuchung().getBelegnummer()));
      }
      else
      {
        GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. {0} gespeichert.",""+getBuchung().getBelegnummer()));
      }
      getBuchung().transactionCommit();

      if (startNew)
        new de.willuhn.jameica.fibu.gui.action.BuchungNeu().handleAction(null);

    }
    catch (ApplicationException e1)
    {
      try
      {
        getBuchung().transactionRollback();
      }
      catch (RemoteException e2)
      {
        Logger.error("unable to rollback transaction",e2);
      }
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (Throwable t)
    {
      try
      {
        getBuchung().transactionRollback();
      }
      catch (RemoteException e2)
      {
        Logger.error("unable to rollback transaction",e2);
      }
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
   * Listener, der das Feld fuer die Steuer aktualisiert.
   */
  private class SollKontoListener implements Listener
  {

    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      try
      {
        String s = (String) getSollKontoAuswahl().getText();
        if (s == null || s.length() == 0)
        {
          getSteuer().disable();
          return;
        }

        DBIterator konten = Settings.getActiveGeschaeftsjahr().getKontenrahmen().getKonten();
        konten.addFilter("kontonummer = '" + s + "'");
        if (!konten.hasNext())
        {
          GUI.getView().setErrorText(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));
          getSteuer().disable();
          return;
        }
        Konto k = (Konto) konten.next();
        
        
        ////////////////////////////////////////////////////////////////////////
        // AV checken
        Kontoart ka = k.getKontoArt();
        if (ka != null && getBuchung().isNewObject() && ka.getKontoArt() == Kontoart.KONTOART_ANLAGE)
        {
          getAnlageVermoegen().enable();
          getAnlageVermoegen().setValue(Boolean.TRUE);
          getLaufzeit().enable();
          getAbschreibungsKonto().enable();
        }
        else
        {
          getAnlageVermoegen().disable();
          getLaufzeit().disable();
          getAbschreibungsKonto().disable();
        }
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Steuer anpassen
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
        GUI.getView().setSuccessText(i18n.tr("Steuersatz wurde auf {0}% geändert", Fibu.DECIMALFORMAT.format(satz)));
        getSteuer().setValue(new Double(satz));
        ////////////////////////////////////////////////////////////////////////
      }
      catch (RemoteException e)
      {
        Logger.error("unable to determine steuer",e);
        GUI.getView().setErrorText(i18n.tr("Fehler beim Ermitten des Steuersatzes für das Konto"));
      }
    }
    
  }

  /**
   * Listener, der fuer beide Konto-Auswahlfelder taugt.
   * @author willuhn
   */
  private class KontoListener implements Listener
  {
    private DialogInput input = null;
    private boolean soll = true;
    
    /**
     * ct.
     * @param input
     * @param soll
     */
    private KontoListener(DialogInput input, boolean soll)
    {
      this.input = input;
      this.soll = soll;
    }
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      try {
        Konto k = readKonto(event.data);

        if (k == null)
          return;

        Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();

        input.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo(jahr)), jahr.getMandant().getWaehrung(), k.getName()}));
        input.setValue(k.getKontonummer());
        input.setText(k.getKontonummer());
        
        // BUGZILLA 122 (Text-Vervollstaendigung)
        try
        {
          String text = (String) getText().getValue();
          if (text != null && text.length() > 0)
            return;

          Kontoart ka = k.getKontoArt();
          if (ka.getKontoArt() != Kontoart.KONTOART_GELD)
            return;
          
          DialogInput di = getSollKontoAuswahl();
          if (soll)
            di = getHabenKontoAuswahl();
          
          Konto gegen = readKonto(di.getText());
          if (gegen == null)
            return;
          getText().setValue(gegen.getName());
        }
        catch (Exception e2)
        {
          Logger.error("unable to autocomplete text",e2);
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to load konto",e);
      }

    }
    
    private Konto readKonto(Object o) throws RemoteException
    {
      if (o != null && (o instanceof Konto))
        return (Konto) o;

      String s = null;
      if (o != null)
        s = o.toString();
      else
        s = input.getText();
      
      if (s == null || s.length() == 0)
        return null;
     
      DBIterator konten = Settings.getActiveGeschaeftsjahr().getKontenrahmen().getKonten();
      konten.addFilter("kontonummer = '" + s + "'");
      if (!konten.hasNext())
      {
        GUI.getView().setErrorText(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));
        return null;
      }
      return (Konto) konten.next();
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