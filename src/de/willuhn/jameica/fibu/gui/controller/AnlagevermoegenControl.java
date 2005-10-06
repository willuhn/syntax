/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AnlagevermoegenControl.java,v $
 * $Revision: 1.11 $
 * $Date: 2005/10/06 22:27:16 $
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
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
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
  
  private KontoInput konto       = null;
  private KontoInput afaKonto    = null;
  private DialogInput datum      = null;
  
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
    this.restwert = new LabelInput(Fibu.DECIMALFORMAT.format(getAnlagevermoegen().getRestwert(Settings.getActiveGeschaeftsjahr())));
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
    
    Date date = getAnlagevermoegen().getAnschaffungsdatum();
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
    this.laufzeit = new IntegerInput(getAnlagevermoegen().getNutzungsdauer());
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
  public KontoInput getKonto() throws RemoteException
  {
    if (konto != null)
      return konto;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.addFilter("kontoart_id = " + Kontoart.KONTOART_ANLAGE);

    konto = new KontoInput(list,getAnlagevermoegen().getKonto());
    return konto;
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
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.addFilter("kontoart_id = " + Kontoart.KONTOART_AUFWAND);
    list.addFilter("steuer_id is null");

    afaKonto = new KontoInput(list,getAnlagevermoegen().getAbschreibungskonto());
    return afaKonto;
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
        getAnlagevermoegen().setKonto((Konto)getKonto().getValue());
        getAnlagevermoegen().setAbschreibungskonto((Konto)getAbschreibungsKonto().getValue());
        a.setAnschaffungsDatum((Date) getDatum().getValue());
        a.setAnschaffungskosten(((Double)getKosten().getValue()).doubleValue());
        a.setNutzungsdauer(((Integer)getLaufzeit().getValue()).intValue());
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
 * Revision 1.11  2005/10/06 22:27:16  willuhn
 * @N KontoInput
 *
 * Revision 1.10  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 * Revision 1.9  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.7  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.6  2005/09/01 21:18:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
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