/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtNeu.java,v $
 * $Revision: 1.4 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.controller.FinanzamtControl;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;

/**
 * @author willuhn
 */
public class FinanzamtNeu extends AbstractView
{

  public FinanzamtNeu(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    // Wir laden erstmal das Objekt bzw. erstellen ein neues.
    Finanzamt fa = (Finanzamt) getCurrentObject();
    if (fa == null)
    {
      try {
        fa = (Finanzamt) Settings.getDatabase().createObject(Finanzamt.class,null);
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Daten für neues Finanzamt konnte nicht erzeugt werden."));
      }
    }

    // jetzt erzeugen wir uns einen Controller fuer diesen Dialog.
    // Er wird die Interaktionen mit der Business-Logik uebernehmen.
    // Damit er an die Daten des Dialogs kommt, muessen wir jedes
    // Eingabe-Feld in ihm registrieren.
    final FinanzamtControl control = new FinanzamtControl(fa);

    // Headline malen
    new Headline(getParent(),I18N.tr("Daten des Finanzamtes bearbeiten"));

    try {
      
      // Gruppe Kontaktdaten erzeugen
      LabelGroup contactGroup = new LabelGroup(getParent(),I18N.tr("Anschriftsdaten"));

      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput name      = new TextInput(fa.getName());
      TextInput postfach  = new TextInput(fa.getPostfach());
      TextInput strasse   = new TextInput(fa.getStrasse());
      TextInput plz       = new TextInput(fa.getPLZ());
      TextInput ort       = new TextInput(fa.getOrt());

      contactGroup.addLabelPair(I18N.tr("Name")    , name);
      contactGroup.addLabelPair(I18N.tr("Strasse") , strasse);
      contactGroup.addLabelPair(I18N.tr("Postfach"), postfach);
      contactGroup.addLabelPair(I18N.tr("PLZ")     , plz);
      contactGroup.addLabelPair(I18N.tr("Ort")     , ort);

      control.register("name",name);
      control.register("postfach",postfach);
      control.register("strasse",strasse);
      control.register("plz",plz);
      control.register("ort",ort);

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
 * $Log: FinanzamtNeu.java,v $
 * Revision 1.4  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/