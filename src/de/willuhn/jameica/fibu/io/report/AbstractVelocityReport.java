/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.io.IOUtil;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
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
  
  /**
   * Liefert die Liste der Konten basierend auf den Report-Daten.
   * @param data die Report-Daten.
   * @return die Liste der Konten.
   * @throws RemoteException
   */
  protected List<Konto> getKonten(ReportData data) throws RemoteException
  {
    final Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    DBIterator<Konto> konten   = jahr.getKontenrahmen().getKonten();
    konten.setOrder(Settings.getAccountOrder());
    final Integer start = parse(data.getStartKonto() != null ? data.getStartKonto().getKontonummer() : null);
    final Integer end   = parse(data.getEndKonto() != null ? data.getEndKonto().getKontonummer() : null);

    final List<Konto> result = new ArrayList<>();

    while (konten.hasNext())
    {
      final Konto k = konten.next();
      final Integer i = parse(k.getKontonummer());
      if (i == null)
      {
        // Keine numerische Kontonummer
        result.add(k);
        continue;
      }
      
      if (start != null && i.intValue() < start.intValue())
        continue;
      
      if (end != null && i.intValue() > end.intValue())
        continue;
      
      result.add(k);
    }
    
    return result;
  }
  
  /**
   * Parst den Text fehlertolerant als Zahl.
   * @param s der Text.
   * @return die Zahl oder NULL, wenn es nicht als Zahl lesbar war.
   */
  private Integer parse(String s)
  {
    if (s == null)
      return null;
    
    try
    {
      return Integer.parseInt(s);
    }
    catch (Exception e)
    {
      // Ist zulässig bei alphanumerischen Kontonummern.
    }
    
    return null;
  }
}
