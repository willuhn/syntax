/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FinanzamtControl.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/25 00:22:17 $
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
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.objects.Finanzamt;
import de.willuhn.jameica.fibu.objects.Mandant;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.rmi.DBObject;
import de.willuhn.jameica.views.parts.Controller;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Finanzamt".
 * @author willuhn
 */
public class FinanzamtControl extends Controller
{

  /**
   * Erzeugt einen neuen Controller der fuer dieses Finanzamt zustaendig ist.
   * @param object die Buchung.
   */
  public FinanzamtControl(DBObject object)
  {
    super(object);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete(java.lang.String)
   */
  public void handleDelete(String id)
  {
    try {
      this.object = Application.getDefaultDatabase().createObject(Finanzamt.class,id);
      handleDelete();
    }
    catch (RemoteException e)
    {
      // Objekt kann nicht geladen werden. Dann muessen wir es auch nicht loeschen.
      Application.getLog().error("no valid finanzamt found");
      GUI.setActionText(I18N.tr("Finanzamt wurde nicht gefunden."));
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete()
   */
  public void handleDelete()
  {
    Finanzamt fa = (Finanzamt) getObject();

    try {
      if (fa.isNewObject())
      {
        GUI.setActionText(I18N.tr("Finanzamt wurde noch nicht gespeichert und muss daher nicht gelöscht werden."));
        return; // wenn's ein neues Objekt ist, gibt's nichts zu loeschen. ;)
      }

      // Jetzt muessen wir noch checken, ob das Finanzamt einem Mandanten zugewiesen ist.
      DBIterator list = Application.getDefaultDatabase().createList(Mandant.class);
      list.addFilter("finanzamt_id='" + fa.getID() + "'");
      if (list.hasNext())
      {
        // TODO: Das muss noch via deleteCheck() in die Business-Logik.
        MessageBox box = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.OK);
        box.setText(I18N.tr("Finanzamt in Verwendung"));
        box.setMessage(I18N.tr("Das Finanzamt ist einem Mandanten zugwiesen.\nBitte ändern oder löschen zu Sie zunächst den Mandanten."));
        box.open();
        return;
      }

    }
    catch (RemoteException e)
    {
      // Also wenn wir nicht mal ne Verbindung zum Business-Objekt haben, ist sowieso was faul ;)
      Application.getLog().error("no valid finanzamt found");
    }

    MessageBox box = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.YES | SWT.NO);
    box.setText(I18N.tr("Finanzamt wirklich löschen?"));
    box.setMessage(I18N.tr("Wollen Sie die Daten dieses Finanzamtes wirklich löschen?"));
    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt
      try {
        fa.delete();
        GUI.setActionText(I18N.tr("Daten des Finanzamtes gelöscht."));
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Fehler beim Löschen der Daten des Finanzamtes."));
        Application.getLog().error("unable to delete finanzamt");
      }
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.FinanzamtListe",null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    Finanzamt fa = (Finanzamt) getObject();

    try {

      //////////////////////////////////////////////////////////////////////////
      // Pflichtfelder checken
      String name = getField("name").getValue();
      if (name == null || "".equals(name)) {
        GUI.setActionText(I18N.tr("Bitte geben Sie den Namen des Finanzamtes ein."));
        return;
      }

      String plz = getField("plz").getValue();
      if (plz == null || "".equals(plz)) {
        GUI.setActionText(I18N.tr("Bitte geben Sie die Postleitzahl des Finanzamtes ein."));
        return;
      }

      String ort = getField("ort").getValue();
      if (ort == null || "".equals(ort)) {
        GUI.setActionText(I18N.tr("Bitte geben Sie den Ort des Finanzamtes ein."));
        return;
      }

      String strasse  = getField("strasse").getValue();
      String postfach = getField("postfach").getValue();
      if ((strasse == null || "".equals(strasse)) && (postfach == null || "".equals(postfach))) {
        GUI.setActionText(I18N.tr("Bitte geben Sie entweder Postfach oder die Strasse des Finanzamtes ein."));
        return;
      }

      //
      //////////////////////////////////////////////////////////////////////////
      
      fa.setName(name);
      fa.setStrasse(strasse);
      fa.setPLZ(plz);
      fa.setPostfach(postfach);
      fa.setOrt(ort);

      
      // und jetzt speichern wir.
      fa.store();
      GUI.setActionText(I18N.tr("Daten des Finanzamtes gespeichert."));
    }
    catch (Exception e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText("Fehler beim Speichern der Daten des Finanzamtes.");
      Application.getLog().error("unable to store finanzamt");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Finanzamt fa = (Finanzamt) Application.getDefaultDatabase().createObject(Finanzamt.class,id);
      GUI.startView("de.willuhn.jameica.fibu.views.FinanzamtNeu",fa);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load finanzamt with id " + id);
      GUI.setActionText(I18N.tr("Daten des zu ladenden Finanzamtes wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.FinanzamtNeu",null);
  }

}

/*********************************************************************
 * $Log: FinanzamtControl.java,v $
 * Revision 1.1  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/