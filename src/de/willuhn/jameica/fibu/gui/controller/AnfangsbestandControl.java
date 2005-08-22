/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AnfangsbestandControl.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/22 21:44:09 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.dialogs.KontoAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer den Anfangsbestand eines Kontos.
 */
public class AnfangsbestandControl extends AbstractControl
{

	// Fach-Objekte
	private Anfangsbestand ab = null;

	// Eingabe-Felder
	private DialogInput konto	  = null;
  private Input betrag        = null;
  private Input mandant       = null;

  private I18N i18n;

  /**
   * @param view
   */
  public AnfangsbestandControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * Liefert den Anfangsbestand.
   * @return der Anfangsbestand.
   * @throws RemoteException
   */
  public Anfangsbestand getAnfangsbestand() throws RemoteException
  {
    if (ab != null)
      return ab;

    ab = (Anfangsbestand) getCurrentObject();
    if (ab != null)
      return ab;
      
    ab = (Anfangsbestand) Settings.getDBService().createObject(Anfangsbestand.class,null);
    ab.setMandant(Settings.getActiveMandant());
    return ab;
  }

  /**
   * Liefert ein Eingabe-Feld fuer die Kontonummer.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DialogInput getKontoAuswahl() throws RemoteException
  {
    if (konto != null)
      return konto;
    
    DBIterator list = Settings.getDBService().createList(Konto.class);
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        Konto k = (Konto) event.data;
        if (k == null)
          return;
        try {
          konto.setValue(k.getKontonummer());
          konto.setText(k.getKontonummer());
          konto.setComment(k.getName());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load konto",e);
        }
      }
    });
    Konto k = getAnfangsbestand().getKonto();
    konto = new DialogInput(k == null ? null : k.getKontonummer(),d);
    konto.setComment(k == null ? "" : k.getName());
    return konto;
  }

  /**
   * Liefert ein Eingabe-Feld fuer den Betrag.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBetrag() throws RemoteException
  {
    if (betrag != null)
      return betrag;
    betrag = new DecimalInput(getAnfangsbestand().getBetrag(), Fibu.DECIMALFORMAT);
    Mandant m = getAnfangsbestand().getMandant();
    if (m == null)
      m = Settings.getActiveMandant();
    betrag.setComment(m.getWaehrung());
    return betrag;
  }
  
  /**
   * Liefert ein Auswahl-Feld fuer den Mandanten.
   * @return Auswahl-Feld.
   * @throws RemoteException
   */
  public Input getMandant() throws RemoteException
  {
    if (mandant != null)
      return mandant;
    
    Mandant m = getAnfangsbestand().getMandant();
    if (m == null)
      m = Settings.getActiveMandant();
    mandant = new LabelInput(m.getFirma() + " [" + m.getAttribute("geschaeftsjahr") + "]");
    return mandant;
  }
  
  /**
   * Speichert den Anfangsbestand.
   */
  public void handleStore()
  {
    try {

      getAnfangsbestand().setBetrag(((Double)getBetrag().getValue()).doubleValue());
      getAnfangsbestand().setMandant(Settings.getActiveMandant());

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
      getAnfangsbestand().setKonto((Konto) konten.next());

      
      // und jetzt speichern wir.
      getAnfangsbestand().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Anfangsbestand gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Logger.error("unable to store anfangsbestand",e);
      GUI.getView().setErrorText("Fehler beim Speichern des Anfangsbestandes.");
    }
    
  }
}

/*********************************************************************
 * $Log: AnfangsbestandControl.java,v $
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/