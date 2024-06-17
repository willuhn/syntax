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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer Buchungs-Vorlagen.
 */
public class BuchungstemplateControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

	private Buchungstemplate buchung 		= null;

  private Input bezeichnung             = null;
	private Input	text					          = null;
	private Input betrag				          = null;
  private DecimalInput steuer				    = null;
  private KontoInput sollKontoAuswahl   = null;
  private KontoInput habenKontoAuswahl  = null;
  
  /**
   * @param view
   */
  public BuchungstemplateControl(AbstractView view)
  {
    super(view);
  }

	/**
	 * Liefert die Buchung.
   * @return die Buchung.
   * @throws RemoteException
   */
  public Buchungstemplate getBuchung() throws RemoteException
	{
		if (this.buchung != null)
			return this.buchung;
		
		Object object = this.getCurrentObject();
		
		if (object instanceof Buchungstemplate)
		{
	    this.buchung = (Buchungstemplate) object;
	    return this.buchung;
		}

    this.buchung = (Buchungstemplate) Settings.getDBService().createObject(Buchungstemplate.class,null);
    
		if (object instanceof Mandant)
		{
		  Mandant m = (Mandant) object;
	    this.buchung.setMandant(m);
	    
      // Als Kontenrahmen nehmen wir den des letzten Geschaeftsjahres
      DBIterator i = m.getGeschaeftsjahre();
      i.setOrder("order by beginn desc");
      if (i.hasNext())
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) i.next();
        this.buchung.setKontenrahmen(jahr.getKontenrahmen());
      }
		}
		else
		{
	    // Wenn kein Mandant angegeben ist, nehmen wir den des aktuellen Geschaeftsjahres.
	    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
	    this.buchung.setMandant(jahr.getMandant());
	    this.buchung.setKontenrahmen(jahr.getKontenrahmen());
		}
		return this.buchung;
		
	}

  /**
	 * Liefert das Eingabe-Feld zur Auswahl des SollKontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public KontoInput getSollKontoAuswahl() throws RemoteException
	{
		if (this.sollKontoAuswahl != null)
			return this.sollKontoAuswahl;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    this.sollKontoAuswahl = new KontoInput(jahr.getKontenrahmen().getKonten(), getBuchung().getSollKonto());
    this.sollKontoAuswahl.addListener(new KontoListener());
    this.sollKontoAuswahl.setName(i18n.tr("Soll-Konto"));
    return this.sollKontoAuswahl;
  }


	/**
	 * Liefert das Eingabe-Feld zur Auswahl des Haben-Kontos.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public KontoInput getHabenKontoAuswahl() throws RemoteException
	{
    if (this.habenKontoAuswahl != null)
      return this.habenKontoAuswahl;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    this.habenKontoAuswahl = new KontoInput(jahr.getKontenrahmen().getKonten(), getBuchung().getHabenKonto());
    this.habenKontoAuswahl.addListener(new KontoListener());
    this.habenKontoAuswahl.setName(i18n.tr("Haben-Konto"));
    return this.habenKontoAuswahl;
	}

  /**
   * Liefert das Eingabe-Feld fuer die Bezeichnung der Vorlage.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBezeichnung() throws RemoteException
  {
    if (this.bezeichnung != null)
      return this.bezeichnung;
    
    this.bezeichnung = new TextInput(getBuchung().getName());
    this.bezeichnung.setName(i18n.tr("Bezeichnung der Vorlage"));
    this.bezeichnung.setMandatory(true);
    return this.bezeichnung;
  }

  /**
	 * Liefert das Eingabe-Feld fuer den Buchungstext.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getText() throws RemoteException
	{
		if (this.text != null)
			return this.text;
		
		this.text = new TextInput(getBuchung().getText());
		this.text.setName(i18n.tr("Text"));
		return this.text;
	}

	/**
	 * Liefert das Eingabe-Feld fuer den Betrag.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBetrag() throws RemoteException
	{
		if (this.betrag != null)
			return this.betrag;
		
		this.betrag = new DecimalInput(getBuchung().getBetrag(), Settings.DECIMALFORMAT);
		this.betrag.setComment(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung());
    this.betrag.addListener(new SteuerListener());
    this.betrag.setName(i18n.tr("Brutto-Betrag"));
		return this.betrag;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Steuer.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DecimalInput getSteuer() throws RemoteException
	{
		if (this.steuer != null)
			return this.steuer;

		this.steuer = new DecimalInput(getBuchung().getSteuer(),Settings.DECIMALFORMAT);
		this.steuer.setComment("%");
		this.steuer.setName(i18n.tr("Steuersatz"));
    SteuerListener sl = new SteuerListener();
    this.steuer.addListener(sl);
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
      //////////////////////////////////////////////////////////////////////////
      // Steuer checken
      Double d = (Double) getSteuer().getValue();
      getBuchung().setSteuer(d == null ? 0.0d : d.doubleValue());
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Betrag checken
      d = (Double) getBetrag().getValue();
      getBuchung().setBetrag(d == null ? 0.0d : d.doubleValue());
      //
      //////////////////////////////////////////////////////////////////////////

      getBuchung().setSollKonto((Konto) getSollKontoAuswahl().getValue());
      getBuchung().setHabenKonto((Konto) getHabenKontoAuswahl().getValue());
      getBuchung().setText((String)getText().getValue());
      getBuchung().setName((String)getBezeichnung().getValue());
      
      // und jetzt speichern wir.
			getBuchung().store();
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Buchungsvorlage gespeichert"),StatusBarMessage.TYPE_SUCCESS));
    }
    catch (ApplicationException ae)
    {
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
    }
    catch (Exception e)
    {
      if (!(e instanceof ApplicationException))
			  Logger.error("unable to store buchungstemplate",e);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Speichern der Buchungsvorlage."),StatusBarMessage.TYPE_ERROR));
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
        double brutto = betrag == null ? getBuchung().getBetrag() : betrag.doubleValue();
        double netto  = math.netto(brutto,satz);
        double steuer = math.steuer(brutto,satz);
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
              getSteuer().setComment(i18n.tr("% [{0}]",s.getName()));
              GUI.getView().setSuccessText(i18n.tr("Steuersatz wurde auf {0}% geändert", Settings.DECIMALFORMAT.format(satz)));
            }
          }
        }
        catch (Exception e)
        {
          Logger.error("unable to determine steuer",e);
          GUI.getView().setErrorText(i18n.tr("Fehler beim Ermitten des Steuersatzes für das Konto"));
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to execute kontolistener",e);
      }

    }
  }
}
