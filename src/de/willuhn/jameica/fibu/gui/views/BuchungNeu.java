/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungNeu.java,v $
 * $Revision: 1.5 $
 * $Date: 2003/11/24 14:21:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.views;

import java.rmi.RemoteException;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.controller.BuchungControl;
import de.willuhn.jameica.fibu.objects.Buchung;
import de.willuhn.jameica.fibu.objects.Konto;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.CurrencyInput;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.LabelGroup;
import de.willuhn.jameica.views.parts.SelectInput;
import de.willuhn.jameica.views.parts.TextInput;

/**
 * @author willuhn
 */
public class BuchungNeu extends AbstractView
{

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
        buchung = (Buchung) Application.getDefaultDatabase().createObject(Buchung.class,null);
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
    Headline headline     = new Headline(getParent(),I18N.tr("Buchung bearbeiten"));

    // Gruppe Konto erzeugen
    LabelGroup kontoGroup = new LabelGroup(getParent(),I18N.tr("Konto"));

    try {
      
      Konto konto     = buchung.getKonto();
      Konto geldKonto = buchung.getGeldKonto();

      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput datum             = new TextInput(Fibu.DATEFORMAT.format(buchung.getDatum()));
      SelectInput kontoInput      = new SelectInput(konto);
      SelectInput geldKontoInput  = new SelectInput(geldKonto);

      TextInput text              = new TextInput(buchung.getText());
      TextInput belegnummer       = new TextInput(""+buchung.getBelegnummer());
      CurrencyInput betrag        = new CurrencyInput(Fibu.DECIMALFORMAT.format(buchung.getBetrag()),
                                                 Fibu.CURRENCY);
      // Fuer fuegen hinter die beiden Konten noch den Saldo des jeweiligen Kontos hinzu.
      kontoInput.addComment(I18N.tr("Saldo") + ": " + Fibu.DECIMALFORMAT.format(konto.getSaldo()) + " " + Fibu.CURRENCY, new SaldoListener(kontoInput));
      geldKontoInput.addComment(I18N.tr("Saldo") + ": " +Fibu.DECIMALFORMAT.format(geldKonto.getSaldo()) + " " + Fibu.CURRENCY, new SaldoListener(geldKontoInput));

      // Fuegen sie zur Gruppe Konto hinzu
      kontoGroup.addLabelPair(I18N.tr("Datum"),       datum);
      kontoGroup.addLabelPair(I18N.tr("Konto"),       kontoInput);
      kontoGroup.addLabelPair(I18N.tr("Geld-Konto"),  geldKontoInput);
      kontoGroup.addLabelPair(I18N.tr("Text"),        text);
      kontoGroup.addLabelPair(I18N.tr("Beleg-Nr."),   belegnummer);
      kontoGroup.addLabelPair(I18N.tr("Betrag"),      betrag);

      // und registrieren sie im Controller.
      control.register("datum",        datum);
      control.register("konto",        kontoInput);
      control.register("geldkonto",    geldKontoInput);
      control.register("text",         text);
      control.register("belegnummer",  belegnummer);
      control.register("betrag",       betrag);

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
    ButtonArea buttonArea = new ButtonArea(getParent(),3);
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

  /**
   * Listener, der an die Auswahlbox des Kontos angehaengt wurden und
   * den Saldo von dem gerade ausgewaehlten Konto als Kommentar anzeigt.
   * @author willuhn
   * 24.11.2003
   */
  class SaldoListener implements FocusListener,Listener
  {

    private SelectInput select;


    /**
     * Kosntruktor.
     * @param s SelectInput-Feld, an dem der Listener haengt.
     */
    SaldoListener(SelectInput s)
    {
      this.select = s;
    }

    private void handle(Combo c)
    {
      try {
        String kontonummer = (String) c.getText();
        DBIterator list = Application.getDefaultDatabase().createList(Konto.class);
        list.addFilter("kontonummer = " + kontonummer);
        if (!list.hasNext()) return;
        Konto konto = (Konto) list.next();
        select.updateComment(I18N.tr("Saldo") + ": " +
                             Fibu.DECIMALFORMAT.format(konto.getSaldo()) +
                             " " + Fibu.CURRENCY);
      }
      catch (RemoteException es)
      {
        GUI.setActionText(I18N.tr("Fehler bei der Saldenermittlung des Kontos."));
      }
    }

    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      handle((Combo) event.widget);
    }

    /**
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     */
    public void focusGained(FocusEvent event)
    {
      handle((Combo) event.widget);
    }

    /**
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     */
    public void focusLost(FocusEvent event)
    {
      handle((Combo) event.widget);
    }
    
  }
}

/*********************************************************************
 * $Log: BuchungNeu.java,v $
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