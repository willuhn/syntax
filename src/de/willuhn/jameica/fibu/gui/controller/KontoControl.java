/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/KontoControl.java,v $
 * $Revision: 1.7 $
 * $Date: 2004/01/27 00:09:10 $
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

import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.jameica.Application;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.views.KontoListe;
import de.willuhn.jameica.fibu.gui.views.KontoNeu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

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
    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.OK);
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
    GUI.startView(KontoListe.class.getName(),null);
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
      String s = getField("steuer").getValue();
      Steuer steuer = null;
      if (s != null)
        steuer = (Steuer) Settings.getDatabase().createObject(Steuer.class,s);

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
			Application.getLog().error("unable to store konto",e);
      GUI.setActionText("Fehler beim Speichern des Kontos.");
    }
    
  }

  /**
   * @see de.willuhn.jameica.views.parts.Controller#handleChooseFromList(java.lang.String)
   */
  public void handleLoad(String id)
  {
    try {
      Konto konto = (Konto) Settings.getDatabase().createObject(Konto.class,id);
      GUI.startView(KontoNeu.class.getName(),konto);
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
    MessageBox box = new MessageBox(GUI.getShell(),SWT.ICON_WARNING | SWT.OK);
    box.setText(I18N.tr("Fehler"));
    box.setMessage(I18N.tr("Neue Konten können nicht angelegt werden."));
    box.open();
    return;
  }

}

/*********************************************************************
 * $Log: KontoControl.java,v $
 * Revision 1.7  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
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
 * Revision 1.2  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/