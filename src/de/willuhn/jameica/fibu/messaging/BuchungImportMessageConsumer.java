/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.messaging;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

public class BuchungImportMessageConsumer implements MessageConsumer
{

  private I18N i18n;

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  @Override
  public Class[] getExpectedMessageTypes()
  {
    return new Class[] { QueryMessage.class };
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void handleMessage(Message message) throws Exception
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources()
        .getI18N();
    QueryMessage msg = (QueryMessage) message;

    ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) msg
        .getData();
    if(data.size() == 0)
    {
      return;
    }
    YesNoDialog d = new YesNoDialog(AbstractDialog.POSITION_CENTER);
    d.setTitle("Buchungen nach Syntax importieren");
    d.setText("Die Buchungen werden in das aktive Geschäftsjahr übernommen.\n"
        + "Jede Buchung wird auf das Konto mit der Nummer der \n"
        + "zugeordneten Buchungsart gebucht.\n"
        + "Das Gegenkonto wir aus der Nummer oder dem Kommentar des JVerein Kontos ermittelt.\n"
        + "Alle Buchungen, auch bereits vorhandene, werden übernommen.\n\n"
        + "Fortfahren?");
    if (!(boolean) d.open())
    {
      return;
    }
    Worker worker = new Worker(data);

    // Wenn wir mehr als 1 Buchung haben, fuehren wir das
    // im Hintergrund aus.
    if (data.size() > 1)
    {
      Application.getController().start(worker);
    }
    else
    {
      worker.run(null);
    }
    Application.getController().start(worker);
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  @Override
  public boolean autoRegister()
  {
    return false;
  }

  private class Worker implements BackgroundTask
  {
    private boolean cancel = false;

    private ArrayList<HashMap<String, Object>> list = null;

    /**
     * ct.
     * 
     * @param jBuchungen
     */
    private Worker(ArrayList<HashMap<String, Object>> data)
    {
      list = data;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
     */
    @Override
    public void interrupt()
    {
      cancel = true;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
     */
    @Override
    public boolean isInterrupted()
    {
      return cancel;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
     */
    @Override
    public void run(ProgressMonitor monitor) throws ApplicationException
    {
      try
      {
        int size = list.size();

        if (monitor != null)
        {
          monitor.setStatusText(
              i18n.tr("Buche {0} Umsätze", Integer.toString(size)));
        }

        double factor = 100d / size;

        int created = 0;
        int error = 0;
        int i = 0;

        for (HashMap<String, Object> map : list)
        {
          if (monitor != null)
          {
            monitor.setPercentComplete((int) ((++i) * factor));
            monitor.log(
                "  " + i18n.tr("Erstelle Buchung {0}", Integer.toString(i)));
          }

          Buchung buchung = null;
          try
          {
            buchung = createBuchung(map);
            buchung.store();

            created++;
          }
          catch (ApplicationException ae)
          {
            // Wenn wir nur eine Buchung hatten und eine ApplicationException,
            // dann fehlen noch Eingaben Da wir nur eine Buchung haben, oeffnen
            // wir die Erfassungsmaske.
            if (size == 1)
            {
              Application.getMessagingFactory()
                  .sendMessage(new StatusBarMessage(ae.getMessage(),
                      StatusBarMessage.TYPE_ERROR));
              new BuchungNeu().handleAction(buchung);
              return;
            }

            if (monitor != null)
            {
              monitor.log("    " + ae.getMessage());
            }
            error++;
          }
          catch (Exception e)
          {
            Logger.error("unable to import umsatz", e);
            if (monitor != null)
            {
              monitor.log("    " + i18n.tr("Fehler: {0}", e.getMessage()));
            }
            error++;
          }
        }

        String text = i18n.tr("Umsatz importiert");
        if (size > 1)
        {
          text = i18n.tr("{0} Umsätze importiert, {1} fehlerhaft",
              Integer.toString(created), Integer.toString(error));
        }

        Application.getMessagingFactory().sendMessage(
            new StatusBarMessage(text, StatusBarMessage.TYPE_SUCCESS));
        if (monitor != null)
        {
          monitor.setStatusText(text);
          monitor.setStatus(ProgressMonitor.STATUS_DONE);
        }

      }
      catch (Exception e)
      {
        Logger.error("error while importing objects", e);
        throw new ApplicationException(
            i18n.tr("Fehler beim Import der Umsätze"));
      }
    }

    private Buchung createBuchung(HashMap<String, Object> map)
        throws ApplicationException, RemoteException
    {

      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();

      int buchungsartNummer = (int) map.get("buchungsartNummer");

      DBIterator<Konto> kontoIt = jahr.getKontenrahmen().getKonten();
      kontoIt.addFilter("kontonummer = " + Integer.toString(buchungsartNummer));
      if (!kontoIt.hasNext())
      {
        throw new ApplicationException(
            i18n.tr("Kein Konto zu Buchungsart-Nummer {0} gefunden",
                Integer.toString(buchungsartNummer)));
      }
      Konto konto = kontoIt.next();

      int kontoNummer = Integer.parseInt((String) map.get("kontoNummer"));

      DBIterator<Konto> gegenkontoIt = jahr.getKontenrahmen().getKonten();
      gegenkontoIt.addFilter("kontonummer = " + Integer.toString(kontoNummer));
      if (!gegenkontoIt.hasNext())
      {
        // Eventuell steht die nummer im KontoKommentag

        kontoNummer = (int) map.get("kontoKommentar");

        gegenkontoIt = jahr.getKontenrahmen().getKonten();
        gegenkontoIt
            .addFilter("kontonummer = " + Integer.toString(kontoNummer));
        if (!gegenkontoIt.hasNext())
        {
          throw new ApplicationException(
              i18n.tr("Kein Konto zu Konto-Nummer {0} gefunden",
                  Integer.toString(kontoNummer)));
        }
      }
      Konto gegenkonto = gegenkontoIt.next();

      final Buchung buchung = (Buchung) Settings.getDBService()
          .createObject(Buchung.class, null);
      buchung.setGeschaeftsjahr(jahr);
      buchung.setBelegnummer(buchung.getBelegnummer()); // Das erzeugt eine neue
                                                        // Belegnummer
      buchung.setKommentar((String) map.get("kommentar"));

      buchung.setText((String) map.get("zweck"));

      if (konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_ERLOES
          && (double) map.get("betrag") >= 0.01d)
      {
        buchung.setSollKonto(gegenkonto);
        buchung.setHabenKonto(konto);
      }
      else
      {
        buchung.setSollKonto(konto);
        buchung.setHabenKonto(gegenkonto);
      }

      Steuer s = null;
      if (konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_ERLOES
          || konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_AUFWAND)
      {
        s = konto.getSteuer();
      }
      else if (gegenkonto.getKontoArt()
          .getKontoArt() == Kontoart.KONTOART_ERLOES
          || gegenkonto.getKontoArt()
              .getKontoArt() == Kontoart.KONTOART_AUFWAND)
      {
        s = gegenkonto.getSteuer();
      }
      buchung.setSteuerObject(s);

      double satz = 0;
      if (s != null)
      {
        satz = s.getSatz();
      }
      buchung.setSteuer(satz);

      double betrag = java.lang.Math.abs((double) map.get("betrag"));

      // Netto und Brutto setzen
      buchung.setBruttoBetrag(betrag);
      buchung.setBetrag(new Math().netto(betrag, satz));

      Date date = (Date) map.get("datum");
      if (date != null)
      {
        buchung.setDatum(date);
      }

      return buchung;
    }
  }
}
