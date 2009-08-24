/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/IdeaFormatExport.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/08/24 11:57:41 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeXmlReference;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.idea.AbschreibungTable;
import de.willuhn.jameica.fibu.io.idea.AnfangsbestandTable;
import de.willuhn.jameica.fibu.io.idea.AnlagevermoegenTable;
import de.willuhn.jameica.fibu.io.idea.BuchungTable;
import de.willuhn.jameica.fibu.io.idea.KontoTable;
import de.willuhn.jameica.fibu.io.idea.KontoartTable;
import de.willuhn.jameica.fibu.io.idea.KontotypTable;
import de.willuhn.jameica.fibu.io.idea.SteuerTable;
import de.willuhn.jameica.fibu.io.idea.Table;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.services.VelocityService;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.system.Platform;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung eines Exporters, welcher einen Datenabzug gemaess GDPdU erzeugt.
 * Siehe http://de.wikipedia.org/wiki/GDPdU und
 * http://www.audicon.net/downloads/dokumentationen/index.php
 */
public class IdeaFormatExport extends AbstractExport
{
  private final static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private final static Map<Integer,String> recordDelimiter = new HashMap();
  static
  {
    recordDelimiter.put(Platform.OS_LINUX,      "&#10;");
    recordDelimiter.put(Platform.OS_LINUX_64,   "&#10;");
    recordDelimiter.put(Platform.OS_MAC,        "&#13;");
    recordDelimiter.put(Platform.OS_WINDOWS,    "&#13;&#10;");
    recordDelimiter.put(Platform.OS_WINDOWS_64, "&#13;&#10;");
    recordDelimiter.put(Platform.OS_UNKNOWN,    "&#13;&#10;");
  }
  
  private ProgressMonitor monitor = null;
  private ZipOutputStream os      = null;
  private Geschaeftsjahr jahr     = null;
  

