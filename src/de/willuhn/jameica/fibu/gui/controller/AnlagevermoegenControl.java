/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AnlagevermoegenControl.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/29 21:37:02 $
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
import java.util.Date;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.dialogs.KontoAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.CalendarDialog;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer das Anlagevermoegen.
 */
public class AnlagevermoegenControl extends AbstractControl
{

  private Anlagevermoegen vermoegen = null;
  
  private I18N i18n = null;
  
  private Input mandant     = null;
  private Input name        = null;
  private Input kosten      = null;
  private Input laufzeit    = null;
  private Input restwert    = null;
  
  private DialogInput konto       = null;
  private DialogInput afaKonto    = null;
  private DialogInput datum       = null;
  
  /**
   * @param view
   */
  public AnlagevermoegenControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * Liefert das Anlagevermoegen.
   * @return Anlagevermoegen.
   * @throws RemoteException
   */
  public Anlagevermoegen getAnlagevermoegen() throws RemoteException
  {
    if (this.vermoegen != null)
      return this.vermoegen;
    
    this.vermoegen = (Anlagevermoegen) getCurrentObject();
    if (this.vermoegen != null)
      return this.vermoegen;
    
    this.vermoegen = (Anlagevermoegen) Settings.getDBService().createObject(Anlagevermoegen.class,null);
    this.vermoegen.setMandant(Settings.getActiveGeschaeftsjahr().getMandant());
    return this.vermoegen;
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
    Mandant m = getAnlagevermoegen().getMandant();
    this.mandant = new LabelInput(m.getFirma());
    this.mandant.setComment(i18n.tr("Steuernummer: {0}",m.getSteuernummer()));
    return this.mandant;
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer die Bezeichnung.
   * @return Bezeichnung.
   * @throws RemoteException
   */
  public Input getName() throws RemoteException
  {
    if (this.name != null)
      return this.name;
    this.name = new TextInput(getAnlagevermoegen().getName(),255);
    return this.name;
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer die Anschaffungskosten.
   * @return Anschaffungskosten.
   * @throws RemoteException
   */
  public Input getKosten() throws RemoteException
  {
    if (this.kosten != null)
      return this.kosten;
    Mandant m = getAnlagevermoegen().getMandant();
    this.kosten = new DecimalInput(getAnlagevermoegen().getAnschaffungskosten(),Fibu.DECIMALFORMAT);
    this.kosten.setComment(m.getWaehrung());
    if (!getAnlagevermoegen().canChange())
    {
      this.kosten.disable();
      GUI.getView().setErrorText(i18n.tr("Es liegen bereits Abschreibungen vor"));
    }
    return this.kosten;
  }
  
  /**
   * Liefert ein Anzeige-Feld fuer den Restwert.
   * @return Restwert.
   * @throws RemoteException
   */
  public Input getRestwert() throws RemoteException
  {
    if (this.restwert != null)
      return this.restwert;
    Mandant m = getAnlagevermoegen().getMandant();
    this.restwert = new LabelInput(Fibu.DECIMALFORMAT.format(getAnlagevermoegen().getRestwert()));
    this.restwert.setComment(m.getWaehrung());
    return this.restwert;
  }

  /**
   * Liefert ein Auswahlfeld fuer das Anschaffungsdatum.
   * @return Anschaffungsdatum.
   * @throws RemoteException
   */
  public Input getDatum() throws RemoteException
  {
    if (this.datum != null)
      return this.datum;
    
    Date date = getAnlagevermoegen().getAnschaffungsDatum();
    if (date == null)
      date = new Date();
    CalendarDialog d = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    d.setTitle(i18n.tr("Anschaffungsdatum"));
    d.setText(i18n.tr("Bitte wählen Sie das Anschaffungsdatum"));
    d.setDate(date);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        datum.setValue(event.data);
        datum.setText(Fibu.DATEFORMAT.format((Date)event.data));
      }
    });
    datum = new DialogInput(Fibu.DATEFORMAT.format(date),d);
    datum.setValue(date);
    datum.disableClientControl();
    if (!getAnlagevermoegen().canChange())
    {
      this.datum.disableButton();
      GUI.getView().setErrorText(i18n.tr("Es liegen bereits Abschreibungen vor"));
    }
    return datum;
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer die Laufzeit.
   * @return Laufzeit.
   * @throws RemoteException
   */
  public Input getLaufzeit() throws RemoteException
  {
    if (this.laufzeit != null)
      return this.laufzeit;
    this.laufzeit = new IntegerInput(getAnlagevermoegen().getLaufzeit());
    if (!getAnlagevermoegen().canChange())
    {
      this.laufzeit.disable();
      GUI.getView().setErrorText(i18n.tr("Es liegen bereits Abschreibungen vor"));
    }
    return this.laufzeit;
  }
  
