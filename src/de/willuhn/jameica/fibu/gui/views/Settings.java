/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/Settings.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/12/11 21:00:34 $
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
import de.willuhn.jameica.fibu.gui.controller.SettingsControl;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;
import de.willuhn.jameica.rmi.DBIterator;

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
    new Headline(getParent(),I18N.tr("Einstellungen"));

    // Gruppe fachliche Einstellungen erzeugen
    LabelGroup group = new LabelGroup(getParent(),I18N.tr("fachliche Einstellungen"));

    boolean store = false;
    try {


      //////////////////////////////////////
      // Mandant
      Input mandantInput = null;
      Mandant mandant = de.willuhn.jameica.fibu.Settings.getActiveMandant();
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
      TextInput currency = new TextInput(de.willuhn.jameica.fibu.Settings.getCurrency());
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
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/