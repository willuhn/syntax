/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/KontoControl.java,v $
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
package de.willuhn.jameica.fibu.controller;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.objects.Konto;
import de.willuhn.jameica.fibu.objects.Steuer;
import de.willuhn.jameica.rmi.DBObject;
import de.willuhn.jameica.views.parts.Controller;
import de.willuhn.jameica.views.parts.SelectInput;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Konto".
 * @author willuhn
 */
public class KontoControl extends Controller
{

  /**
   * Erzeugt einen neuen Controller der fuer dieses Konto zustaendig ist.
   * @param object das Konto.
   */
  public KontoControl(DBObject object)
  {
    super(object);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete(java.lang.String)
   */
  public void handleDelete(String id)
  {
    handleDelete();
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete()
   */
  public void handleDelete()
  {
    MessageBox box = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.OK);
    box.setText(I18N.tr("Fehler"));
    box.setMessage(I18N.tr("Konten dürfen nicht gelöscht werden."));
    box.open();
    return;
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.KontoListe",null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    Konto konto = (Konto) getObject();

    try {

      konto.setName(getField("name").getValue());
      konto.setKontonummer(getField("kontonummer").getValue());

      // Kontenrahmen und Kontoart darf nicht geaendert werden

      //////////////////////////////////////////////////////////////////////////
      // Steuersatz checken
      String choosen = getField("steuer").getValue();
      Steuer steuer = (Steuer) ((SelectInput) getField("steuer")).getValue(choosen);
      konto.setSteuer(steuer);
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      konto.store();
      GUI.setActionText(I18N.tr("Konto gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText("Fehler beim Speichern des Kontos.");
      Application.getLog().error("unable to store konto");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Konto konto = (Konto) Application.getDefaultDatabase().createObject(Konto.class,id);
      GUI.startView("de.willuhn.jameica.fibu.views.KontoNeu",konto);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load konto with id " + id);
      GUI.setActionText(I18N.tr("Konto wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    MessageBox box = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.OK);
    box.setText(I18N.tr("Fehler"));
    box.setMessage(I18N.tr("Neue Konten können nicht angelegt werden."));
    box.open();
    return;
  }

}

/*********************************************************************
 * $Log: KontoControl.java,v $
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/