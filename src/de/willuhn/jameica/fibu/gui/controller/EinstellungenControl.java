/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/EinstellungenControl.java,v $
 * $Revision: 1.5 $
 * $Date: 2010/06/01 16:37:22 $
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

  private KontoInput afaKonto              = null;
  private KontoInput afaKontoGWG           = null;
  private Input gwgWert                    = null;
  private CheckboxInput systemDataWritable = null;
  
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
    if (this.systemDataWritable == null)
    {
      this.systemDataWritable = new CheckboxInput(Settings.getSystemDataWritable());
      this.systemDataWritable.setName(i18n.tr("Änderungen des System-Kontenrahmen zulassen"));
    }
    return this.systemDataWritable;
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


/*********************************************************************
 * $Log: EinstellungenControl.java,v $
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.4  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.3.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 * Revision 1.3  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.2  2005/10/13 15:44:33  willuhn
 * @B bug 139
 *
 * Revision 1.1  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 *********************************************************************/