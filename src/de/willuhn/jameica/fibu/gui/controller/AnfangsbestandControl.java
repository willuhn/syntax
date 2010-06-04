/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AnfangsbestandControl.java,v $
 * $Revision: 1.12 $
 * $Date: 2010/06/04 00:33:56 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer den Anfangsbestand eines Kontos.
 */
public class AnfangsbestandControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

	// Fach-Objekte
	private Anfangsbestand ab = null;

	// Eingabe-Felder
	private KontoInput konto	    = null;
  private Input betrag          = null;

  /**
   * @param view
   */
  public AnfangsbestandControl(AbstractView view)
  {
    super(view);
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
    ab.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
    return ab;
  }

  /**
   * Liefert ein Eingabe-Feld fuer die Kontonummer.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public KontoInput getKontoAuswahl() throws RemoteException
  {
    if (konto != null)
      return konto;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator konten = jahr.getKontenrahmen().getKonten();
    konten.addFilter("(kontoart_id = " + Kontoart.KONTOART_ANLAGE + " or " +
                     " kontoart_id = " + Kontoart.KONTOART_GELD + ")");
    
    konto = new KontoInput(konten,getAnfangsbestand().getKonto());
    konto.setMandatory(true);
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
    betrag = new DecimalInput(getAnfangsbestand().getBetrag(), Settings.DECIMALFORMAT);

    Mandant m = null;
    Geschaeftsjahr jahr = getAnfangsbestand().getGeschaeftsjahr();
    if (jahr != null)
      m = jahr.getMandant();
    
    if (m == null)
      m = Settings.getActiveGeschaeftsjahr().getMandant();
    betrag.setComment(m.getWaehrung());
    betrag.setMandatory(true);
    return betrag;
  }
  
  /**
   * Speichert den Anfangsbestand.
   */
  public void handleStore()
  {
    try {

      try {
        getAnfangsbestand().setBetrag(((Double) getBetrag().getValue()).doubleValue());
      }
      catch (Exception e)
      {
        Logger.error("unable to set betrag",e);
        throw new ApplicationException(i18n.tr("Betrag ungültig."));
      }

      getAnfangsbestand().setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
      getAnfangsbestand().setKonto((Konto)getKontoAuswahl().getValue());
      
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
 * Revision 1.12  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.11  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.10  2007/02/27 18:17:32  willuhn
 * @B Anfangsbestaende nur von Anlage- und Geldkonten erzeugen
 *
 * Revision 1.9  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.8  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.7  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.6  2005/10/06 22:27:17  willuhn
 * @N KontoInput
 *
 * Revision 1.5  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 * Revision 1.4  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.3  2005/09/01 21:18:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.1  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/