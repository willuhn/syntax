/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungNeu.java,v $
 * $Revision: 1.16 $
 * $Date: 2003/12/15 19:08:03 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.widgets.*;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.controller.BuchungControl;
import de.willuhn.jameica.fibu.rmi.*;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;
import de.willuhn.jameica.rmi.DBIterator;

/**
 * @author willuhn
 */
public class BuchungNeu extends AbstractView
{

  private DecimalInput steuer;
  private ButtonArea buttonArea; // Member damit wir den Speichern-Knopf bei Fehlern ausgrauen koennen

  public BuchungNeu(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    // Wir laden erstmal das Objekt bzw. erstellen ein neues.
    Buchung buchung = (Buchung) getCurrentObject();
    if (buchung == null)
    {
      try {
        buchung = (Buchung) Settings.getDatabase().createObject(Buchung.class,null);
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Neue Buchung konnte nicht erzeugt werden."));
      }
    }

    // jetzt erzeugen wir uns einen Controller fuer diesen Dialog.
    // Er wird die Interaktionen mit der Business-Logik uebernehmen.
    // Damit er an die Daten des Dialogs kommt, muessen wir jedes
    // Eingabe-Feld in ihm registrieren.
    final BuchungControl control = new BuchungControl(buchung);

    // Headline malen
    new Headline(getParent(),I18N.tr("Buchung bearbeiten"));

    // Gruppe Konto erzeugen
    LabelGroup kontoGroup = new LabelGroup(getParent(),I18N.tr("Konto"));

    try {
      
      // Saldo: Summe aller Netto-Buchungen auf diesem Konto
      Konto konto     = buchung.getKonto();
      if (konto == null) konto = (Konto) Settings.getDatabase().createObject(Konto.class,null);

      // besser Eingabefeld mit Knopf zur Suche dahinter
      GeldKonto geldKonto = buchung.getGeldKonto();
      if (geldKonto == null) geldKonto = (GeldKonto) Settings.getDatabase().createObject(GeldKonto.class,null);

      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput datum             = new TextInput(Fibu.DATEFORMAT.format(buchung.getDatum()));
      datum.addComment(I18N.tr("Wochentag: "),new WochentagListener(datum));

      SearchInput kontoInput      = new SearchInput(konto.getKontonummer(), new KontoSearchDialog());
      SearchInput geldKontoInput  = new SearchInput(geldKonto.getKontonummer(), new GeldKontoSearchDialog());

      TextInput text              = new TextInput(buchung.getText());
      TextInput belegnummer       = new TextInput(""+buchung.getBelegnummer());
      DecimalInput betrag         = new DecimalInput(Fibu.DECIMALFORMAT.format(buchung.getBetrag()));
        betrag.addComment(Settings.getCurrency(),null);

      // Das Ding ist deswegen ein Member, weil wir es beim Kontowechsel aktualisieren muessen
      steuer         = new DecimalInput(Fibu.DECIMALFORMAT.format(buchung.getSteuer()));
        steuer.addComment("%",null);

      // Wir fuegen hinter die beiden Konten noch den Saldo des jeweiligen Kontos hinzu.
      kontoInput.addComment(I18N.tr("Saldo") + ": " + Fibu.DECIMALFORMAT.format(konto.getSaldo()) + " " + Settings.getCurrency(), new SaldoListener(kontoInput,steuer));
      geldKontoInput.addComment(I18N.tr("Saldo") + ": " +Fibu.DECIMALFORMAT.format(geldKonto.getSaldo()) + " " + Settings.getCurrency(), new SaldoListener(geldKontoInput,null));

      // Fuegen sie zur Gruppe Konto hinzu
      kontoGroup.addLabelPair(I18N.tr("Datum"),       datum);
      kontoGroup.addLabelPair(I18N.tr("Konto"),       kontoInput);
      kontoGroup.addLabelPair(I18N.tr("Geld-Konto"),  geldKontoInput);
      kontoGroup.addLabelPair(I18N.tr("Text"),        text);
      kontoGroup.addLabelPair(I18N.tr("Beleg-Nr."),   belegnummer);
      kontoGroup.addLabelPair(I18N.tr("Betrag"),      betrag);
      kontoGroup.addLabelPair(I18N.tr("Steuer"),      steuer);

      // und registrieren sie im Controller.
      control.register("datum",        datum);
      control.register("konto",        kontoInput);
      control.register("geldkonto",    geldKontoInput);
      control.register("text",         text);
      control.register("belegnummer",  belegnummer);
      control.register("betrag",       betrag);
      control.register("steuer",       steuer);

      // wir machen das Datums-Feld zu dem mit dem Focus.
      datum.focus();

    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Buchungsdaten."));
    }


    // und noch die Abschicken-Knoepfe
    buttonArea = new ButtonArea(getParent(),3);
    buttonArea.addCancelButton(control);
    buttonArea.addDeleteButton(control);
    buttonArea.addStoreButton(control);

  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }


