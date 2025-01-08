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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.fibu.util.NumberUtil;
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

/**
 * Ermöglicht den Import von Buchungen per Messaging durch andere Plugins.
 */
public class BuchungImportMessageConsumer implements MessageConsumer
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

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
  public void handleMessage(Message message) throws Exception
  {
    final QueryMessage msg = (QueryMessage) message;

    final List<Map<String, Object>> data = (List<Map<String, Object>>) msg.getData();
    
    if(data.isEmpty())
    {
      Logger.warn("message did not contain any bookings");
      return;
    }
    
    final YesNoDialog d = new YesNoDialog(AbstractDialog.POSITION_CENTER);
    d.setTitle("Buchungen nach Syntax importieren");
    d.setText("Die Buchungen werden in das aktive Geschäftsjahr übernommen.\n"
        + "Jede Buchung wird auf das Konto mit der Nummer der \n"
        + "zugeordneten Buchungsart gebucht.\n"
        + "Das Gegenkonto wir aus der Nummer oder dem Kommentar des Kontos ermittelt.\n"
        + "Alle Buchungen, auch bereits vorhandene, werden übernommen.\n\n"
        + "Fortfahren?");
    
    if (!(boolean) d.open())
      return;
    
    final Worker worker = new Worker(data);

    // Wenn wir mehr als 1 Buchung haben, fuehren wir das
    // im Hintergrund aus.
    if (data.size() > 1)
      Application.getController().start(worker);
    else
      worker.run(null);
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  @Override
  public boolean autoRegister()
  {
    return false;
  }

  /**
   * Der eigentliche Worker.
   */
  private class Worker implements BackgroundTask
  {
    private boolean cancel = false;

    private List<Map<String, Object>> list = null;

    /**
     * ct.
     * @param data die Liste der Buchungen.
     */
    private Worker(List<Map<String, Object>> data)
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
          monitor.setStatusText(i18n.tr("Erstelle {0} Buchungen", Integer.toString(size)));

        double factor = 100d / size;

        int created = 0;
        int error = 0;
        int i = 0;

        for (Map<String, Object> map : list)
        {
          if (monitor != null)
          {
            monitor.setPercentComplete((int) ((++i) * factor));
            monitor.log("  " + i18n.tr("Erstelle Buchung {0}", Integer.toString(i)));
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
            // dann fehlen noch Eingaben. Da wir nur eine Buchung haben, oeffnen
            // wir die Erfassungsmaske.
            if (size == 1)
            {
              Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
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
            Logger.error("unable to import booking", e);
            if (monitor != null)
              monitor.log("    " + i18n.tr("Fehler: {0}", e.getMessage()));
            
            error++;
          }
        }

        String text = i18n.tr("Buchung erstellt");
        if (size > 1)
          text = i18n.tr("{0} Buchungen erfolgreich erstellt, {1} fehlerhaft",Integer.toString(created), Integer.toString(error));

        Application.getMessagingFactory().sendMessage(new StatusBarMessage(text, StatusBarMessage.TYPE_SUCCESS));
        
        if (monitor != null)
        {
          monitor.setStatusText(text);
          monitor.setStatus(ProgressMonitor.STATUS_DONE);
        }
      }
      catch (Exception e)
      {
        Logger.error("error while importing objects", e);
        throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Buchungen"));
      }
    }

    /**
     * Erstellt die Buchung basierend auf den Daten aus der Map.
     * @param map die Map mit den Daten der Buchung.
     * @return die erstellte aber noch nicht gespeicherte Buchung.
     * @throws ApplicationException
     * @throws RemoteException
     */
    private Buchung createBuchung(Map<String, Object> map) throws ApplicationException, RemoteException
    {
      final Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      final Kontenrahmen kr = jahr.getKontenrahmen();

      // Je nachdem, welche Properties angegeben sind, verwenden wir entweder buchungsartkonto/gegenkonto oder sollkonto/habenkonto
      final Object buchungsart = map.get("buchungsartkonto");
      final Object gegenkonto  = map.get("gegenkonto");
      final Object sollkonto   = map.get("sollkonto");
      final Object habenkonto  = map.get("habenkonto");
      
      boolean autodetect = (buchungsart != null && gegenkonto != null);
      if (!autodetect && (sollkonto == null || habenkonto == null)) // wenn buchungsart/gegenkonto nicht angegeben sind, müssen sollkonto/habenkonto angegeben sein
        throw new ApplicationException(i18n.tr("Zu verwendende Soll-/Habenkonten unvollständig"));
      
      Konto k1 = this.getKonto(kr,autodetect ? buchungsart : sollkonto);
      Konto k2 = this.getKonto(kr,autodetect ? gegenkonto : habenkonto);
      
      final Buchung buchung = (Buchung) Settings.getDBService().createObject(Buchung.class, null);
      buchung.setGeschaeftsjahr(jahr);
      buchung.setBelegnummer(buchung.getBelegnummer()); // Das erzeugt eine neue Belegnummer
      buchung.setKommentar((String) map.get("kommentar"));
      buchung.setText((String) map.get("zweck"));
      buchung.setDatum(this.parseDate(map.get("datum")));
      
      final BigDecimal betrag = NumberUtil.parse(map.get("betrag"));
      if (betrag == null)
        throw new ApplicationException(i18n.tr("Kein Betrag angegeben"));
      
      final double d = betrag.setScale(2, RoundingMode.HALF_UP).doubleValue();
      final int k1Art = k1.getKontoArt().getKontoArt();
      
      if (autodetect)
      {
        // automatisch ermitteln
        final boolean k1haben = (k1Art == Kontoart.KONTOART_ERLOES && d >= 0.01d);
        buchung.setSollKonto(k1haben ? k2 : k1);
        buchung.setHabenKonto(k1haben ? k1 : k2);
      }
      else
      {
        // Fest vorgegeben
        buchung.setSollKonto(k1);
        buchung.setHabenKonto(k2);
      }
      
      // Steuer nehmen wir von dem Konto, welches als Erlös- oder Aufwandskonto definiert ist
      final Steuer s = (k1Art == Kontoart.KONTOART_ERLOES || k1Art == Kontoart.KONTOART_AUFWAND) ? k1.getSteuer() : k2.getSteuer();
      final double satz = (s != null ? s.getSatz() : 0.0d);
      buchung.setSteuerObject(s);
      buchung.setSteuer(satz);

      // Netto und Brutto setzen
      buchung.setBruttoBetrag(d);
      buchung.setBetrag(new Math().netto(d, satz));

      return buchung;
    }
    
    /**
     * Parst das Datum fehlertolerant.
     * @param o das Datum.
     * @return das geparste Datum oder NULL, wenn keines ermittelbar war.
     */
    private Date parseDate(Object o)
    {
      if (o == null)
        return null;
      
      if (o instanceof Date)
        return (Date) o;
      
      try
      {
        return Settings.CUSTOM_DATEFORMAT.parse(o.toString());
      }
      catch (Exception e)
      {
        Logger.error("unknown date format: " + o);
      }
      return null;
    }
    
    /**
     * Liefert das Konto mit der angebenen Kontonummeer.
     * @param kr der Kontenrahmen.
     * @param nummer die Kontonummer.
     * @return das Konto.
     * @throws RemoteException
     * @throws ApplicationException
     */
    private Konto getKonto(Kontenrahmen kr, Object nummer) throws RemoteException, ApplicationException
    {
      final DBIterator<Konto> it = kr.getKonten();
      it.addFilter("kontonummer = ?",nummer);
      
      if (!it.hasNext())
        throw new ApplicationException(i18n.tr("Kein Konto zu Kontonummer {0} gefunden",nummer.toString()));
      
      return it.next();
    }
  }
}