  /**
   * @see de.willuhn.jameica.fibu.io.Export#doExport(de.willuhn.jameica.fibu.io.ExportData, de.willuhn.util.ProgressMonitor)
   */
  public final void doExport(ExportData data, ProgressMonitor monitor) throws ApplicationException, OperationCanceledException
  {
    this.monitor = monitor;
    
    String target = data.getTarget();
    
    if (target == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Datei aus, in der der IDEA-Datenabzug gespeichert werden soll"));

    //////////////////////////////////////////////////////////////////////////
    // simulierter Fortschrittsbalken. Da wir bei Velocity keine Moeglichkeit
    // haben, einen echten Fortschritt zu errechnen, simulieren wir halt ;)
    final Timer timer = new Timer(true);
    final TimerTask fakeProgress = new TimerTask() {
      public void run()
      {
        if (IdeaFormatExport.this.monitor == null || IdeaFormatExport.this.monitor.getPercentComplete() == 100)
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
        IdeaFormatExport.this.monitor.addPercentComplete(1);
      }
    };
    //////////////////////////////////////////////////////////////////////////

    try
    {
      this.monitor.setStatusText(i18n.tr("Erstelle Datenabzug"));

      timer.schedule(fakeProgress,0,100);

      this.jahr = data.getGeschaeftsjahr();
      this.os = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
      
      //////////////////////////////////////////////////////////////////////////
      // Schritt 1: XML-Beschreibung des Exports erzeugen
      VelocityExportData vData = new VelocityExportData();
      vData.addObject("jahr", jahr);

      VelocityContext context = new VelocityContext();
      context.put("math",           new Math());
      context.put("datum",          new Date());
      context.put("dateformat",     Fibu.DATEFORMAT);
      context.put("longdateformat", Fibu.LONGDATEFORMAT);
      context.put("decimalformat",  Fibu.DECIMALFORMAT);
      context.put("export",         vData);
      context.put("charset",        System.getProperty("file.encoding"));
      context.put("lineseparator",  recordDelimiter.get(Application.getPlatform().getOS()));

      // XML-Escaping
      EventCartridge ec = new EventCartridge();
      EscapeXmlReference ex = new EscapeXmlReference()
      {
        /**
         * @see org.apache.velocity.app.event.implement.EscapeXmlReference#escape(java.lang.Object)
         */
        protected String escape(Object text)
        {
          if (text == null)
            return null;
          String s = text.toString();
          if (s.startsWith("&#")) // ist schon escaped
            return s;
          return super.escape(s);
        }
        
      };
      ec.addEventHandler(ex);
      ec.attachToContext(context);

      VelocityService service = (VelocityService) Application.getBootLoader().getBootable(VelocityService.class);
      VelocityEngine engine = service.getEngine(Fibu.class.getName());
      if (engine == null)
        throw new Exception("velocity engine not found");

      StringWriter writer = new StringWriter();
      Template t = engine.getTemplate("idea.xml.vm","ISO-8859-15");
      t.merge(context,writer);
      add("index.xml",new ByteArrayInputStream(writer.toString().getBytes()));
      
      //////////////////////////////////////////////////////////////////////////
      // Schritt 2: gdpdu-01-08-2002.dtd
      add("gdpdu-01-08-2002.dtd",Application.getPluginLoader().getPlugin(Fibu.class).getResources().getClassLoader().getResourceAsStream("res/gdpdu-01-08-2002.dtd"));
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Schritt 3: Zugehoerige CSV-Dateien mit den Nutzdaten erzeugen
      
      // TODO
      add("steuer.csv",          new SteuerTable());
      add("kontoart.csv",        new KontoartTable());
      add("kontotyp.csv",        new KontotypTable());
      add("konto.csv",           new KontoTable());
      add("konto_ab.csv",        new AnfangsbestandTable());
      add("buchung.csv",         new BuchungTable());
      add("anlagevermoegen.csv", new AnlagevermoegenTable());
      add("abschreibung.csv",    new AbschreibungTable());
      //
      //////////////////////////////////////////////////////////////////////////

      this.monitor.setStatus(ProgressMonitor.STATUS_DONE);
      this.monitor.setStatusText(i18n.tr("Datenabzug erstellt"));
      this.monitor.setPercentComplete(100);
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
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen des IDEA-Datenabzugs: {0}",e.getMessage()),e);
    }
    finally
    {
      if (this.os != null) {
        try { this.os.close();}
        catch (Exception e){Logger.error("error while closing outputstream",e);}
      }

      try {
        if (fakeProgress != null) fakeProgress.cancel();
        if (timer != null)        timer.cancel();
      }
      catch (Exception e) {}
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-idea.zip",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("IDEA-Datenabzug (GDPdU)");
  }

  /**
   * Fuegt eine neue Datei zur ZIP-Datei hinzu.
   * @param name Name der Datei.
   * @param is Datenquelle.
   * @throws IOException
   */
  private void add(String name, InputStream is) throws IOException
  {
    try
    {
      this.monitor.log(name);
      this.os.putNextEntry(new ZipEntry(name));

      byte b[] = new byte[4096];
      int read = 0;
      while ((read = is.read(b)) >= 0)
      {
        if (read > 0) // Nur schreiben, wenn wirklich was gelesen wurde
          this.os.write(b,0,read);
      }
      this.os.closeEntry();
    }
    finally
    {
      is.close();
    }
  }
  
  /**
   * Fuegt eine Tabelle zur CSV-Datei hinzu.
   * @param name Name der Datei.
   * @param table die Tabelle.
   * @throws Exception
   */
  private void add(String name, Table table) throws Exception
  {
    this.monitor.log(name);
    
    List<List<String>> lines = table.getLines(this.jahr);

    this.os.putNextEntry(new ZipEntry(name));
    for (List<String> line:lines)
    {
      StringBuffer sb = new StringBuffer();
      for (int i=0;i<line.size();++i)
      {
        // Quoting mittels >"< habe ich weggelassen, weil in der Spec.
        // Beschreibungsstandard-GDPdU-01-08-2002.pdf nicht beschrieben
        // ist, wie das Zeichen innerhalb eines Wertes escaped werden
        // muss, falls es da auftritt. Ich wuerde Backslash >\< vermuten.
        // Steht da aber nicht ausdruecklich.
        sb.append(line.get(i));
        if (i+1<line.size())
          sb.append(";");
      }
      sb.append(System.getProperty("line.separator"));
      this.os.write(sb.toString().getBytes());
    }
    this.os.closeEntry();
  }

  
}


/**********************************************************************
 * $Log: IdeaFormatExport.java,v $
 * Revision 1.3  2009/08/24 11:57:41  willuhn
 * @N Umstellung auf neuen VelocityService
 *
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.5  2009/06/30 16:00:59  willuhn
 * @N Erste komplette Version des Exports
 *
 * Revision 1.1.2.4  2009/06/26 13:56:56  willuhn
 * @N IDEA-Export (Buchungen und Anfangsbestaende)
 *
 * Revision 1.1.2.3  2009/06/25 16:33:17  willuhn
 * @N Erste CSV-Daten fuer Steuer, Kontoart, Kontotyp und Konto
 *
 * Revision 1.1.2.2  2009/06/25 15:21:18  willuhn
 * @N weiterer Code fuer IDEA-Export
 *
 * Revision 1.1.2.1  2009/06/24 17:28:37  willuhn
 * *** empty log message ***
 *
 **********************************************************************/