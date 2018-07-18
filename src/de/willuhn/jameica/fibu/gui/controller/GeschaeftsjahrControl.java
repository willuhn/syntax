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
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
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
    try
    {
      Geschaeftsjahr jahr = this.getGeschaeftsjahr();
      
      // Wir checken mal, ob es ein neues Objekt ist. Wenn ja, pruefen wir,
      // ob der Mandant bereits ein offenes Jahr hat. Ist das der Fall zeigen
      // wir einen Hinweis an, dass es besser waere, erst das Vorjahr zu schliessen
      if (jahr.isNewObject())
      {
        Mandant m = jahr.getMandant();
        if (m == null)
          throw new ApplicationException(i18n.tr("Kein Mandant ausgewählt"));
        
        DBIterator it = m.getGeschaeftsjahre();
        it.addFilter("(closed is NULL or closed = 0)");
        if (it.hasNext())
        {
          String q = i18n.tr("Es existiert ein noch offenes Geschäftsjahr, welches zuerst geschlossen werden sollte.\n" +
                             "Beim Schließen eines Geschäftsjahres werden automatisch die Abschreibungen gebucht,\n" +
                             "ein passendes Folgejahr erstellt und die Salden der Bestandskonten als Anfangsbestände\n" +
                             "in das neue Geschäftsjahr übernommen. Das manuelle Anlegen eines Geschäftsjahres ist\n" +
                             "daher meist nicht notwendig.\n\n" +
                             "Klicken Sie mit der rechten Maustaste auf das Geschäftsjahr (aktivieren Sie es ggf. vorher)\n" +
                             "und wählen Sie die Option \"Geschäftsjahr abschließen...\".\n\n" +
                             "Neues Geschäftsjahr dennoch speichern?");
          if (!Application.getCallback().askUser(q,true))
            return false;
        }
      }

      //////////////////////////////////////////////////////////////////////////
      // Kontenrahmen checken
      jahr.setKontenrahmen((Kontenrahmen) getKontenrahmenAuswahl().getValue());
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Geschaeftsjahr checken

      jahr.setBeginn((Date)getBeginn().getValue());
      jahr.setEnde((Date)getEnde().getValue());
      //
      //////////////////////////////////////////////////////////////////////////

      // und jetzt speichern wir.
      jahr.store();
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Geschäftsjahr gespeichert."),StatusBarMessage.TYPE_SUCCESS));
      return true;
    }
    catch (ApplicationException e1)
    {
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(e1.getMessage(),StatusBarMessage.TYPE_ERROR));
    }
    catch (Exception e)
    {
      Logger.error("unable to store gj",e);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Speichern des Geschäftsjahres."),StatusBarMessage.TYPE_ERROR));
    }
    return false;
  }
}
