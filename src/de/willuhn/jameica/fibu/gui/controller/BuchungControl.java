/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/BuchungControl.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/11/24 17:27:53 $
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
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.objects.Buchung;
import de.willuhn.jameica.fibu.objects.Konto;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.rmi.DBObject;
import de.willuhn.jameica.views.parts.Controller;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Neue Buchung".
 * @author willuhn
 */
public class BuchungControl extends Controller
{

  /**
   * Erzeugt einen neuen Controller der fuer diese Buchung zustaendig ist.
   * @param object die Buchung.
   */
  public BuchungControl(DBObject object)
  {
    super(object);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete(java.lang.String)
   */
  public void handleDelete(String id)
  {
    try {
      this.object = Application.getDefaultDatabase().createObject(Buchung.class,id);
    }
    catch (RemoteException e)
    {
      // Objekt kann nicht geladen werden. Dann muessen wir es auch nicht loeschen.
      Application.getLog().error("no valid buchung found");
      GUI.setActionText(I18N.tr("Buchung wurde nicht gefunden."));
    }
    handleDelete();
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete()
   */
  public void handleDelete()
  {
    Buchung buchung = (Buchung) getObject();
    int beleg = 0;

    try {
      beleg = buchung.getBelegnummer();
      if (buchung.isNewObject())
      {
        GUI.setActionText(I18N.tr("Buchung wurde noch nicht gespeichert und muss daher nicht gelöscht werden."));
        return; // wenn's ein neues Objekt ist, gibt's nichts zu loeschen. ;)
      }
    }
    catch (RemoteException e)
    {
      // Also wenn wir nicht mal ne Verbindung zum Business-Objekt haben, ist sowieso was faul ;)
      Application.getLog().error("no valid buchung found");
    }

    MessageBox box = new MessageBox(GUI.shell,SWT.ICON_WARNING | SWT.YES | SWT.NO);
    box.setText(I18N.tr("Buchung wirklich löschen?"));
    box.setMessage(I18N.tr("Wollen Sie diese Buchung wirklich löschen?"));
    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt und wechseln zurueck zur Buchungsliste
      try {
        buchung.delete();
        GUI.setActionText(I18N.tr("Buchung Nr. " + beleg + " gelöscht."));
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Fehler beim Löschen der Buchung."));
        Application.getLog().error("unable to delete buchung");
      }
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.BuchungListe",null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    Buchung buchung = (Buchung) getObject();

    try {

      //////////////////////////////////////////////////////////////////////////
      // Belegnummer checken
      try {
        buchung.setBelegnummer(Integer.parseInt(getField("belegnummer").getValue()));
      }
      catch (NumberFormatException e)
      {
        GUI.setActionText(I18N.tr("Belegnummer ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Betrag checken
      try {
        String b = getField("betrag").getValue();
        b = b.replaceAll(",",".");
        
        buchung.setBetrag(Double.parseDouble(b));
      }
      catch (NumberFormatException e)
      {
        GUI.setActionText(I18N.tr("Betrag ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Datum checken
      
      try {
        buchung.setDatum(Fibu.DATEFORMAT.parse(getField("datum").getValue()));
      }
      catch (ParseException e)
      {
        GUI.setActionText(I18N.tr("Datum ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Konto checken
      
      DBIterator konten = Application.getDefaultDatabase().createList(Konto.class);
      konten.addFilter("kontonummer = '"+getField("konto").getValue()+"'");
      if (!konten.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewähltes Konto existiert nicht."));
        return;
      }
      buchung.setKonto((Konto) konten.next());
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // GeldKonto checken
      
      DBIterator geldkonten = Application.getDefaultDatabase().createList(Konto.class);
      geldkonten.addFilter("kontonummer = '"+getField("geldkonto").getValue()+"'");
      if (!geldkonten.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewähltes Geld-Konto existiert nicht."));
        return;
      }
      buchung.setGeldKonto((Konto) geldkonten.next());
      //
      //////////////////////////////////////////////////////////////////////////

      buchung.setText(getField("text").getValue());

      // und jetzt speichern wir.
      buchung.store();
      GUI.setActionText(I18N.tr("Buchung Nr. " + buchung.getBelegnummer() + " gespeichert."));
      // jetzt machen wir die Buchung leer, damit sie beim naechsten Druck
      // auf Speichern als neue Buchung gespeichert wird.
      buchung.clear();
      GUI.startView("de.willuhn.jameica.fibu.views.BuchungNeu",buchung);

    }
    catch (Exception e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText("Fehler beim Speichern der Buchung.");
      Application.getLog().error("unable to store buchung");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Buchung buchung = (Buchung) Application.getDefaultDatabase().createObject(Buchung.class,id);
      GUI.startView("de.willuhn.jameica.fibu.views.BuchungNeu",buchung);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load buchung with id " + id);
      GUI.setActionText(I18N.tr("Buchung wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView("de.willuhn.jameica.fibu.views.BuchungNeu",null);
  }

}

/*********************************************************************
 * $Log: BuchungControl.java,v $
 * Revision 1.3  2003/11/24 17:27:53  willuhn
 * @N Context menu in table
 *
 * Revision 1.2  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 **********************************************************************/