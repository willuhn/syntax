/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExporter.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/29 12:17:29 $
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
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.Date;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung eines Exporters, welcher das Velocity-Framework nutzt.
 */
public class VelocityExporter
{

  private static File templateDir = null;
  private static I18N i18n        = null;
  
  static
  {
    try
    {
      // Velocity initialisieren
      Logger.info("init velocity template engine");
      Velocity.setProperty(Velocity.RESOURCE_LOADER,"fibu");
      Velocity.setProperty("fibu.resource.loader.description","Fibu Velocity Loader");
      Velocity.setProperty("fibu.resource.loader.class",VelocityLoader.class.getName());

      Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, new VelocityLogger());
      Velocity.init();

      i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

      PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
      templateDir = new File(res.getPath() + File.separator + "lib","velocity");
      Logger.info("velocity template dir: " + templateDir.getAbsolutePath());

    }
    catch (Throwable t)
    {
      Logger.error("velocity init failed",t);
    }
  }

  /**
   * Exportiert die Daten in den angegebenen OutputStream.
   * @param export der zu druckende Export.
   * @throws ApplicationException
   * @throws RemoteException
   */
  public static synchronized void export(Export export) throws ApplicationException, RemoteException
  {
    if (export == null || export.getTarget() == null)
      throw new ApplicationException(i18n.tr("Kein Ausgabe-Ziel für die Datei angegeben"));

    String template = export.getTemplate();
    if (template == null || template.length() == 0)
      throw new ApplicationException(i18n.tr("Kein Template angegeben"));

    Logger.debug("preparing velocity context");
    VelocityContext context = new VelocityContext();

    context.put("jahr",           Settings.getActiveGeschaeftsjahr());
    context.put("math",           new Math());
    context.put("datum",          new Date());
    context.put("dateformat",     Fibu.DATEFORMAT);
    context.put("longdateformat", Fibu.LONGDATEFORMAT);
    context.put("decimalformat",  Fibu.DECIMALFORMAT);
    context.put("export",         export);

    BufferedWriter writer = null;
    try
    {
      writer = new BufferedWriter(new OutputStreamWriter(export.getTarget()));

      Template t = Velocity.getTemplate("template.vm");
      t.merge(context,writer);
    }
    catch (Exception e)
    {
      Logger.error("error while writing into velocity file " + template,e);
      throw new ApplicationException(i18n.tr("Fehler beim Schreiben in die Export-Datei"));
    }
    finally
    {
      if (writer != null)
      {
        try
        {
          writer.close();
        }
        catch (Exception e)
        {
          Logger.error("error while closing outputstream",e);
        }
      }
    }
  }
}


/**********************************************************************
 * $Log: VelocityExporter.java,v $
 * Revision 1.4  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.3  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 * Revision 1.2  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.1  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/