/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantNeu.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/24 23:02:11 $
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
import de.willuhn.jameica.fibu.controller.MandantControl;
import de.willuhn.jameica.fibu.objects.Kontenrahmen;
import de.willuhn.jameica.fibu.objects.Mandant;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.LabelGroup;
import de.willuhn.jameica.views.parts.SelectInput;
import de.willuhn.jameica.views.parts.TextInput;

/**
 * @author willuhn
 */
public class MandantNeu extends AbstractView
{

  public MandantNeu(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    // Wir laden erstmal das Objekt bzw. erstellen ein neues.
    Mandant mandant = (Mandant) getCurrentObject();
    if (mandant == null)
    {
      try {
        mandant = (Mandant) Application.getDefaultDatabase().createObject(Mandant.class,null);
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Neuer Mandant konnte nicht erzeugt werden."));
      }
    }

    // jetzt erzeugen wir uns einen Controller fuer diesen Dialog.
    // Er wird die Interaktionen mit der Business-Logik uebernehmen.
    // Damit er an die Daten des Dialogs kommt, muessen wir jedes
    // Eingabe-Feld in ihm registrieren.
    final MandantControl control = new MandantControl(mandant);

    // Headline malen
    Headline headline     = new Headline(getParent(),I18N.tr("Mandant bearbeiten"));


    try {
      
      // Gruppe Kontaktdaten erzeugen
      LabelGroup contactGroup = new LabelGroup(getParent(),I18N.tr("Kontaktdaten"));

      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput name1             = new TextInput(mandant.getName1());
      TextInput name2             = new TextInput(mandant.getName2());
      TextInput firma             = new TextInput(mandant.getFirma());
      TextInput strasse           = new TextInput(mandant.getStrasse());
      TextInput plz               = new TextInput(mandant.getPLZ());
      TextInput ort               = new TextInput(mandant.getOrt());

      contactGroup.addLabelPair(I18N.tr("Name 1")  , name1);
      contactGroup.addLabelPair(I18N.tr("Name 2")  , name2);
      contactGroup.addLabelPair(I18N.tr("Firma")   , firma);
      contactGroup.addLabelPair(I18N.tr("Strasse") , strasse);
      contactGroup.addLabelPair(I18N.tr("PLZ")     , plz);
      contactGroup.addLabelPair(I18N.tr("Ort")     , ort);

      LabelGroup finanzGroup = new LabelGroup(getParent(),I18N.tr("Buchhalterische Daten"));

      Kontenrahmen kr = mandant.getKontenrahmen();
      if (kr == null) kr = (Kontenrahmen) Application.getDefaultDatabase().createObject(Kontenrahmen.class,null);

      SelectInput kontenrahmen = new SelectInput(kr);
      TextInput steuernummer   = new TextInput(mandant.getSteuernummer());

      finanzGroup.addLabelPair(I18N.tr("Kontenrahmen"),kontenrahmen);
      finanzGroup.addLabelPair(I18N.tr("Steuernummer"),steuernummer);

      control.register("name1",name1);
      control.register("name2",name2);
      control.register("firma",firma);
      control.register("strasse",strasse);
      control.register("plz",plz);
      control.register("ort",ort);
      control.register("steuernummer",steuernummer);
      control.register("kontenrahmen",kontenrahmen);

    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Mandantendaten."));
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
 * $Log: MandantNeu.java,v $
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/