  class WochentagListener implements Listener
  {
    private TextInput text;
    
    WochentagListener(TextInput t)
    {
      text = t;
    }

    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      Text t = (Text) event.widget;
      String datum = t.getText();

      Date d = null;
      try {
        d = Fibu.DATEFORMAT.parse(datum);
      }
      catch (ParseException e)
      {
        // ok, evtl. ein Datum in Kurzformat, wir versuchen's mal
        try {
          d = Fibu.FASTDATEFORMAT.parse(datum);
        }
        catch (ParseException e2)
        {
          try {
            // ok, evtl. 4-stelliges Datum mit GJ vom Mandanten
            d = Fibu.FASTDATEFORMAT.parse(datum + Settings.getActiveMandant().getGeschaeftsjahr());
          }
          catch (Exception e3)
          {
            // Ne, hat keinen Zweck.
            return;
          }
        }
      }

      if (d == null)
        return;

      Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());
      cal.setTime(d);
      int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
      if (i < 0 || i >= Fibu.WEEKDAYS.length)
        return;
      text.updateComment(I18N.tr("Wochentag: ") + I18N.tr(Fibu.WEEKDAYS[i]));
    }
  }

  /**
   * Listener, der an die Auswahlbox des Kontos angehaengt wurden und
   * den Saldo von dem gerade ausgewaehlten Konto als Kommentar anzeigt.
   * @author willuhn
   * 24.11.2003
   */
  class SaldoListener implements Listener
  {
    private SearchInput search;
    private DecimalInput steuer;

    /**
     * Konstruktor.
     * @param search Search-Feld, an dem der Listener haengt.
     * @param s DecimalInput mit dem Feld fuer die Steuer des Kontos. Optional.
     */
    SaldoListener(SearchInput search, DecimalInput s)
    {
      this.search = search;
      this.steuer = s;
    }

    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      if (!(event.widget instanceof Text))
        return;
      try {
        Text t = (Text) event.widget;
        String kontonummer = t.getText();
        if (kontonummer == null || "".equals(kontonummer))
        {
          return; // Kontonummer fehlt oder ist leer -> keine Saldenermittlung moeglich
        }

        DBIterator list = Settings.getDatabase().createList(Konto.class);
        list.addFilter("kontonummer = " + kontonummer);
        if (!list.hasNext())
        {
          GUI.setActionText(I18N.tr("Das ausgewählte Konto existiert nicht."));
          return;
        } 
        Konto konto = (Konto) list.next();
        search.updateComment(I18N.tr("Saldo") + ": " +
                             Fibu.DECIMALFORMAT.format(konto.getSaldo()) +
                             " " + Settings.getCurrency());
      
        // wenn uns das Steuer-Eingabefeld uebergeben wurde, aktualisieren wir es auch gleich
        if (steuer != null)
        {
          steuer.setValue(Fibu.DECIMALFORMAT.format(konto.getSteuer().getSatz()));
        }
        GUI.setActionText("");
      }
      catch (RemoteException es)
      {
        GUI.setActionText(I18N.tr("Fehler bei der Saldenermittlung des Kontos."));
      }
    }

  }
}

/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.16  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.14  2003/12/10 01:12:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2003/12/10 00:47:29  willuhn
 * @N SearchDialog for Konto works now ;)
 *
 * Revision 1.12  2003/12/08 15:41:25  willuhn
 * @N searchInput
 *
 * Revision 1.11  2003/12/05 18:42:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.9  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.5  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/11/23 19:26:25  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/