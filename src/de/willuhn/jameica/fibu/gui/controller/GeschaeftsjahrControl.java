/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/GeschaeftsjahrControl.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/06/04 00:33:56 $
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
import java.util.Date;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.CalendarDialog;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuers Geschaeftsjahr.
 */
public class GeschaeftsjahrControl extends AbstractControl
{

  private DialogInput beginn             = null;
  private DialogInput ende               = null;
  private Input kontenrahmenAuswahl      = null;
  
  private Geschaeftsjahr jahr = null;

  private I18N i18n = null;
  
  /**
   * @param view
   */
  public GeschaeftsjahrControl(AbstractView view)
  {
    super(view);
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * Liefert das Geschaeftsjahr.
   * @return Geschaeftsjahr.
   * @throws RemoteException
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException
  {
    if (this.jahr != null)
      return this.jahr;
    
    this.jahr = (Geschaeftsjahr) getCurrentObject();
    if (this.jahr != null)
      return this.jahr;
    
    this.jahr = (Geschaeftsjahr) Settings.getDBService().createObject(Geschaeftsjahr.class,null);
    return this.jahr;
  }
  
  /**
   * Liefert das Eingabe-Feld fuer den Beginn des Geschaeftsjahres.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getBeginn() throws RemoteException
  {
    if (beginn != null)
      return beginn;
    
    Date start = getGeschaeftsjahr().getBeginn();
    CalendarDialog d = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    d.setTitle(i18n.tr("Beginn des Geschäftsjahres"));
    d.setText(i18n.tr("Bitte wählen Sie den Beginn des Geschäftsjahres"));
    d.setDate(start);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        beginn.setValue(event.data);
        beginn.setText(Settings.DATEFORMAT.format((Date)event.data));
      }
    });
    beginn = new DialogInput(Settings.DATEFORMAT.format(start),d);
    beginn.setValue(start);
    beginn.disableClientControl();
    beginn.setMandatory(true);
    return beginn;
  }

  /**
   * Liefert das Eingabe-Feld fuer das Ende des Geschaeftsjahres.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getEnde() throws RemoteException
  {
    if (ende != null)
      return ende;
    
    Date end = getGeschaeftsjahr().getEnde();
    CalendarDialog d = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    d.setTitle(i18n.tr("Ende des Geschäftsjahres"));
    d.setText(i18n.tr("Bitte wählen Sie das Ende des Geschäftsjahres"));
    d.setDate(end);
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        ende.setValue(event.data);
        ende.setText(Settings.DATEFORMAT.format((Date)event.data));
      }
    });
    ende = new DialogInput(Settings.DATEFORMAT.format(end),d);
    ende.setValue(end);
    ende.disableClientControl();
    ende.setMandatory(true);
    return ende;
  }

  /**
   * Liefert das Eingabe-Feld zur Auswahl des Kontenrahmens.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getKontenrahmenAuswahl() throws RemoteException
  {
    if (kontenrahmenAuswahl != null)
      return kontenrahmenAuswahl;

    kontenrahmenAuswahl = new SelectInput(Settings.getDBService().createList(Kontenrahmen.class),getGeschaeftsjahr().getKontenrahmen());
    kontenrahmenAuswahl.setMandatory(true);
    return kontenrahmenAuswahl;
  }

  /**
   * Speichert das Geschaeftsjahr.
   * @return true, wenn das Speichern erfolgreich war.
   */
  public boolean handleStore()
  {
    try {

      //////////////////////////////////////////////////////////////////////////
      // Kontenrahmen checken
      getGeschaeftsjahr().setKontenrahmen((Kontenrahmen) getKontenrahmenAuswahl().getValue());
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Geschaeftsjahr checken

      getGeschaeftsjahr().setBeginn((Date)getBeginn().getValue());
      getGeschaeftsjahr().setEnde((Date)getEnde().getValue());
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      getGeschaeftsjahr().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Geschäftsjahr gespeichert."));
      return true;
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (RemoteException e)
    {
      Logger.error("unable to store gj",e);
      GUI.getView().setErrorText("Fehler beim Speichern des Geschäftsjahres.");
    }
    return false;
  }


}


/*********************************************************************
 * $Log: GeschaeftsjahrControl.java,v $
 * Revision 1.7  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.6  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.5  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.3  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
 * Revision 1.2  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/