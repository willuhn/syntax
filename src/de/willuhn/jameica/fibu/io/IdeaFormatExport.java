/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/IdeaFormatExport.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/24 17:28:37 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.io.BufferedWriter;
import java.io.File;
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
import org.apache.velocity.app.Velocity;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.messaging.ExportMessage;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
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

  /**
   * @see de.willuhn.jameica.fibu.io.Export#doExport(de.willuhn.jameica.fibu.io.ExportData, de.willuhn.util.ProgressMonitor)
   */
  public final void doExport(ExportData data, final ProgressMonitor monitor) throws ApplicationException, OperationCanceledException
  {
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
        monitor.addPercentComplete(1);
      }
    };
    //////////////////////////////////////////////////////////////////////////

    Writer writer = null;
    try
    {
      monitor.setStatusText(i18n.tr("Erstelle Datenabzug"));

      timer.schedule(fakeProgress,0,100);

      //////////////////////////////////////////////////////////////////////////
      // Schritt 1: XML-Beschreibung des Exports erzeugen
      VelocityExportData vData = new VelocityExportData();
      vData.addObject("jahr", data.getGeschaeftsjahr());

      VelocityContext context = new VelocityContext();
      context.put("math",           new Math());
      context.put("datum",          new Date());
      context.put("dateformat",     Fibu.DATEFORMAT);
      context.put("longdateformat", Fibu.LONGDATEFORMAT);
      context.put("decimalformat",  Fibu.DECIMALFORMAT);
      context.put("export",         vData);
      context.put("charset",        System.getProperty("file.encoding"));


      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target)));
      Template t = Velocity.getTemplate("idea.xml.vm","ISO-8859-15");
      t.merge(context,writer);
      // TODO als "index.xml" in der ZIP-Datei speichern
      // TODO gdpdu-01-08-2002.dtd auch noch mit reinpacken
      // TODO RecordDelimiter (Zeilenumbruchszeichen) fehlt noch in idea.xml.vm
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Schritt 2: Zugehoerige CSV-Dateien mit den Nutzdaten erzeugen
      
      // TODO
      
      //
      //////////////////////////////////////////////////////////////////////////

      monitor.setStatus(ProgressMonitor.STATUS_DONE);
      monitor.setStatusText(i18n.tr("Datenabzug erstellt"));
      monitor.setPercentComplete(100);

      Application.getMessagingFactory().sendMessage(new ExportMessage(i18n.tr("IDEA-Datenabzug erstellt"),new File(target)));
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
      if (writer != null) {
        try { writer.close();}
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
  
  
}


/**********************************************************************
 * $Log: IdeaFormatExport.java,v $
 * Revision 1.1.2.1  2009/06/24 17:28:37  willuhn
 * *** empty log message ***
 *
 **********************************************************************/