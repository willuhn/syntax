/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/SteuerControl.java,v $
 * $Revision: 1.4 $
 * $Date: 2003/12/12 01:28:07 $
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
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.jameica.*;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.views.SteuerListe;
import de.willuhn.jameica.fibu.gui.views.SteuerNeu;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.parts.Controller;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse behandelt alle Button-Drueckungen(sic!) ;) des
 * Dialogs "Steuern".
 * @author willuhn
 */
public class SteuerControl extends Controller
{

  /**
   * Erzeugt einen neuen Controller der fuer diesen Steuersatz zustaendig ist.
   * @param object der Steuersatz.
   */
  public SteuerControl(DBObject object)
  {
    super(object);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete(java.lang.String)
   */
  public void handleDelete(String id)
  {
    try {
      this.object = Application.getDefaultDatabase().createObject(Steuer.class,id);
      handleDelete();
    }
    catch (RemoteException e)
    {
      // Objekt kann nicht geladen werden. Dann muessen wir es auch nicht loeschen.
      Application.getLog().error("no valid steuer found");
      GUI.setActionText(I18N.tr("Steuersatz wurde nicht gefunden."));
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleDelete()
   */
  public void handleDelete()
  {
    Steuer steuer = (Steuer) getObject();

    try {

      if (steuer.isNewObject())
      {
        GUI.setActionText(I18N.tr("Steuersatz wurde noch nicht gespeichert und muss daher nicht gelöscht werden."));
        return; // wenn's ein neues Objekt ist, gibt's nichts zu loeschen. ;)
      }

      MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.YES | SWT.NO);
      box.setText(I18N.tr("Steuersatz wirklich löschen?"));
      box.setMessage(I18N.tr("Wollen Sie diesen Steuersatz wirklich löschen?"));
      if (box.open() != SWT.YES)
        return;

      // ok, wir loeschen das Objekt
      steuer.delete();
      GUI.setActionText(I18N.tr("Steuersatz gelöscht."));
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
      GUI.setActionText(I18N.tr("Fehler beim Löschen des Steuersatzes."));
      Application.getLog().error("unable to delete steuer");
    }
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCancel()
   */
  public void handleCancel()
  {
    GUI.startView(SteuerListe.class.getName(),null);
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleStore()
   */
  public void handleStore()
  {
    Steuer steuer = (Steuer) getObject();

    try {

      steuer.setName(getField("name").getValue());

      //////////////////////////////////////////////////////////////////////////
      // Steuersatz checken
      try {
        steuer.setSatz(Fibu.DECIMALFORMAT.parse(getField("satz").getValue()).doubleValue());
      }
      catch (NumberFormatException e)
      {
        GUI.setActionText(I18N.tr("Steuersatz ungültig."));
        return;
      }
      catch (ParseException e)
      {
        GUI.setActionText(I18N.tr("Steuersatz ungültig."));
        return;
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Steuerkonto checken
      
      DBIterator steuerkonten = Application.getDefaultDatabase().createList(SteuerKonto.class);
      steuerkonten.addFilter("kontonummer="+getField("steuerkonto").getValue());
      if (!steuerkonten.hasNext())
      {
        GUI.setActionText(I18N.tr("Ausgewähltes Steuerkonto existiert nicht."));
        return;
      }
      steuer.setSteuerKonto((SteuerKonto) steuerkonten.next());
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      steuer.store();
      GUI.setActionText(I18N.tr("Steuersatz gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.setActionText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      GUI.setActionText("Fehler beim Speichern des Steuersatzes.");
      Application.getLog().error("unable to store steuer");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Steuer steuer = (Steuer) Application.getDefaultDatabase().createObject(Steuer.class,id);
      GUI.startView(SteuerNeu.class.getName(),steuer);
    }
    catch (RemoteException e)
    {
      Application.getLog().error("unable to load steuer with id " + id);
      GUI.setActionText(I18N.tr("Steuersatz wurde nicht gefunden."));
    }
        
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleCreate()
   */
  public void handleCreate()
  {
    GUI.startView(SteuerNeu.class.getName(),null);
  }

}

/*********************************************************************
 * $Log: SteuerControl.java,v $
 * Revision 1.4  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/