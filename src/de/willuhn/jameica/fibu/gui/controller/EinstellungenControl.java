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
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.messaging.StatusBarMessage;
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

  private KontoInput afaKonto                = null;
  private KontoInput afaKontoGWG             = null;
  private Input gwgWert                      = null;
  private CheckboxInput systemDataWritable   = null;
  private CheckboxInput syncCheckmarks       = null;
  private CheckboxInput useExistingGjOnClose = null;
  
  /**
   * ct.
   * @param view
   */
  public EinstellungenControl(AbstractView view)
  {
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
    afaKonto = new KontoInput(konten,Settings.getAbschreibungsKonto(jahr,false));
    afaKonto.setName(i18n.tr("Vorgabe Abschreibungskonto"));
    return afaKonto;
  }
  
  /**
   * Liefert das Eingabe-Feld zur Auswahl des Abschreibungskontos fuer GWGs.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public KontoInput getAbschreibungsKontoGWG() throws RemoteException
  {
    if (afaKontoGWG != null)
      return afaKontoGWG;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();

    DBIterator konten = jahr.getKontenrahmen().getKonten();
    konten.addFilter("kontoart_id = " + Kontoart.KONTOART_AUFWAND);
    konten.addFilter("steuer_id is null");
    afaKontoGWG = new KontoInput(konten,Settings.getAbschreibungsKonto(jahr,true));
    afaKontoGWG.setName(i18n.tr("Vorgabe Abschreibungskonto für GWG"));
    return afaKontoGWG;
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

    gwgWert = new DecimalInput(Settings.getGwgWert(jahr),Settings.DECIMALFORMAT);
    gwgWert.setName(i18n.tr("Nettogrenze GWG"));
    gwgWert.setComment(m.getWaehrung() + " [" + i18n.tr("Geringwertige Wirtschaftsgüter") + "]");
    return gwgWert;
  }
  
  /**
   * Liefert eine Checkbox, mit der das Aendern des Systemkontenrahmens aktiviert werden kann.
   * @return Checkbox.
   */
  public CheckboxInput getSystemDataWritable()
  {
    if (this.systemDataWritable != null)
      return this.systemDataWritable;
    
    this.systemDataWritable = new CheckboxInput(Settings.getSystemDataWritable());
    this.systemDataWritable.setName(i18n.tr("Änderungen des System-Kontenrahmen zulassen"));
    return this.systemDataWritable;
  }
  
  /**
   * Liefert eine Checkbox, mit der eingestellt werden kann, ob beim Schliessen eines
   * Geschaeftsjahres alternativ zur automatischen Erstellung des Folgejahres auch ein
   * eventuell bereits vorhandenes verwendet werden kann.
   * @return Checkbox.
   */
  public CheckboxInput getUseExistingGjOnClose()
  {
    if (this.useExistingGjOnClose != null)
      return this.useExistingGjOnClose;
    
    this.useExistingGjOnClose = new CheckboxInput(Settings.getUseExistingGjOnClose());
    this.useExistingGjOnClose.setName(i18n.tr("Beim Schließen des Geschäftsjahres ein evtl. vorhandenes Folgejahr wiederverwenden"));
    return this.useExistingGjOnClose;
  }

  /**
   * Liefert eine Checkbox, mit der die Synchronisierung der Geprueft-Markierung aktiviert werden kann.
   * @return Checkbox.
   */
  public CheckboxInput getSyncCheckmarks()
  {
    if (this.syncCheckmarks != null)
      return this.syncCheckmarks;
    
    this.syncCheckmarks = new CheckboxInput(Settings.getSyncCheckmarks());
    this.syncCheckmarks.setName(i18n.tr("Geprüft-Markierung mit Buchungen aus Hibiscus-Umsätzen synchronisieren"));
    return this.syncCheckmarks;
  }

  /**
   * Speichert die Einstellungen.
   */
  public synchronized void handleStore()
  {
    try
    {
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      
      Konto k = (Konto) getAbschreibungsKonto().getValue();
      if (k != null)
      {
        Kontoart ka = k.getKontoArt();
        if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
          throw new ApplicationException(i18n.tr("Das ausgewählte Abschreibungskonto ist kein Aufwandskonto"));
      }
      Settings.setAbschreibungsKonto(jahr,k,false);

      k = (Konto) getAbschreibungsKontoGWG().getValue();
      if (k != null)
      {
        Kontoart ka = k.getKontoArt();
        if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
          throw new ApplicationException(i18n.tr("Das ausgewählte Abschreibungskonto für GWG ist kein Aufwandskonto"));
      }
      Settings.setAbschreibungsKonto(jahr,k,true);
      
      Double d = (Double) getGwgWert().getValue();
      Settings.setGwgWert(jahr,d == null ? 0 : d.doubleValue());
      
      boolean sysChange = ((Boolean)getSystemDataWritable().getValue()).booleanValue();
      if (sysChange)
      {
        try
        {
          String msg = i18n.tr("Änderungen des Systemkontenrahmens wirken sich auf allen Mandanten aus.\n" +
                               "Ein Zurücksetzen des Systemkontenrahmens auf die \"Werkseinstellungen\" ist nicht möglich.\n" +
                               "Aktivieren Sie diese Funktion nur, wenn Sie genau wissen, was Sie tun.\n\n" +
                               "Möchten Sie Änderungen des Systemkontenrahmen tatsächlich zulassen?");
          sysChange = Application.getCallback().askUser(msg);
        }
        catch (Exception e)
        {
          Logger.error("unable to ask user",e);
        }
      }
      getSystemDataWritable().setValue(Boolean.valueOf(sysChange));
      Settings.setSystemDataWritable(sysChange);
      
      Settings.setSyncCheckmarks(((Boolean) getSyncCheckmarks().getValue()).booleanValue());
      Settings.setUseExistingGjOnClose(((Boolean) getUseExistingGjOnClose().getValue()).booleanValue());

      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Einstellungen gespeichert"),StatusBarMessage.TYPE_SUCCESS));
    }
    catch (Exception e)
    {
      String text = e.getMessage();
      if (!(e instanceof ApplicationException))
      {
        text = i18n.tr("Fehler beim Speichern der Einstellungen");
        Logger.error("unable to store settings",e);
      }

      Application.getMessagingFactory().sendMessage(new StatusBarMessage(text,StatusBarMessage.TYPE_ERROR));
    }
  }
}
