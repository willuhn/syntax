/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/MandantControl.java,v $
 * $Revision: 1.9 $
 * $Date: 2004/01/03 18:07:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.jameica.*;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.MandantListe;
import de.willuhn.jameica.fibu.gui.views.MandantNeu;
import de.willuhn.jameica.fibu.rmi.*;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.parts.Controller;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Mandant".
 * @author willuhn
 */
public class MandantControl extends Controller
{

  /**
   * Erzeugt einen neuen Controller der fuer diesen Mandanten zustaendig ist.
   * @param object der Mandant.
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
      this.object = Settings.getDatabase().createObject(Mandant.class,id);
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

      MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
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
      MessageBox box2 = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.OK);
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
    GUI.startView(MandantListe.class.getName(),null);
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
      
      Kontenrahmen kr = (Kontenrahmen) Settings.getDatabase().createObject(Kontenrahmen.class,getField("kontenrahmen").getValue());
      mandant.setKontenrahmen(kr);
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
      
      DBIterator finanzaemter = Settings.getDatabase().createList(Finanzamt.class);
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
			Application.getLog().error("unable to store mandant",e);
      GUI.setActionText("Fehler beim Speichern des Mandanten.");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Mandant mandant = (Mandant) Settings.getDatabase().createObject(Mandant.class,id);
      GUI.startView(MandantNeu.class.getName(),mandant);
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
    GUI.startView(MandantNeu.class.getName(),null);
  }

}

/*********************************************************************
 * $Log: MandantControl.java,v $
 * Revision 1.9  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.8  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.5  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
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