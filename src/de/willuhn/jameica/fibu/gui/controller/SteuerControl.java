/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/SteuerControl.java,v $
 * $Revision: 1.21 $
 * $Date: 2005/10/06 17:27:59 $
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
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer den Steuer-Dialog.
 */
public class SteuerControl extends AbstractControl
{

	// Fach-Objekte.
	private Steuer steuer = null;

	// Eingabe-Felder
  private Input mandant       = null;
	private Input name					= null;
	private Input satz    			= null;

  private Input kontoauswahl	= null;
  
  private I18N i18n;
  
  /**
   * @param view
   */
  public SteuerControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * Liefert eine Auswahl des Mandanten.
   * @return Mandant.
   * @throws RemoteException
   */
  public Input getMandant() throws RemoteException
  {
    if (this.mandant != null)
      return this.mandant;
    Mandant m = getSteuer().getMandant();
    if (m == null)
      m = Settings.getActiveGeschaeftsjahr().getMandant();
    this.mandant = new LabelInput(m.getFirma());
    this.mandant.setComment(i18n.tr("Steuernummer: {0}",m.getSteuernummer()));
    return this.mandant;
  }

	/**
	 * Liefert die Steuer.
   * @return Steuer.
   * @throws RemoteException
   */
  public Steuer getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;
			
		steuer = (Steuer) getCurrentObject();
		if (steuer != null)
			return steuer;

		steuer = (Steuer) Settings.getDBService().createObject(Steuer.class,null);
		return steuer;

	}

	/**
	 * Liefert ein Eingabe-Feld fuer den Namen des Steuersatzes.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getName() throws RemoteException
	{
		if (name != null)
			return name;

    if (getSteuer().isInitial())
      name = new LabelInput(getSteuer().getName());
    else
      name = new TextInput(getSteuer().getName());
		return name;
	}

	/**
	 * Liefert ein Eingabe-Feld fuer den Steuersatz.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getSatz() throws RemoteException
	{
		if (satz != null)
			return satz;
    if (getSteuer().isInitial())
      satz = new LabelInput(Fibu.DECIMALFORMAT.format(getSteuer().getSatz()));
    else
      satz = new DecimalInput(getSteuer().getSatz(), Fibu.DECIMALFORMAT);
		satz.setComment(i18n.tr("Angabe in \"%\""));
		return satz;
	}

	/**
	 * Liefert ein Auswahl-Feld fuer das Steuer-Konto.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getKontoAuswahl() throws RemoteException
	{
    // TODO Neues KontoInput
		if (kontoauswahl != null)
			return kontoauswahl;

    final Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    final Kontenrahmen kr = jahr.getKontenrahmen();
    final Mandant mandant = jahr.getMandant();
    Konto k = getSteuer().getSteuerKonto();

    if (getSteuer().isInitial())
    {
      kontoauswahl = new LabelInput(k == null ? null : k.getKontonummer());
    }
    else
    {
      DBIterator list = kr.getKonten();
      list.addFilter("kontoart_id = " + Kontoart.KONTOART_STEUER);
      KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
      d.addCloseListener(new Listener() {
        public void handleEvent(Event event)
        {
          if (event == null || event.data == null)
            return;

          try
          {
            Konto k = (Konto) event.data;
              
            kontoauswahl.setValue(k);
            ((DialogInput)kontoauswahl).setText(k.getKontonummer());
            kontoauswahl.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo(jahr)), mandant.getWaehrung(), k.getName()}));
          }
          catch (RemoteException e)
          {
            Logger.error("unable to load konto",e);
          }
        }
      });
      kontoauswahl = new DialogInput(k == null ? null : (k.getKontonummer() + " [" + k.getName() + "]"),d);
      ((DialogInput) kontoauswahl).disableClientControl();
    }
    kontoauswahl.setValue(k);
    kontoauswahl.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo(jahr)), mandant.getWaehrung(), k.getName()}));
		return kontoauswahl;
	}


  /**
   * Speichert den Steuersatz.
   */
  public void handleStore()
  {
    try {

      if (getSteuer().isInitial())
      {
        GUI.getView().setErrorText(i18n.tr("System-Steuerkonto darf nicht geändert werden."));
        return;
      }

      getSteuer().setName((String)  getName().getValue());
      getSteuer().setSatz(((Double) getSatz().getValue()).doubleValue());

      //////////////////////////////////////////////////////////////////////////
      // Steuerkonto checken
      
      Object o = getKontoAuswahl().getValue();
      if (o == null || !(o instanceof Konto))
      {
        GUI.getView().setErrorText(i18n.tr("Bitte wählen Sie ein Steuerkonto aus."));
        return;
      }
      getSteuer().setSteuerKonto((Konto)o);
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      getSteuer().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Steuersatz gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      GUI.getView().setErrorText("Fehler beim Speichern des Steuersatzes.");
      Logger.error("unable to store steuer",e);
    }
    
  }
}

/*********************************************************************
 * $Log: SteuerControl.java,v $
 * Revision 1.21  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 * Revision 1.20  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.19  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2005/09/01 21:18:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.16  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.15  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.13  2005/08/08 22:54:15  willuhn
 * @N massive refactoring
 *
 * Revision 1.12  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.11  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/29 01:21:51  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/29 01:14:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/