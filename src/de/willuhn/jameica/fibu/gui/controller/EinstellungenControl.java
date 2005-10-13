/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/EinstellungenControl.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/10/13 15:44:33 $
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
 * Controller fuer den Einstellungsdialog.
 * @author willuhn
 */
public class EinstellungenControl extends AbstractControl
{
  private I18N i18n = null;

  private KontoInput afaKonto = null;
  private Input gwgWert       = null;
  
  /**
   * ct.
   * @param view
   */
  public EinstellungenControl(AbstractView view) {
    super(view);
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }
  
  /**
   * Liefert das Eingabe-Feld zur Auswahl des Abschreibungskontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public KontoInput getAbschreibungsKonto() throws RemoteException
  {
    if (afaKonto != null)
      return afaKonto;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();

    DBIterator konten = jahr.getKontenrahmen().getKonten();
    konten.addFilter("kontoart_id = " + Kontoart.KONTOART_AUFWAND);
    konten.addFilter("steuer_id is null");
    afaKonto = new KontoInput(konten,Settings.getAbschreibunsgKonto(jahr));
    return afaKonto;
  }
  
  /**
   * Liefert ein Eingabe-Feld zur Definition des GWG-Wertes.
   * @return GWG-Wert.
   * @throws RemoteException
   */
  public Input getGwgWert() throws RemoteException
  {
    if (gwgWert != null)
      return this.gwgWert;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    Mandant m = jahr.getMandant();

    gwgWert = new DecimalInput(Settings.getGwgWert(jahr),Fibu.DECIMALFORMAT);
    gwgWert.setComment(m.getWaehrung() + " [" + i18n.tr("Geringwertige Wirtschaftsgüter") + "]");
    return gwgWert;
  }
  
  /**
   * Speichert die Einstellungen.
   */
  public synchronized void handleStore()
  {
    try
    {
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      Settings.setAbschreibungsKonto(jahr,(Konto)getAbschreibungsKonto().getValue());
      
      Double d = (Double) getGwgWert().getValue();
      Settings.setGwgWert(jahr,d == null ? 0 : d.doubleValue());

      GUI.getStatusBar().setSuccessText(i18n.tr("Einstellungen gespeichert"));
    }
    catch (RemoteException e)
    {
      Logger.error("unable to store settings",e);
      GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Speichern der Einstellungen"));
    }
    catch (ApplicationException ae)
    {
      GUI.getStatusBar().setErrorText(ae.getMessage());
    }
  }
}


/*********************************************************************
 * $Log: EinstellungenControl.java,v $
 * Revision 1.2  2005/10/13 15:44:33  willuhn
 * @B bug 139
 *
 * Revision 1.1  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 *********************************************************************/