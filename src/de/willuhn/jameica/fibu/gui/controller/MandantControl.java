/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/MandantControl.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/11/27 00:21:05 $
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
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.objects.Finanzamt;
import de.willuhn.jameica.fibu.objects.Kontenrahmen;
import de.willuhn.jameica.fibu.objects.Mandant;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.rmi.DBObject;
import de.willuhn.jameica.views.parts.Controller;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Mandant".
 * @author willuhn
 */
public class MandantControl extends Controller
{

  /**
   * Erzeugt einen neuen Controller der fuer diesen Mandanten zustaendig ist.
   * @param object die Buchung.
   */
  public MandantControl(DBObject object)
  {
    super(object);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete(java.lang.String)
   */
  public void handleDelete(String id)
  {
    try {
      this.object = Application.getDefaultDatabase().createObject(Mandant.class,id);
      handleDelete();
    }
    catch (RemoteException e)
    {
      // Objekt kann nicht geladen werden. Dann muessen wir es auch nicht loeschen.
      Application.getLog().error("no valid mandant found");
      GUI.setActionText(I18N.tr("Mandant wurde nicht gefunden."));
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete()
   */
  public void handleDelete()
  {
    Mandant mandant = (Mandant) getObject();

    try {

      if (mandant.isNewObject())
      {
        GUI.setActionText(I18N.tr("Mandant wurde noch nicht gespeichert und muss daher nicht gelöscht werden."));
        return; // wenn's ein neues Objekt ist, gibt's nichts zu loeschen. ;)
      }

      MessageBox box = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.YES | SWT.NO);
      box.setText(I18N.tr("Mandant wirklich löschen?"));
      box.setMessage(I18N.tr("Wollen Sie diesen Mandanten wirklich löschen?"));
      if (box.open() != SWT.YES)
        return;

      // ok, wir loeschen das Objekt
      mandant.delete();
      GUI.setActionText(I18N.tr("Mandant gelöscht."));
    }
    catch (ApplicationException e1)
    {
      MessageBox box2 = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.OK);
      box2.setText(I18N.tr("Fehler"));
      box2.setMessage(e1.getLocalizedMessage());
      box2.open();
      return;
      
    }
    catch (RemoteException e)
    {
      GUI.setActionText(I18N.tr("Fehler beim Löschen des Mandanten."));
      Application.getLog().error("unable to delete mandant");
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.MandantListe",null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    Mandant mandant = (Mandant) getObject();

    try {

      //////////////////////////////////////////////////////////////////////////
      // Kontenrahmen checken
      
      DBIterator kontenrahmen = Application.getDefaultDatabase().createList(Kontenrahmen.class);
      kontenrahmen.addFilter("name = '"+getField("kontenrahmen").getValue()+"'");
      if (!kontenrahmen.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewählter Kontenrahmen existiert nicht."));
        return;
      }
      mandant.setKontenrahmen((Kontenrahmen) kontenrahmen.next());
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Geschaeftsjahr checken

      int geschaeftsjahr = 0;
      try {
        geschaeftsjahr = Integer.parseInt(getField("geschaeftsjahr").getValue());
      }
      catch (NumberFormatException e)
      {
        GUI.setActionText(I18N.tr("Bitte geben Sie eine gültige Jahreszahl zwischen ") + 
                          Fibu.YEAR_MIN + I18N.tr(" und ") + Fibu.YEAR_MAX + I18N.tr(" ein"));
        return;
      }
      mandant.setGeschaeftsjahr(geschaeftsjahr);
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Finanzamt checken
      
      DBIterator finanzaemter = Application.getDefaultDatabase().createList(Finanzamt.class);
      finanzaemter.addFilter("name = '"+getField("finanzamt").getValue()+"'");
      if (!finanzaemter.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewähltes Finanzamt existiert nicht."));
        return;
      }
      mandant.setFinanzamt((Finanzamt) finanzaemter.next());
      //
      //////////////////////////////////////////////////////////////////////////

      mandant.setName1(getField("name1").getValue());
      mandant.setName2(getField("name2").getValue());
      mandant.setFirma(getField("firma").getValue());
      mandant.setStrasse(getField("strasse").getValue());
      mandant.setPLZ(getField("plz").getValue());
      mandant.setOrt(getField("ort").getValue());
      mandant.setSteuernummer(getField("steuernummer").getValue());

      
      // und jetzt speichern wir.
      mandant.store();
      GUI.setActionText(I18N.tr("Mandant gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText("Fehler beim Speichern des Mandanten.");
      Application.getLog().error("unable to store mandant");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Mandant mandant = (Mandant) Application.getDefaultDatabase().createObject(Mandant.class,id);
      GUI.startView("de.willuhn.jameica.fibu.views.MandantNeu",mandant);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load mandant with id " + id);
      GUI.setActionText(I18N.tr("Mandant wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.MandantNeu",null);
  }

}

/*********************************************************************
 * $Log: MandantControl.java,v $
 * Revision 1.3  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.2  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/