  /**
   * Liefert das Eingabe-Feld zur Auswahl des Kontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DialogInput getKonto() throws RemoteException
  {
    if (konto != null)
      return konto;
    
    final String waehrung = Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung();
    DBIterator list = Settings.getDBService().createList(Konto.class);
    list.addFilter("kontoart_id = " + Kontoart.KONTOART_ANLAGE);
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        Konto k = (Konto) event.data;
        if (k == null)
          return;
        try {
          konto.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), waehrung, k.getName()}));
          konto.setValue(k.getKontonummer());
          konto.setText(k.getKontonummer());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load konto",e);
        }

      }
    });
    
    Konto k = getAnlagevermoegen().getKonto();
    konto = new DialogInput(k == null ? null : k.getKontonummer(),d);
    konto.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), waehrung, k.getName()}));
    return konto;
  }

  /**
   * Liefert das Eingabe-Feld zur Auswahl des Abschreibungskontos.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public DialogInput getAbschreibungsKonto() throws RemoteException
  {
    if (afaKonto != null)
      return afaKonto;
    
    final String waehrung = Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung();
    DBIterator list = Settings.getDBService().createList(Konto.class);
    list.addFilter("kontoart_id = " + Kontoart.KONTOART_AUSGABE);
    KontoAuswahlDialog d = new KontoAuswahlDialog(list,KontoAuswahlDialog.POSITION_MOUSE);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event) {
        Konto k = (Konto) event.data;
        if (k == null)
          return;
        try {
          afaKonto.setComment(i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), waehrung, k.getName()}));
          afaKonto.setValue(k.getKontonummer());
          afaKonto.setText(k.getKontonummer());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load konto",e);
        }

      }
    });
    
    Konto k = getAnlagevermoegen().getAbschreibungskonto();
    afaKonto = new DialogInput(k == null ? null : k.getKontonummer(),d);
    afaKonto.setComment(k == null ? "" : i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Fibu.DECIMALFORMAT.format(k.getSaldo()), waehrung, k.getName()}));
    return konto;
  }

  /**
   * Speichert das Anlagevermoegen.
   */
  public void handleStore()
  {
    try
    {
      Anlagevermoegen a = getAnlagevermoegen();
      a.setName((String) getName().getValue());
      if (a.canChange())
      {
        //////////////////////////////////////////////////////////////////////////
        // Konto checken
        String s = (String) getKonto().getText();
        if (s == null || s.length() == 0)
        {
          GUI.getView().setErrorText(i18n.tr("Bitten geben Sie ein Bestandskonto ein."));
          return;
        }
        DBIterator konten = Settings.getDBService().createList(Konto.class);
        konten.addFilter("kontonummer = '" + s + "'");
        if (!konten.hasNext())
        {
          GUI.getView().setErrorText(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));
          return;
        }
        getAnlagevermoegen().setKonto((Konto) konten.next());
        //
        //////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////
        // Abschreibungskonto checken
        s = (String) getAbschreibungsKonto().getText();
        if (s == null || s.length() == 0)
        {
          GUI.getView().setErrorText(i18n.tr("Bitten geben Sie ein Konto für die Abschreibungen ein."));
          return;
        }
        konten = Settings.getDBService().createList(Konto.class);
        konten.addFilter("kontonummer = '" + s + "'");
        if (!konten.hasNext())
        {
          GUI.getView().setErrorText(i18n.tr("Das Konto \"{0}\" existiert nicht.",s));
          return;
        }
        getAnlagevermoegen().setAbschreibungskonto((Konto) konten.next());
        //
        //////////////////////////////////////////////////////////////////////////

        a.setAnschaffungsDatum((Date) getDatum().getValue());
        a.setAnschaffungskosten(((Double)getKosten().getValue()).doubleValue());
        a.setLaufzeit(((Integer)getLaufzeit().getValue()).intValue());
      }
      a.store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Anlage-Gegenstand gespeichert"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while saving av",e);
      GUI.getView().setErrorText(i18n.tr("Fehler beim Speichern des Anlage-Gegenstandes"));
    }
    catch (ApplicationException ae)
    {
      GUI.getView().setErrorText(ae.getMessage());
    }
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenControl.java,v $
 * Revision 1.4  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 15:20:51  willuhn
 * @B bugfixing
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/