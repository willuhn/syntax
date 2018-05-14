/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Level;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuers Geschaeftsjahr.
 */
public class GeschaeftsjahrControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  private DateInput beginn             = null;
  private DateInput ende               = null;
  private Input kontenrahmenAuswahl    = null;
  
  private Geschaeftsjahr jahr = null;

  /**
   * @param view
   */
  public GeschaeftsjahrControl(AbstractView view)
  {
    super(view);
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
    
    this.beginn = new DateInput(this.getGeschaeftsjahr().getBeginn());
    this.beginn.setTitle(i18n.tr("Beginn des Geschäftsjahres"));
    this.beginn.setText(i18n.tr("Bitte wählen Sie den Beginn des Geschäftsjahres"));
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
    
    this.ende = new DateInput(this.getGeschaeftsjahr().getEnde());
    this.ende.setTitle(i18n.tr("Ende des Geschäftsjahres"));
    this.ende.setText(i18n.tr("Bitte wählen Sie das Ende des Geschäftsjahres"));
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

    DBIterator list = Settings.getDBService().createList(Kontenrahmen.class);
    String id = null;
    try
    {
      id = this.getGeschaeftsjahr().getMandant().getID();
    }
    catch (Exception e)
    {
      Logger.write(Level.DEBUG,"currently no mandant available",e);
    }
    if (id != null)
      list.addFilter("mandant_id is null or mandant_id = " + id);
    else
      list.addFilter("mandant_id is null");
    
    list.setOrder("order by name");
    kontenrahmenAuswahl = new SelectInput(list,getGeschaeftsjahr().getKontenrahmen());
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
 * Revision 1.9  2012/01/29 22:09:14  willuhn
 * @N Neues DateInput verwenden statt manuellen CalendarDialog (mit DialogInput)
 *
 * Revision 1.8  2011-03-21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 * Revision 1.7  2010-06-04 00:33:56  willuhn
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