/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/Settings.java,v $
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
import de.willuhn.jameica.fibu.controller.SettingsControl;
import de.willuhn.jameica.fibu.objects.Mandant;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.Input;
import de.willuhn.jameica.views.parts.LabelGroup;
import de.willuhn.jameica.views.parts.LabelInput;
import de.willuhn.jameica.views.parts.SelectInput;
import de.willuhn.jameica.views.parts.TextInput;

/**
 * @author willuhn
 */
public class Settings extends AbstractView
{

  public Settings(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    final SettingsControl control = new SettingsControl(null);

    // Headline malen
    Headline headline     = new Headline(getParent(),I18N.tr("Einstellungen"));

    // Gruppe fachliche Einstellungen erzeugen
    LabelGroup group = new LabelGroup(getParent(),I18N.tr("fachliche Einstellungen"));

    boolean store = false;
    try {


      //////////////////////////////////////
      // Mandant
      Input mandantInput = null;
      Mandant mandant = de.willuhn.jameica.fibu.objects.Settings.getActiveMandant();
      if (mandant == null) // noch keiner ausgewahlt. Dann jetzt bitte tun.
        mandant = (Mandant) Application.getDefaultDatabase().createObject(Mandant.class,null);
      

      DBIterator list = mandant.getList();
      if (list.hasNext())
      {
        mandantInput    = new SelectInput(mandant);
        store = true;
      }
      else {
        mandantInput    = new LabelInput(I18N.tr("Kein Mandant vorhanden. Bitte richten Sie zunächst einen ein."));
      }
      group.addLabelPair(I18N.tr("aktiver Mandant"),mandantInput);
      //
      //////////////////////////////////////

      //////////////////////////////////////
      // Waehrung
      TextInput currency = new TextInput(de.willuhn.jameica.fibu.objects.Settings.getCurrency());
      group.addLabelPair(I18N.tr("Währungsbezeichnung"),currency);
      //
      //////////////////////////////////////

      control.register("mandant", mandantInput);
      control.register("currency", currency);

    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Einstellungen."));
    }

    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea(getParent(),store ? 2 : 1);
    buttonArea.addCancelButton(control);

    if (store) buttonArea.addStoreButton(control);
    
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }

}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/