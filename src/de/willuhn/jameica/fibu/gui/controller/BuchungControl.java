/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.AnlagevermoegenNeu;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.fibu.server.SaldenCache;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.ButtonInput;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer Buchungen.
 */
public class BuchungControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(BuchungControl.class);

  // Fachobjekte
	private Buchung buchung 	            = null;

	// Eingabe-Felder
  private Input template                = null;
	private TextAreaInput	text		        = null;
	private TextAreaInput kommentar       = null;
	private Input belegnummer		          = null;
	private Input betrag				          = null;

  private SelectInput steuer				    = null;
  private DateInput datum               = null;
  private KontoInput sollKontoAuswahl   = null;
  private KontoInput habenKontoAuswahl  = null;
  
  private CheckboxInput anlageVermoegen = null;
  private Input anlagevermoegenLink     = null;
  
  private SteuerListener steuerListener = new SteuerListener();
  
  private AbstractView view = null;
  
  private Input splitbuchung     = null;

  /**
   * @param view
   */
  public BuchungControl(AbstractView view)
  {
    super(view);
    this.view = view;
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
    
    list.addFilter("(mandant_id is null or mandant_id = " + Settings.getActiveGeschaeftsjahr().getMandant().getID() + ")");
    list.addFilter("(kontenrahmen_id is null or kontenrahmen_id = " + Settings.getActiveGeschaeftsjahr().getKontenrahmen().getID() + ")");
    list.setOrder("order by name");

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
            
            // Wir ueberschreiben den Text und Betrag nur, wenn nicht schon was andres drin steht
            String text = (String) getText().getValue();
            if (text == null || text.length() == 0)
              getText().setValue(t.getText());
            
            Double betrag = (Double) getBetrag().getValue();
            if (betrag == null || betrag.doubleValue() == 0.0d)
              getBetrag().setValue(Double.valueOf(t.getBetrag()));
            
            getSteuer().setValue(t.getSteuerObject());
            
            // Das Setzen des Focus geht zwar. Aber danach laesst sich die
            // Combo-Box nicht mehr bedienen. Scheint ein SWT-Bug zu sein.
            // getBetrag().focus();
            new KontoListener(null).handleEvent(null);
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
  public DateInput getDatum() throws RemoteException
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
          d = Settings.DATEFORMAT.parse(s);
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

    datum = new DateInput(d,Settings.CUSTOM_DATEFORMAT);
    datum.setTitle(i18n.tr("Datum"));
    datum.setText(i18n.tr("Bitte wählen Sie das Datum für diese Buchung"));
    datum.setMandatory(true);
    datum.setComment("");

    
    // Mit dem Listener ergaenzen wir den Wochentag als Kommentar
    datum.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        Date d = (Date) datum.getValue();

        if (d == null)
          return;

        // Wochentag ergaenzen
        Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());
        cal.setTime(d);
        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (i < 0 || i >= Settings.WEEKDAYS.length)
          return;

        datum.setComment(i18n.tr(Settings.WEEKDAYS[i]));
      }
    
    });
    datum.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
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
    sollKontoAuswahl.addListener(new KontoListener(sollKontoAuswahl));
    sollKontoAuswahl.setMandatory(true);
    sollKontoAuswahl.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
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
    habenKontoAuswahl.addListener(new KontoListener(habenKontoAuswahl));
    habenKontoAuswahl.setMandatory(true);
    habenKontoAuswahl.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
    return habenKontoAuswahl;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Buchungstext.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public TextAreaInput getText() throws RemoteException
	{
		if (text != null)
			return text;
		
		text = new TextAreaInput(getBuchung().getText());
		text.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
    text.setMandatory(true);
    text.setHeight(80);
		return text;
	}

  /**
   * Liefert das Eingabe-Feld fuer den Kommentar.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public TextAreaInput getKommentar() throws RemoteException
  {
    if (kommentar != null)
      return kommentar;
    
    kommentar = new TextAreaInput(getBuchung().getKommentar(),1000);
    kommentar.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
    kommentar.setHeight(80);
    return kommentar;
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
    return this.anlageVermoegen;
  }
  
  /**
   * Liefert ein Label mit der Bezeichnung des ggf zugehoerigen Anlagegutes samt Link zum Oeffnen.
   * @return Label mit AV.
   */
  public Input getAnlageVermoegenLink()
  {
    if (this.anlagevermoegenLink != null)
      return this.anlagevermoegenLink;
    
    this.anlagevermoegenLink = new AVLink();
    return this.anlagevermoegenLink;
  }
  
  /**
   * Liefert ein Label ob es eine Splitbuchung ist
   * @return Label mit Splitbuchung.
   */
  public Input getSplitbuchung()
  {
    if (this.splitbuchung != null)
      return this.splitbuchung;
    
    this.splitbuchung = new LabelInput("");
    return this.splitbuchung;
  }
  /**
   * Hilfsklasse.
   */
  private class AVLink extends ButtonInput
  {
    private AVLink()
    {
      addButtonListener(new Listener()
      {
        public void handleEvent(Event event)
        {
          try
          {
            Anlagevermoegen av = getBuchung().getAnlagevermoegen();
            if (av == null)
              return;
            new AnlagevermoegenNeu().handleAction(av);
          }
          catch (Exception e)
          {
            Logger.error("unable to load av",e);
          }
        }
      });
    }

    /**
     * @see de.willuhn.jameica.gui.input.ButtonInput#getClientControl(org.eclipse.swt.widgets.Composite)
     */
    public Control getClientControl(Composite parent)
    {
      Label label = GUI.getStyleFactory().createLabel(parent,SWT.NONE);
      try
      {
        Anlagevermoegen av = getBuchung().getAnlagevermoegen();
        if (av != null)
          label.setText(av.getName());
        else
          label.setText(i18n.tr("kein Anlage-Gegenstand verfügbar"));
      }
      catch (Exception e)
      {
        Logger.error("unable to display av",e);
      }
      return label;
    }

    /**
     * @see de.willuhn.jameica.gui.input.Input#getValue()
     */
    public Object getValue()
    {
      return null;
    }

    /**
     * @see de.willuhn.jameica.gui.input.Input#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
    }
    
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
		belegnummer.setMandatory(true);
		belegnummer.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
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
		
		betrag = new DecimalInput(getBuchung().getBruttoBetrag(), Settings.DECIMALFORMAT);
		betrag.setComment(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung());
		betrag.setMandatory(true);
    betrag.addListener(new SteuerListener());
    betrag.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
		return betrag;
	}

	/**
	 * Liefert das Auswahl-Feld fuer die Steuer.
   * @return Auswahl-Feld.
   * @throws RemoteException
   */
  public SelectInput getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;

    final DBIterator list = Settings.getDBService().createList(Steuer.class);
    list.addFilter("mandant_id is null or mandant_id = ?",Settings.getActiveGeschaeftsjahr().getMandant().getID());
    list.setOrder("ORDER BY satz,name");
    
    final Kontenrahmen kr = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
    final List<Steuer> found = new ArrayList<Steuer>();
    while (list.hasNext())
    {
      Steuer s = (Steuer) list.next();
      Konto k = s.getSteuerKonto();
      if (k == null)
        continue;
      
      if (BeanUtil.equals(k.getKontenrahmen(),kr))
        found.add(s);
    }
    steuer = new SelectInput(found,getBuchung().getSteuerObject());
    steuer.setEnabled(getBuchung().getGeschaeftsjahr().isClosed());
    steuer.setPleaseChoose("<" + i18n.tr("Keine Steuer") + ">");
     
    final SteuerListener sl = new SteuerListener();
    steuer.addListener(sl);
    
    // Einmal initial auslösen
    sl.handleEvent(null);

		return steuer;
	}
  
  /**
   * Tauscht Soll- und Haben-Konto.
   * @throws ApplicationException
   */
  public void handleFlipAccounts() throws ApplicationException
  {
    try
    {
      final Konto soll = this.getSollKontoAuswahl().getKonto();
      final Konto haben = this.getHabenKontoAuswahl().getKonto();
      
      this.getSollKontoAuswahl().setValue(haben);
      this.getHabenKontoAuswahl().setValue(soll);
    }
    catch (Exception e)
    {
      Logger.error("unable to flip accounts",e);
      throw new ApplicationException(i18n.tr("Tauschen von Soll/Haben fehlgeschlagen: {0}",e.getMessage()));
    }
  }

  /**
   * Speichert die Buchung.
   * @param startNew legt fest, ob danach sofort der Dialog zum Erfassen einer neuen Buchung geoeffnet werden soll.
   * @return true, wenn das Speichern erfolgreich war.
   * @throws ApplicationException
   */
  public boolean handleStore(boolean startNew) throws ApplicationException
  {
    try {

      //////////////////////////////////////////////////////////////////////////
      // Belegnummer checken
      try {
        getBuchung().setBelegnummer(((Integer) getBelegnummer().getValue()).intValue());
      }
      catch (Exception e)
      {
        throw new ApplicationException(i18n.tr("Belegnummer ungültig."));
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Betraege
      Math math = new Math();
      Steuer steuerObj = (Steuer)getSteuer().getValue();
      double steuer = steuerObj == null ? 0.0d : steuerObj.getSatz();
      double brutto = ((Double)getBetrag().getValue()).doubleValue();
      double netto  = math.netto(brutto,steuer);
      
      if (Double.isNaN(steuer))
        throw new ApplicationException(i18n.tr("Steuersatz ungültig."));
      if (Double.isNaN(brutto))
        throw new ApplicationException(i18n.tr("Betrag ungültig."));

  	  getBuchung().setSteuerObject(steuerObj);
  	  getBuchung().setSteuer(steuer);
      getBuchung().setBruttoBetrag(brutto);
      getBuchung().setBetrag(netto);
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Datum checken
      
      Date d = (Date) getDatum().getValue();
      if (d == null)
      {
        Logger.warn("no date given, using actual date");
        d = new Date();
      }
      getBuchung().setDatum(d);
      settings.setAttribute("buchung.date.last." + Settings.getActiveGeschaeftsjahr().getID(),Settings.DATEFORMAT.format(d));
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Fuer den Fall, dass der User die Konten geaendert hat, muss auch der Saldo-Cache der
      // vorherigen Konten aktualisiert werden. Siehe https://homebanking-hilfe.de/forum/topic.php?p=159869#real159869
      final Konto ks = getBuchung().getSollKonto();
      if (ks != null)
        SaldenCache.remove(Settings.getActiveGeschaeftsjahr(),ks.getKontonummer());
      final Konto kh = getBuchung().getHabenKonto();
      if (kh != null)
        SaldenCache.remove(Settings.getActiveGeschaeftsjahr(),kh.getKontonummer());
      //
      //////////////////////////////////////////////////////////////////////////

      getBuchung().setSollKonto((Konto) getSollKontoAuswahl().getValue());
      getBuchung().setHabenKonto((Konto) getHabenKontoAuswahl().getValue());
      getBuchung().setText((String)getText().getValue());
      getBuchung().setKommentar((String)getKommentar().getValue());
      
      // und jetzt speichern wir.
			getBuchung().store();
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Buchung Nr. {0} gespeichert.",Integer.toString(getBuchung().getBelegnummer())),StatusBarMessage.TYPE_SUCCESS));

      // Damit wird die Attachment-Referenz nach dem Neu-Anlegen der Buchung aktualisiert
      this.view.setCurrentObject(getBuchung());

      // BUGZILLA 245
      
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
        av.setBuchung(getBuchung());
        new AnlagevermoegenNeu().handleAction(av);
      }
      else if (startNew)
        new de.willuhn.jameica.fibu.gui.action.BuchungNeu().handleAction(null);
      
      return true;
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
			Logger.error("unable to store booking",e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Speichern der Buchung fehlgeschlagen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
    }
    return false;
    
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
        Steuer s = (Steuer) getSteuer().getValue();
        
        try
        {
          // Steuer-Auswahl deaktivieren, wenn keines der Konten ein Steuerkonto vorsieht
          Konto sk = (Konto) getSollKontoAuswahl().getValue();
          Konto hk = (Konto) getHabenKontoAuswahl().getValue();
          Steuer ss = sk == null ? null : sk.getSteuer();
          Steuer hs = hk == null ? null : hk.getSteuer();
          getSteuer().setEnabled((ss != null || hs != null) && !getBuchung().getGeschaeftsjahr().isClosed());
        }
        catch (Exception e)
        {
          Logger.error("unable to determine steuer",e);
          GUI.getView().setErrorText(i18n.tr("Fehler beim Ermitten des Steuersatzes für das Konto"));
        }

        Double betrag = (Double) getBetrag().getValue();
        double brutto = betrag == null ? getBuchung().getBruttoBetrag() : betrag.doubleValue();
        double steuer = 0d;
        double netto  = brutto;

        // Steuerbetrag neu berechnen basierend auf der aktuellen Auswahl
        if (s != null)
        {
          double satz = s.getSatz();
          
          Math math = new Math();
          netto  = math.netto(brutto,satz);
          steuer = math.steuer(brutto,satz);
        }
        String curr = Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung();
        getBetrag().setComment(i18n.tr("{0} [Netto: {1} {0}]", new String[]{curr,Settings.DECIMALFORMAT.format(netto)}));
        getSteuer().setComment(i18n.tr("[Betrag: {0} {1}]", new String[]{Settings.DECIMALFORMAT.format(steuer),curr}));
        
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
    private KontoInput input = null;
    
    /**
     * ct.
     * @param input
     */
    private KontoListener(KontoInput input)
    {
      this.input = input;
    }
    
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      if (this.input != null && !this.input.hasChanged())
        return;
      
      // Texte und Kommentare ergaenzen
      try
      {

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
            if (sk != null && ska != null && ska.getKontoArt() != Kontoart.KONTOART_GELD)
              t = sk.getName();
            else if (hk != null && hka != null && hka.getKontoArt() != Kontoart.KONTOART_GELD)
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
          
          getSteuer().setValue(s);
          steuerListener.handleEvent(null);
          
          double satz = 0;
          if (s != null)
        	  satz = s.getSatz();
          
          GUI.getView().setSuccessText(i18n.tr("Steuersatz wurde auf {0}% geändert", Settings.DECIMALFORMAT.format(satz)));
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
}

