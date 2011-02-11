/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.76 $
 * $Date: 2011/02/11 10:46:11 $
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

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
import de.willuhn.jameica.gui.input.ButtonInput;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
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
  private DateInput datum               = null;
  private KontoInput sollKontoAuswahl   = null;
  private KontoInput habenKontoAuswahl  = null;
  
  private CheckboxInput anlageVermoegen = null;
  private Input anlagevermoegenLink     = null;
  
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
    sollKontoAuswahl.addListener(new KontoListener());
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
    habenKontoAuswahl.addListener(new KontoListener());
    habenKontoAuswahl.setMandatory(true);
    habenKontoAuswahl.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
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
		text.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
    text.setMandatory(true);
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
	 * Liefert das Eingabe-Feld fuer die Steuer.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DecimalInput getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;

		steuer = new DecimalInput(getBuchung().getSteuer(),Settings.DECIMALFORMAT);
		steuer.setComment("%");
    SteuerListener sl = new SteuerListener();
    steuer.addListener(sl);
    sl.handleEvent(null);
    steuer.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
    steuer.setMandatory(true);
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
        throw new ApplicationException(i18n.tr("Belegnummer ungültig."));
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Betraege
      Math math = new Math();
      double steuer = ((Double)getSteuer().getValue()).doubleValue();
      double brutto = ((Double)getBetrag().getValue()).doubleValue();
      double netto  = math.netto(brutto,steuer);
      
      if (Double.isNaN(steuer))
        throw new ApplicationException(i18n.tr("Steuersatz ungültig."));
      if (Double.isNaN(brutto))
        throw new ApplicationException(i18n.tr("Betrag ungültig."));

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
      
      getBuchung().setSollKonto((Konto) getSollKontoAuswahl().getValue());
      getBuchung().setHabenKonto((Konto) getHabenKontoAuswahl().getValue());
      getBuchung().setText((String)getText().getValue());
      
      // und jetzt speichern wir.
			getBuchung().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. {0} gespeichert.",""+getBuchung().getBelegnummer()));

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
        Double betrag = (Double) getBetrag().getValue();
        double brutto = betrag == null ? getBuchung().getBruttoBetrag() : betrag.doubleValue();
        double steuer = 0d;
        double netto  = brutto;

        if (getSteuer().isEnabled())
        {
          Double d = (Double) getSteuer().getValue();
          if (d != null)
          {
            double satz = d.doubleValue();
            
            Math math = new Math();
            netto  = math.netto(brutto,satz);
            steuer = math.steuer(brutto,satz);
          }
        }
        String curr = Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung();
        getBetrag().setComment(i18n.tr("{0} [Netto: {1} {0}]", new String[]{curr,Settings.DECIMALFORMAT.format(netto)}));
        getSteuer().setComment(i18n.tr("% [Betrag: {0} {1}]", new String[]{Settings.DECIMALFORMAT.format(steuer),curr}));
        
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
          getSteuer().setEnabled(s != null);
          if (s != null)
          {
            double satz = s.getSatz();
            getSteuer().enable();
            getSteuer().setValue(new Double(satz));
            new SteuerListener().handleEvent(null);
            GUI.getView().setSuccessText(i18n.tr("Steuersatz wurde auf {0}% geändert", Settings.DECIMALFORMAT.format(satz)));
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
}

/*********************************************************************
 * $Log: BuchungControl.java,v $
 * Revision 1.76  2011/02/11 10:46:11  willuhn
 * @B BUGZILLA 990
 *
 * Revision 1.75  2010-10-22 11:47:30  willuhn
 * @B Keine Doppelberechnung mehr in der Buchungserfassung (brutto->netto->brutto)
 *
 * Revision 1.74  2010-10-13 21:55:31  willuhn
 * @N Text und Betrag nur dann mit den Werten aus der Vorlage ueberschreiben, wenn nicht schon was drin steht
 *
 * Revision 1.73  2010-06-04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.72  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.71  2010/02/08 15:39:48  willuhn
 * @N Option "Geschaeftsjahr abschliessen" in Kontextmenu des Geschaeftsjahres
 * @N Zweispaltiges Layout in Mandant-Details - damit bleibt mehr Platz fuer die Reiter unten drunter
 * @N Anzeige von Pflichtfeldern
 *
 * Revision 1.70  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.69.2.2  2008/10/06 10:38:36  willuhn
 * @C Bei Konten ohne Steuer Netto=Brutto anzeigen
 *
 * Revision 1.69.2.1  2008/07/03 10:37:08  willuhn
 * @N Effektivere Erzeugung neuer Buchungsnummern
 * @B Nach Wechsel des Geschaeftsjahres nicht Dialog "Geschaeftsjahr bearbeiten" oeffnen
 *
 * Revision 1.69  2006/10/10 22:30:07  willuhn
 * @C DialogInput gegen DateInput ersetzt
 *
 * Revision 1.68  2006/10/09 23:48:41  willuhn
 * @B bug 140
 *
 * Revision 1.67  2006/07/17 21:58:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.66  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.65  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
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