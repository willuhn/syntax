/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FinanzamtControl.java,v $
 * $Revision: 1.7 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.FinanzamtListe;
import de.willuhn.jameica.fibu.gui.views.FinanzamtNeu;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.parts.Controller;
import de.willuhn.jameica.rmi.DBObject;

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
      this.object = Settings.getDatabase().createObject(Finanzamt.class,id);
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

    }
    catch (RemoteException e)
    {
      // Also wenn wir nicht mal ne Verbindung zum Business-Objekt haben, ist sowieso was faul ;)
      Application.getLog().error("no valid finanzamt found");
    }

    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
    box.setText(I18N.tr("Finanzamt wirklich löschen?"));
    box.setMessage(I18N.tr("Wollen Sie die Daten dieses Finanzamtes wirklich löschen?"));
    if (box.open() == SWT.YES)
    {
      // ok, wir loeschen das Objekt
      try {
        fa.delete();
        GUI.setActionText(I18N.tr("Daten des Finanzamtes gelöscht."));
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
    GUI.startView(FinanzamtListe.class.getName(),null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    Finanzamt fa = (Finanzamt) getObject();

    try {

      fa.setName(getField("name").getValue());
      fa.setStrasse(getField("strasse").getValue());
      fa.setPLZ(getField("plz").getValue());
      fa.setPostfach(getField("postfach").getValue());
      fa.setOrt(getField("ort").getValue());

      
      // und jetzt speichern wir.
      fa.store();
      GUI.setActionText(I18N.tr("Daten des Finanzamtes gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
			Application.getLog().error("unable to store finanzamt",e);
      GUI.setActionText("Fehler beim Speichern der Daten des Finanzamtes.");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Finanzamt fa = (Finanzamt) Settings.getDatabase().createObject(Finanzamt.class,id);
      GUI.startView(FinanzamtNeu.class.getName(),fa);
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
    GUI.startView(FinanzamtNeu.class.getName(),null);
  }

}

/*********************************************************************
 * $Log: FinanzamtControl.java,v $
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/16 02:27:32  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.1  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/