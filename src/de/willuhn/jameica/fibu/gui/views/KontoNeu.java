/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoNeu.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/05 17:11:58 $
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
import de.willuhn.jameica.fibu.controller.KontoControl;
import de.willuhn.jameica.fibu.objects.Kontenrahmen;
import de.willuhn.jameica.fibu.objects.Konto;
import de.willuhn.jameica.fibu.objects.Kontoart;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.LabelGroup;
import de.willuhn.jameica.views.parts.LabelInput;
import de.willuhn.jameica.views.parts.SelectInput;
import de.willuhn.jameica.views.parts.TextInput;

/**
 * @author willuhn
 */
public class KontoNeu extends AbstractView
{

  public KontoNeu(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    // Wir laden erstmal das Objekt bzw. erstellen ein neues.
    Konto konto = (Konto) getCurrentObject();
    if (konto == null)
    {
      GUI.setActionText(I18N.tr("Konto wurde nicht gefunden."));
      GUI.startView("de.willuhn.jameica.fibu.views.KontoListe",null);
    }

    // jetzt erzeugen wir uns einen Controller fuer diesen Dialog.
    // Er wird die Interaktionen mit der Business-Logik uebernehmen.
    // Damit er an die Daten des Dialogs kommt, muessen wir jedes
    // Eingabe-Feld in ihm registrieren.
    final KontoControl control = new KontoControl(konto);

    // Headline malen
    new Headline(getParent(),I18N.tr("Konto bearbeiten"));

    try {
      
      // Gruppe erzeugen
      LabelGroup group = new LabelGroup(getParent(),I18N.tr("Eigenschaften des Kontos"));

      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput name             = new TextInput(konto.getName());
      TextInput kontonummer      = new TextInput(konto.getKontonummer());
      SelectInput steuer         = new SelectInput(konto.getSteuer());

      Kontoart ka = konto.getKontoArt();
      LabelInput kontoart        = new LabelInput((String) ka.getField(ka.getPrimaryField()));

      Kontenrahmen k = konto.getKontenrahmen();
      LabelInput kontenrahmen    = new LabelInput((String) k.getField(k.getPrimaryField()));

      group.addLabelPair(I18N.tr("Name")        , name);
      group.addLabelPair(I18N.tr("Kontonummer") , kontonummer);
      group.addLabelPair(I18N.tr("Steuersatz")  , steuer);
      group.addLabelPair(I18N.tr("Kontoart")    , kontoart);
      group.addLabelPair(I18N.tr("Kontenrahmen"), kontenrahmen);

      control.register("name",name);
      control.register("kontonummer",kontonummer);
      control.register("steuer",steuer);
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Konto-Daten."));
    }


    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea(getParent(),2);
    buttonArea.addCancelButton(control);
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
 * $Log: KontoNeu.java,v $
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/