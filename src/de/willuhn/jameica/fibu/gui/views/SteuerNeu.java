/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/SteuerNeu.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/12/10 23:51:52 $
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
import de.willuhn.jameica.fibu.controller.SteuerControl;
import de.willuhn.jameica.fibu.objects.Steuer;
import de.willuhn.jameica.fibu.objects.SteuerKonto;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.DecimalInput;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.LabelGroup;
import de.willuhn.jameica.views.parts.SearchInput;
import de.willuhn.jameica.views.parts.TextInput;

/**
 * @author willuhn
 */
public class SteuerNeu extends AbstractView
{

  public SteuerNeu(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    // Wir laden erstmal das Objekt bzw. erstellen ein neues.
    Steuer steuer = (Steuer) getCurrentObject();
    if (steuer == null)
    {
      try {
        steuer = (Steuer) Application.getDefaultDatabase().createObject(Steuer.class,null);
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Neuer Steuersatz konnte nicht erzeugt werden."));
      }
    }

    // jetzt erzeugen wir uns einen Controller fuer diesen Dialog.
    // Er wird die Interaktionen mit der Business-Logik uebernehmen.
    // Damit er an die Daten des Dialogs kommt, muessen wir jedes
    // Eingabe-Feld in ihm registrieren.
    final SteuerControl control = new SteuerControl(steuer);

    // Headline malen
    new Headline(getParent(),I18N.tr("Steuersatz bearbeiten"));

    try {
      
      LabelGroup steuerGroup = new LabelGroup(getParent(),I18N.tr("Steuersatz"));

      // Wir erzeugen uns alle Eingabe-Felder mit den Daten aus dem Objekt.
      TextInput name    = new TextInput(steuer.getName());
      DecimalInput satz = new DecimalInput(Fibu.DECIMALFORMAT.format(steuer.getSatz()));
        satz.addComment("%",null);

      SteuerKonto konto = steuer.getSteuerKonto();
      if (konto == null) konto = (SteuerKonto) Application.getDefaultDatabase().createObject(SteuerKonto.class,null);
      SearchInput kontoInput = new SearchInput(konto.getKontonummer(), new SteuerKontoSearchDialog());

      steuerGroup.addLabelPair(I18N.tr("Name")      , name);
      steuerGroup.addLabelPair(I18N.tr("Steuersatz"), satz);
      steuerGroup.addLabelPair(I18N.tr("Steuer-Sammelkonto"), kontoInput);

      control.register("name",name);
      control.register("satz",satz);
      control.register("steuerkonto",kontoInput);
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Steuersätze."));
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
 * $Log: SteuerNeu.java,v $
 * Revision 1.3  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/