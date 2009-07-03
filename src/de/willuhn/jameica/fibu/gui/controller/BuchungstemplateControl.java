/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungstemplateControl.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/07/03 10:52:18 $
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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
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
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer Buchungs-Vorlagen.
 */
public class BuchungstemplateControl extends AbstractControl
{
	
  // Fachobjekte
	private Buchungstemplate buchung 		= null;

	// Eingabe-Felder
  private Input bezeichnung      = null;
	private Input	text					   = null;
	private Input betrag				   = null;
  private DecimalInput steuer				    = null;
  private KontoInput sollKontoAuswahl   = null;
  private KontoInput habenKontoAuswahl  = null;
  
  private Input mandant          = null;
  private Input kontenrahmen     = null;
  
  private I18N i18n;

  /**
   * @param view
   */
  public BuchungstemplateControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

	/**
	 * Liefert die Buchung.
   * @return die Buchung.
   * @throws RemoteException
   */
  public Buchungstemplate getBuchung() throws RemoteException
	{
		if (buchung != null)
			return buchung;
		
		buchung = (Buchungstemplate) getCurrentObject();
		if (buchung != null)
			return buchung;
		
		buchung = (Buchungstemplate) Settings.getDBService().createObject(Buchungstemplate.class,null);
    
    // Die beiden Parameter geben wir automatisch vor.
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    buchung.setMandant(jahr.getMandant());
    buchung.setKontenrahmen(jahr.getKontenrahmen());
		return buchung;
		
	}

  /**
   * Liefert ein Anzeigefeld fuer den Mandanten.
   * @return Mandant.
   * @throws RemoteException
   */
  public Input getMandant() throws RemoteException
  {
    if (this.mandant != null)
      return this.mandant;
    Mandant m = getBuchung().getMandant();
    if (m == null)
      m = Settings.getActiveGeschaeftsjahr().getMandant();
    this.mandant = new LabelInput(m.getFirma());
    this.mandant.setComment(i18n.tr("Steuernummer: {0}",m.getSteuernummer()));
    return this.mandant;
  }

  /**
   * Liefert ein Anzeigefeld fuer den Kontenrahmen.
   * @return Mandant.
   * @throws RemoteException
   */
  public Input getKontenrahmen() throws RemoteException
  {
    if (this.kontenrahmen != null)
      return this.kontenrahmen;
    Kontenrahmen m = getBuchung().getKontenrahmen();
    if (m == null)
      m = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
    this.kontenrahmen = new LabelInput(m.getName());
    return this.kontenrahmen;
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
    return habenKontoAuswahl;
	}

  /**
   * Liefert das Eingabe-Feld fuer die Bezeichnung der Vorlage.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBezeichnung() throws RemoteException
  {
    if (bezeichnung != null)
      return bezeichnung;
    
    bezeichnung = new TextInput(getBuchung().getName());
    return bezeichnung;
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
	 * Liefert das Eingabe-Feld fuer den Betrag.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBetrag() throws RemoteException
	{
		if (betrag != null)
			return betrag;
		
		betrag = new DecimalInput(getBuchung().getBetrag(), Fibu.DECIMALFORMAT);
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
      GUI.getStatusBar().setSuccessText(i18n.tr("Buchungsvorlage gespeichert"));
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (Throwable t)
    {
			Logger.error("unable to store buchungstemplate",t);
      GUI.getView().setErrorText("Fehler beim Speichern der Buchungsvorlage.");
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
              getSteuer().setComment(i18n.tr("% [{0}]",s.getName()));
              GUI.getView().setSuccessText(i18n.tr("Steuersatz wurde auf {0}% geändert", Fibu.DECIMALFORMAT.format(satz)));
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

/*********************************************************************
 * $Log: BuchungstemplateControl.java,v $
 * Revision 1.4  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.2  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/