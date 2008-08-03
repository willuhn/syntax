/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExporter.java,v $
 * $Revision: 1.8.2.1 $
 * $Date: 2008/08/03 23:02:47 $
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
import java.io.OutputStreamWriter;
import java.util.Date;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung eines Exporters, welcher das Velocity-Framework nutzt.
 */
public class VelocityExporter
{

  private static I18N i18n        = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * Exportiert die Daten in den angegebenen OutputStream.
   * @param export der zu druckende Export.
   * @throws ApplicationException
   */
  public static synchronized void export(Export export) throws ApplicationException
  {
    if (export == null || export.getTarget() == null)
      throw new ApplicationException(i18n.tr("Kein Ausgabe-Ziel für die Datei angegeben"));

    String template = export.getTemplate();
    if (template == null || template.length() == 0)
      throw new ApplicationException(i18n.tr("Kein Template angegeben"));

    Logger.debug("preparing velocity context");
    VelocityContext context = new VelocityContext();

    context.put("math",           new Math());
    context.put("datum",          new Date());
    context.put("dateformat",     Fibu.DATEFORMAT);
    context.put("longdateformat", Fibu.LONGDATEFORMAT);
    context.put("decimalformat",  Fibu.DECIMALFORMAT);
    context.put("export",         export);

    AbstractPlugin plugin = Application.getPluginLoader().getPlugin(Fibu.class);
    Manifest manifest = plugin.getManifest();
    String version = manifest.getName() + " " + manifest.getVersion() + " [Build: " + plugin.getManifest().getBuildnumber() + " - " + plugin.getManifest().getBuildDate() + "]";
    context.put("version",        version);

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
      throw new ApplicationException(i18n.tr("Fehler beim Erzeugen der Export-Datei: {0}",e.getMessage()));
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
 * Revision 1.8.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 * Revision 1.8  2006/10/07 19:34:23  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2006/01/02 17:38:16  willuhn
 * @N moved Velocity to Jameica
 *
 * Revision 1.6  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
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