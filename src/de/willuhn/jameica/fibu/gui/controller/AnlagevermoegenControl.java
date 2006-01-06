/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AnlagevermoegenControl.java,v $
 * $Revision: 1.17 $
 * $Date: 2006/01/06 15:17:08 $
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
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
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
  private Input hinweis     = null;
  
  private KontoInput konto       = null;
  private KontoInput afaKonto    = null;
  private DialogInput datum      = null;
  
  private de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(AnfangsbestandControl.class);

  
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
    if (!getAnlagevermoegen().canChange())
      this.name.disable();
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
    this.kosten.setComment(i18n.tr("{0}, GWG-Grenze: {1} {0}",new String[]{m.getWaehrung(),Fibu.DECIMALFORMAT.format(Settings.getGwgWert(null))}));
    if (!getAnlagevermoegen().canChange())
      this.kosten.disable();
    return this.kosten;
  }
  
  /**
   * Liefert einen Hinweistext.
   * @return Hinweistext.
   * @throws RemoteException
   */
  public Input getHinweis() throws RemoteException
  {
    if (this.hinweis != null)
      return this.hinweis;
    this.hinweis = new LabelInput("");
    ((LabelInput)this.hinweis).setColor(Color.ERROR);
    if (!getAnlagevermoegen().canChange())
      this.hinweis.setValue(i18n.tr("Es existieren bereits Abschreibungen. Anlage kann nicht mehr geändert werden"));
    return this.hinweis;
  }
  
  /**
   * Liefert ein Feld fuer den Restwert.
   * @return Restwert.
   * @throws RemoteException
   */
  public Input getRestwert() throws RemoteException
  {
    if (this.restwert != null)
      return this.restwert;
    Mandant m = getAnlagevermoegen().getMandant();
    this.restwert = new DecimalInput(getAnlagevermoegen().getRestwert(Settings.getActiveGeschaeftsjahr()),Fibu.DECIMALFORMAT);
    this.restwert.setComment(m.getWaehrung());
    if (getAnlagevermoegen().canChange())
      new RestwertListener().handleEvent(null);
    else
      this.restwert.disable();
    
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
        new RestwertListener().handleEvent(null);
      }
    });
    datum = new DialogInput(Fibu.DATEFORMAT.format(date),d);
    datum.setValue(date);
    datum.disableClientControl();
    if (!getAnlagevermoegen().canChange())
      this.datum.disable();
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
    int n = getAnlagevermoegen().getNutzungsdauer();
    this.laufzeit = new IntegerInput(n == 0 ? 1 : n);
    if (!getAnlagevermoegen().canChange())
      this.laufzeit.disable();
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

    Konto k = getAnlagevermoegen().getKonto();
    
    String last = settings.getString("konto.last",null);
    if (k == null && getAnlagevermoegen().isNewObject() && last != null && last.length() > 0)
    {
      try
      {
        k = (Konto) Settings.getDBService().createObject(Konto.class,last);
      }
      catch (Exception e) {/*ignore*/}
    }

    konto = new KontoInput(list,k);
    if (!getAnlagevermoegen().canChange())
      this.konto.disable();
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

    Konto k = getAnlagevermoegen().getAbschreibungskonto();
    if (k == null)
    {
      Double d = (Double) getKosten().getValue();
      k = Settings.getAbschreibunsgKonto(jahr,d != null && d.doubleValue() < Settings.getGwgWert(jahr));
    }
    afaKonto = new KontoInput(list,k);
    if (!getAnlagevermoegen().canChange())
      this.afaKonto.disable();
    return afaKonto;
  }

  /**
   * Speichert das Anlagevermoegen.
   */
  public void handleStore()
  {
    try
    {
      Konto k = (Konto)getKonto().getValue();

      Konto abschreibung = (Konto)getAbschreibungsKonto().getValue();

      getAnlagevermoegen().setName((String) getName().getValue());
      getAnlagevermoegen().setKonto(k);
      getAnlagevermoegen().setAnschaffungsDatum((Date) getDatum().getValue());
      
      double ak = 0.0d;
      try
      {
        ak = ((Double) getKosten().getValue()).doubleValue();
        getAnlagevermoegen().setAnschaffungskosten(ak);
      }
      catch (Exception e)
      {
        Logger.error("unable to set kosten",e);
        throw new ApplicationException(i18n.tr("Anschaffungskosten ungültig."));
      }

      int nutzungsdauer = 1;
      try {
        nutzungsdauer = ((Integer) getLaufzeit().getValue()).intValue();
      }
      catch (Exception e)
      {
        Logger.error("unable to set laufzeit",e);
        throw new ApplicationException(i18n.tr("Nutzungsdauer ungültig."));
      }
      
      if (ak <= Settings.getGwgWert(Settings.getActiveGeschaeftsjahr()))
      {
        Konto ka = Settings.getAbschreibunsgKonto(Settings.getActiveGeschaeftsjahr(),true);

        YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
        d.setTitle(i18n.tr("Geringwertiges Wirtschaftsgut"));

        String text = i18n.tr("Anlage kann als GWG sofort abgeschrieben werden.\n" +
                              "Nutzungsdauer zur Sofort-Abschreibung auf 1 Jahr verkürzen?");
        
        if (ka != null)
          text += "\n" + i18n.tr("Ausserdem können die Abschreibungen auf dem Konto\n" +
              "\"" + ka.getName() + "\" gebucht werden.");
        d.setText(text);
        try
        {
          if (((Boolean)d.open()).booleanValue())
          {
            getLaufzeit().setValue(new Integer(1));
            nutzungsdauer = 1;
 
            if (ka != null)
            {
              getAbschreibungsKonto().setValue(ka);
              abschreibung = ka;
            }
          }
        }
        catch (OperationCanceledException oce)
        {
          // ignore
        }
        catch (Exception e)
        {
          Logger.error("error while checking for gwg",e);
          GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Prüfen auf GWG"));
        }
      }
      getAnlagevermoegen().setAbschreibungskonto(abschreibung);
      getAnlagevermoegen().setNutzungsdauer(nutzungsdauer);

      if (getRestwert().isEnabled())
      {
        try {
          getAnlagevermoegen().setRestwert(((Double) getRestwert().getValue()).doubleValue());
        }
        catch (Exception e)
        {
          Logger.error("unable to set restwert",e);
          throw new ApplicationException(i18n.tr("Restwert ungültig."));
        }
      }

      getAnlagevermoegen().store();
      settings.setAttribute("konto.last",k.getID());
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
  
  /**
   * Prueft, ob der Restwert eingegeben werden kann.
   * @author willuhn
   */
  private class RestwertListener implements Listener
  {

    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      try
      {
        Date d = (Date) getDatum().getValue();
        if (d == null)
          return;
        
        Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
        Date start = jahr.getBeginn();
        if (d.before(start) && getAnlagevermoegen().canChange())
          getRestwert().enable();
        else
          getRestwert().disable();
        
      }
      catch (RemoteException e)
      {
        Logger.error("error while checking restwert",e);
        GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Prüfen des Restwertes"));
      }
    }
    
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenControl.java,v $
 * Revision 1.17  2006/01/06 15:17:08  willuhn
 * @C Abschreibungskonto
 *
 * Revision 1.16  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.15  2006/01/04 00:53:48  willuhn
 * @B bug 166 Ausserplanmaessige Abschreibungen
 *
 * Revision 1.14  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.13  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.12  2006/01/02 23:50:58  willuhn
 * *** empty log message ***
 *
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