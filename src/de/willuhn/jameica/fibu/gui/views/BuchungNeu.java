/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungNeu.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/11/21 02:10:57 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.controller.BuchungNeuControl;
import de.willuhn.jameica.fibu.objects.Buchung;
import de.willuhn.jameica.rmi.DBObject;
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
    final BuchungNeuControl control = new BuchungNeuControl(buchung);

    // Headline malen
    Headline headline     = new Headline(getParent(),I18N.tr("Buchung bearbeiten"));

    // Gruppe Konto erzeugen
    LabelGroup kontoGroup = new LabelGroup(getParent(),I18N.tr("Konto"));

    try {
      
      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput datum       = new TextInput(Fibu.DATEFORMAT.format(buchung.getDatum()));
      SelectInput konto     = new SelectInput((DBObject) buchung.getKonto());
      TextInput text        = new TextInput(buchung.getText());
      TextInput belegnummer = new TextInput(""+buchung.getBelegnummer());
      CurrencyInput betrag  = new CurrencyInput(buchung.getBetrag(),"EUR");

      // Fuegen sie zur Gruppe Konto hinzu
      kontoGroup.addLabelPair(I18N.tr("Datum"),     datum);
      kontoGroup.addLabelPair(I18N.tr("Konto"),     konto);
      kontoGroup.addLabelPair(I18N.tr("Text"),      text);
      kontoGroup.addLabelPair(I18N.tr("Beleg-Nr."), belegnummer);
      kontoGroup.addLabelPair(I18N.tr("Betrag"),    betrag);

      // und registrieren sie im Controller.
      control.register("datum",        datum);
      control.register("konto",        konto);
      control.register("text",         text);
      control.register("belegnummer",  belegnummer);
      control.register("betrag",       betrag);

    }
    catch (RemoteException e)
    {
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
}

/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.2  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/