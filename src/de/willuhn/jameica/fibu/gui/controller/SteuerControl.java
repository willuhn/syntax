/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/SteuerControl.java,v $
 * $Revision: 1.14 $
 * $Date: 2005/08/09 23:53:34 $
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
import de.willuhn.jameica.fibu.gui.dialogs.KontoAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
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
	private Input name					= null;
	private Input satz    			= null;

  private DialogInput kontoauswahl	= null;
  
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
		satz = new DecimalInput(getSteuer().getSatz(), Fibu.DECIMALFORMAT);
		satz.setComment(i18n.tr("Angabe in \"%\""));
		return satz;
	}

	/**
	 * Liefert ein Auswahl-Feld fuer das Steuer-Konto.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public DialogInput getKontoAuswahl() throws RemoteException
	{
		if (kontoauswahl != null)
			return kontoauswahl;

    KontoAuswahlDialog d = new KontoAuswahlDialog(Settings.getDBService().createList(SteuerKonto.class),KontoAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        kontoauswahl.setValue(event.data);
      }
    });
    SteuerKonto k = getSteuer().getSteuerKonto();
		kontoauswahl = new DialogInput(k == null ? null : (k.getKontonummer() + " [" + k.getName() + "]"),d);
    kontoauswahl.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1}",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), Settings.getActiveMandant().getWaehrung()}));
    kontoauswahl.disableClientControl();
		return kontoauswahl;
	}


  /**
   * Speichert den Steuersatz.
   */
  public void handleStore()
  {
    try {

      getSteuer().setName((String)  getName().getValue());
      getSteuer().setSatz(((Double) getSatz().getValue()).doubleValue());

      //////////////////////////////////////////////////////////////////////////
      // Steuerkonto checken
      
      Object o = getKontoAuswahl().getValue();
      if (o == null || !(o instanceof SteuerKonto))
      {
        GUI.getView().setErrorText(i18n.tr("Bitte wählen Sie ein Steuerkonto aus."));
        return;
      }
      getSteuer().setSteuerKonto((SteuerKonto)o);
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      getSteuer().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Steuersatz gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.getStatusBar().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      GUI.getStatusBar().setErrorText("Fehler beim Speichern des Steuersatzes.");
      Logger.error("unable to store steuer",e);
    }
    
  }
}

/*********************************************************************
 * $Log: SteuerControl.java,v $
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