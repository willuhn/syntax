/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/report/AbstractVelocityReport.java,v $
 * $Revision: 1.2 $
 * $Date: 2011/05/12 09:10:31 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import de.willuhn.io.IOUtil;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.services.VelocityService;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung eines Reports, welcher das Velocity-Framework nutzt.
 */
public abstract class AbstractVelocityReport extends AbstractReport
{
  final static String ENCODING_TEMPLATE = "ISO-8859-15";
  final static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Liefert die Nutzdaten fuer den Export.
   * @param data die Export-Metadaten.
   * @return Nutzdaten fuer den Export.
   * @throws Exception
   */
  protected abstract VelocityReportData getData(ReportData data) throws Exception;
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#doReport(de.willuhn.jameica.fibu.io.report.ReportData, de.willuhn.util.ProgressMonitor)
   */
  public final void doReport(ReportData data, final ProgressMonitor monitor) throws ApplicationException, OperationCanceledException
  {
    String target = data.getTarget();
    
    if (target == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Datei aus, in der die Auswertung gespeichert werden soll"));

    //////////////////////////////////////////////////////////////////////////
    // simulierter Fortschrittsbalken. Da wir bei Velocity keine Moeglichkeit
    // haben, einen echten Fortschritt zu errechnen, simulieren wir halt ;)
    final Timer timer = new Timer(true);
    final TimerTask fakeProgress = new TimerTask() {
      public void run()
      {
        if (monitor == null || monitor.getPercentComplete() == 100)
        {
          try
          {
            timer.cancel();
            return;
          }
          catch (Exception e)
          {
            // ignore
          }
        }
        if (monitor != null)
          monitor.addPercentComplete(1);
      }
    };
    //////////////////////////////////////////////////////////////////////////

    Writer writer = null;
    try
    {
      monitor.setStatusText(i18n.tr("Erstelle Auswertung"));

      timer.schedule(fakeProgress,0,100);

      VelocityReportData vData = getData(data);
      String template = vData.getTemplate();
      if (template == null || template.length() == 0)
        throw new ApplicationException(i18n.tr("Kein Template angegeben"));
      
      // Basis-Daten der User-Eingaben noch uebernehmen
      final Geschaeftsjahr jahr = data.getGeschaeftsjahr();
      final Date start = data.getStartDatum();
      final Date end   = data.getEndDatum(); 
      vData.addObject("jahr",       jahr);
      vData.addObject("start",      start != null ? start : jahr.getBeginn());
      vData.addObject("end",        end != null ? end : jahr.getEnde());
      
      vData.addObject("startkonto", data.getStartKonto());
      vData.addObject("endkonto",   data.getEndKonto());

      VelocityContext context = new VelocityContext();
      context.put("name",           this.getName());
      context.put("math",           new Math());
      context.put("datum",          new Date());
      context.put("dateformat",     Settings.DATEFORMAT);
      context.put("longdateformat", Settings.LONGDATEFORMAT);
      context.put("decimalformat",  Settings.DECIMALFORMAT);
      context.put("export",         vData);
      context.put("charset",        Settings.ENCODING_REPORTS);

      Manifest mf = Application.getPluginLoader().getPlugin(Fibu.class).getManifest();
      context.put("version","SynTAX " + mf.getVersion() + ", Jameica " + Application.getManifest().getVersion());


      Logger.info("creating report \"" + this.getName() + "\" (encoding " + Settings.ENCODING_REPORTS + ")");
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target),Settings.ENCODING_REPORTS));
      
      VelocityService service = (VelocityService) Application.getBootLoader().getBootable(VelocityService.class);
      VelocityEngine engine = service.getEngine(Fibu.class.getName());
      Template t = engine.getTemplate("template.vm",ENCODING_TEMPLATE); // Gelesen werden die Templates aber mit dem festen Encoding
      t.merge(context,writer);
      monitor.setStatus(ProgressMonitor.STATUS_DONE);
      monitor.setStatusText(i18n.tr("Auswertung erstellt"));
      monitor.setPercentComplete(100);
    }
    catch (OperationCanceledException oce)
    {
      throw oce;
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("error while creating export",e);
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Auswertung: {0}",e.getMessage()),e);
    }
    finally
    {
      IOUtil.close(writer);

      try {
        if (fakeProgress != null) fakeProgress.cancel();
        if (timer != null)        timer.cancel();
      }
      catch (Exception e) {}
    }
  }
}


/**********************************************************************
 * $Log: AbstractVelocityReport.java,v $
 * Revision 1.2  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.1  2010-08-27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
 * Revision 1.6  2010/07/19 09:08:08  willuhn
 * @N Versionsnummern mit in Auswertung schreiben
 *
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.4  2010/02/05 09:58:25  willuhn
 * @B Name der Auswertung wurde nicht angezeigt
 *
 * Revision 1.3  2009/08/24 11:56:47  willuhn
 * @N Umstellung auf neuen VelocityService - damit funktioniert SynTAX jetzt nur noch mit Jameica 1.9
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.4  2009/06/25 15:21:18  willuhn
 * @N weiterer Code fuer IDEA-Export
 *
 * Revision 1.1.2.3  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.1.2.2  2009/06/23 17:22:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 * Revision 1.8.2.4  2009/03/03 23:28:39  willuhn
 * @B Fehlende Encoding-Angabe im HTML-Export
 * @N HTML-Title mit Name des Exports
 *
 * Revision 1.8.2.3  2009/03/01 23:37:55  willuhn
 * @C Templates sollten explizit mit Latin1-Encoding gelesen werden, da sie von mir in diesem Encoding erstellt wurden
 *
 * Revision 1.8.2.2  2009/02/16 10:19:55  willuhn
 * @B Abfrage der Versionsnummer fuehrte zu einer Inkompatibilitaet zwischen Jameica 1.7 und Jameica 1.8 - Versionsnummer aus Report entfernt
 *
 * Revision 1.8.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 **********************************************************************/