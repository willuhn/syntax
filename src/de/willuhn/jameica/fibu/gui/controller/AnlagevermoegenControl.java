/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AnlagevermoegenControl.java,v $
 * $Revision: 1.26 $
 * $Date: 2012/01/29 22:09:14 $
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.gui.input.ButtonInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.messaging.StatusBarMessage;
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
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(AnfangsbestandControl.class);

  private Anlagevermoegen vermoegen = null;
  
  private Input name        = null;
  private Input kosten      = null;
  private Input laufzeit    = null;
  private Input restwert    = null;
  private Input hinweis     = null;
  
  private KontoInput konto        = null;
  private KontoInput afaKonto     = null;
  private DateInput datum       = null;
  
  private ButtonInput buchungLink = null;
  
  private SelectInput status     = null;
  

  
  /**
   * @param view
   */
  public AnlagevermoegenControl(AbstractView view)
  {
    super(view);
    
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
   * Liefert ein Eingabe-Feld fuer die Bezeichnung.
   * @return Bezeichnung.
   * @throws RemoteException
   */
  public Input getName() throws RemoteException
  {
    if (this.name != null)
      return this.name;
    this.name = new TextInput(getAnlagevermoegen().getName(),255);
    this.name.setEnabled(getAnlagevermoegen().canChange());
    this.name.setMandatory(true);
    return this.name;
  }
  
  /**
   * Liefert eine Auswahl fuer den Status des Anlagegutes.
   * @return Auswahl fuer den Status des Anlagegutes.
   * @throws RemoteException
   */
  public SelectInput getStatus() throws RemoteException
  {
    if (this.status != null)
      return this.status;
    
    List<Status> list = new ArrayList<Status>();
    list.add(new Status(Anlagevermoegen.STATUS_BESTAND));
    list.add(new Status(Anlagevermoegen.STATUS_VERKAUFT));
    list.add(new Status(Anlagevermoegen.STATUS_ENTSORGT));
    this.status = new SelectInput(list,new Status(this.getAnlagevermoegen().getStatus()));
    return this.status;
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
    this.kosten = new DecimalInput(getAnlagevermoegen().getAnschaffungskosten(),Settings.DECIMALFORMAT);
    this.kosten.setComment(i18n.tr("{0}, GWG-Grenze: {1} {0}",new String[]{m.getWaehrung(),Settings.DECIMALFORMAT.format(Settings.getGwgWert(null))}));
    this.kosten.setEnabled(getAnlagevermoegen().canChange());
    this.kosten.setMandatory(true);
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
    this.restwert = new DecimalInput(getAnlagevermoegen().getRestwert(Settings.getActiveGeschaeftsjahr()),Settings.DECIMALFORMAT);
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
    
    this.datum = new DateInput(date);
    this.datum.setText(i18n.tr("Bitte wählen Sie das Anschaffungsdatum"));
    this.datum.setTitle(i18n.tr("Anschaffungsdatum"));
    this.datum.addListener(new RestwertListener());
    this.datum.setEnabled(getAnlagevermoegen().canChange());
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
    this.laufzeit.setEnabled(getAnlagevermoegen().canChange());
    this.laufzeit.setMandatory(true);
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
    this.konto.setEnabled(getAnlagevermoegen().canChange());
    this.konto.setMandatory(true);
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
      k = Settings.getAbschreibungsKonto(jahr,d != null && d.doubleValue() < Settings.getGwgWert(jahr));
    }
    afaKonto = new KontoInput(list,k);
    this.afaKonto.setEnabled(getAnlagevermoegen().canChange());
    this.afaKonto.setMandatory(true);
    return afaKonto;
  }

  /**
   * Liefert ein Label mit der Bezeichnung der ggf zugehoerigen Buchung samt Link zum Oeffnen.
   * @return Label mit Buchung.
   * @throws RemoteException
   */
  public ButtonInput getBuchungLink() throws RemoteException
  {
    if (this.buchungLink != null)
      return this.buchungLink;
    
    this.buchungLink = new BuchungLink();
    this.buchungLink.setEnabled(getAnlagevermoegen().canChange());
    return this.buchungLink;
  }
  
  /**
   * Hilfsklasse.
   */
  private class BuchungLink extends ButtonInput
  {
    private BuchungLink()
    {
      addButtonListener(new Listener()
      {
        public void handleEvent(Event event)
        {
          try
          {
            Buchung b = getAnlagevermoegen().getBuchung();
            if (b == null)
              return;
            new BuchungNeu().handleAction(b);
          }
          catch (Exception e)
          {
            Logger.error("unable to load buchung",e);
          }
        }
      });
    }

    /**
     * @see de.willuhn.jameica.gui.input.ButtonInput#getClientControl(org.eclipse.swt.widgets.Composite)
     */
    public Control getClientControl(Composite parent)
    {
      Label label = GUI.getStyleFactory().createLabel(parent,SWT.NONE);
      try
      {
        Buchung b = getAnlagevermoegen().getBuchung();
        if (b != null)
          label.setText(b.getText());
        else
          label.setText(i18n.tr("keine zugehörige Buchung verfügbar"));
      }
      catch (Exception e)
      {
        Logger.error("unable to display buchung",e);
      }
      return label;
    }

    /**
     * @see de.willuhn.jameica.gui.input.Input#getValue()
     */
    public Object getValue()
    {
      return null;
    }

    /**
     * @see de.willuhn.jameica.gui.input.Input#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
    }
    
  }

  /**
   * Speichert das Anlagevermoegen.
   */
  public void handleStore()
  {
    try
    {
      // Wir erlauben hier nur noch das Aendern des Status
      if (!getAnlagevermoegen().canChange())
      {
        Status s = (Status) this.getStatus().getValue();
        int current = getAnlagevermoegen().getStatus();
        if (current == s.code)
          return; // Nichts geaendert
        
        getAnlagevermoegen().updateStatus(s.code);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Status gespeichert"),StatusBarMessage.TYPE_SUCCESS));
        return;
      }
      
      Konto k = (Konto)getKonto().getValue();

      Konto abschreibung = (Konto)getAbschreibungsKonto().getValue();

      getAnlagevermoegen().setName((String) getName().getValue());
      getAnlagevermoegen().setKonto(k);
      getAnlagevermoegen().setAnschaffungsDatum((Date) getDatum().getValue());
      getAnlagevermoegen().setStatus(((Status)getStatus().getValue()).code);
      
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
      
      if (nutzungsdauer > 1 && ak <= Settings.getGwgWert(Settings.getActiveGeschaeftsjahr()))
      {
        YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
        d.setTitle(i18n.tr("Geringwertiges Wirtschaftsgut"));

        String text = i18n.tr("Anlage kann als GWG sofort abgeschrieben werden.\n" +
                              "Nutzungsdauer zur Sofort-Abschreibung auf 1 Jahr verkürzen?");
        
        Konto ka = Settings.getAbschreibungsKonto(Settings.getActiveGeschaeftsjahr(),true);

        if (ka != null && !ka.equals(abschreibung))
          text += "\n" + i18n.tr("Ausserdem können die Abschreibungen auf dem Konto\n" +
              "\"" + ka.getName() + "\" gebucht werden.");
        d.setText(text);
        try
        {
          if (((Boolean)d.open()).booleanValue())
          {
            getLaufzeit().setValue(new Integer(1));
            nutzungsdauer = 1;
 
            if (ka != null && !ka.equals(abschreibung))
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
  
  /**
   * Hilfsklasse fuer den Status des Anlagegutes.
   */
  private class Status
  {
    private int code = Anlagevermoegen.STATUS_BESTAND;
    
    /**
     * ct.
     * @param status Status-Code.
     * @param text Status-Text.
     */
    private Status(int status)
    {
      this.code = status;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      switch (this.code)
      {
        case Anlagevermoegen.STATUS_BESTAND:
          return i18n.tr("Im Bestand");
        case Anlagevermoegen.STATUS_VERKAUFT:
          return i18n.tr("Veräußert");
        case Anlagevermoegen.STATUS_ENTSORGT:
          return i18n.tr("Entsorgt");
      }
      return i18n.tr("Unbekannter Status");
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
      if (!(o instanceof Status))
        return false;
      return this.code == ((Status)o).code;
    }
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenControl.java,v $
 * Revision 1.26  2012/01/29 22:09:14  willuhn
 * @N Neues DateInput verwenden statt manuellen CalendarDialog (mit DialogInput)
 *
 * Revision 1.25  2011-05-12 09:10:32  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.24  2010-09-20 10:27:36  willuhn
 * @N Neuer Status fuer Anlagevermoegen - damit kann ein Anlagegut auch dann noch in der Auswertung erscheinen, wenn es zwar abgeschrieben ist aber sich noch im Bestand befindet. Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=69910#69910
 *
 * Revision 1.23  2010-09-20 09:19:06  willuhn
 * @B minor gui fixes
 *
 * Revision 1.22  2010-06-04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.21  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.20  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.19.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 * Revision 1.19  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.18  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
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