/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/EinstellungenControl.java,v $
 * $Revision: 1.1 $
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
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Controller fuer den Einstellungsdialog.
 * @author willuhn
 */
public class EinstellungenControl extends AbstractControl
{
  private I18N i18n = null;

  private KontoInput afaKonto = null;
  
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
    Mandant m = jahr.getMandant();
    Konto konto = null;
    
    String id = Settings.getSettings().getString("mandant." + m.getID() + ".afakonto",null);
    if (id != null && id.length() > 0)
      konto = (Konto) Settings.getDBService().createObject(Konto.class,id);
    
    DBIterator konten = jahr.getKontenrahmen().getKonten();
    konten.addFilter("kontoart_id = " + Kontoart.KONTOART_AUFWAND);
    konten.addFilter("steuer_id is null");
    afaKonto = new KontoInput(konten,konto);
    return afaKonto;
  }
  
  /**
   * Speichert die Einstellungen.
   */
  public synchronized void handleStore()
  {
    try
    {
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      Mandant m = jahr.getMandant();
      Konto k = (Konto) getAbschreibungsKonto().getValue();
      if (k != null)
      {
        Kontoart ka = k.getKontoArt();
        if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
        {
          GUI.getView().setErrorText(i18n.tr("Konto {0} ist kein gültiges Aufwandskonto",k.getKontonummer()));
          return;
        }
      }
      String id = k == null ? null : k.getID();
      Settings.getSettings().setAttribute("mandant." + m.getID() + ".afakonto",id);
      GUI.getStatusBar().setSuccessText(i18n.tr("Einstellungen gespeichert"));
    }
    catch (RemoteException e)
    {
      Logger.error("unable to store settings",e);
      GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Speichern der Einstellungen"));
    }
  }
}


/*********************************************************************
 * $Log: EinstellungenControl.java,v $
 * Revision 1.1  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 *********************************************************************/