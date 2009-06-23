/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/AbstractVelocityExport.java,v $
 * $Revision: 1.1.2.2 $
 * $Date: 2009/06/23 17:22:28 $
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
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung eines Exporters, welcher das Velocity-Framework nutzt.
 */
public abstract class AbstractVelocityExport extends AbstractExport
{
  final static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Liefert die Nutzdaten fuer den Export.
   * @param data die Export-Metadaten.
   * @return Nutzdaten fuer den Export.
   * @throws Exception
   */
  protected abstract VelocityExportData getData(ExportData data) throws Exception;
  
  /**
   * @see de.willuhn.jameica.fibu.io.Export#doExport(de.willuhn.jameica.fibu.io.ExportData, de.willuhn.util.ProgressMonitor)
   */
  public final void doExport(ExportData data, final ProgressMonitor monitor) throws ApplicationException, OperationCanceledException
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
        monitor.addPercentComplete(2);
      }
    };
    //////////////////////////////////////////////////////////////////////////

    Writer writer = null;
    try
    {
      timer.schedule(fakeProgress,0,200);

      VelocityExportData vData = getData(data);
      String template = vData.getTemplate();
      if (template == null || template.length() == 0)
        throw new ApplicationException(i18n.tr("Kein Template angegeben"));
      
      monitor.setStatusText(i18n.tr("Erstelle Auswertung"));
      VelocityContext context = new VelocityContext();

      context.put("math",           new Math());
      context.put("datum",          new Date());
      context.put("dateformat",     Fibu.DATEFORMAT);
      context.put("longdateformat", Fibu.LONGDATEFORMAT);
      context.put("decimalformat",  Fibu.DECIMALFORMAT);
      context.put("export",         vData);
      context.put("charset",        System.getProperty("file.encoding"));

      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target)));
      Template t = Velocity.getTemplate("template.vm","ISO-8859-15");
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
}


/**********************************************************************
 * $Log: AbstractVelocityExport.java,v $
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