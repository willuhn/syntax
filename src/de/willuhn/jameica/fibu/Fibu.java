/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Fibu.java,v $
 * $Revision: 1.17 $
 * $Date: 2005/08/08 21:35:46 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;

import java.io.File;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.willuhn.datasource.db.EmbeddedDatabase;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Basisklasse des Fibu-Plugins fuer das Jameica-Framework.
 * @author willuhn
 */
public class Fibu extends AbstractPlugin
{

  /**
   * @param file
   */
  public Fibu(File file)
  {
    super(file);
  }

  /**
   * Dateformatter.
   */
  public static DateFormat DATEFORMAT       = new SimpleDateFormat("dd.MM.yyyy");
  
  /**
   * Dateformatter fuer Kurz-Format.
   */
  public static DateFormat FASTDATEFORMAT   = new SimpleDateFormat("ddMMyyyy");
  
  /**
   * Dezimal-Formatter.
   */
  public static DecimalFormat DECIMALFORMAT = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
  
  public static String[] WEEKDAYS = new String[] {
    "Sonntag",
    "Montag",
    "Dienstag",
    "Mittwoch",
    "Donnerstag",
    "Freitag",
    "Sonnabend"
  };

  public static int YEAR_MIN                = 1950;
  public static int YEAR_MAX                = 2020;

  static {
    DECIMALFORMAT.applyPattern("#0.00");
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  public void init() throws ApplicationException
  {
    try
    {
      Settings.getDatabase();
    }
    catch (RemoteException e)
    {
      Logger.error("error while loading db service",e);
      I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
      throw new ApplicationException(i18n.tr("Fehler beim Laden der Datenbank"));
    }
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#shutDown()
   */
  public void shutDown()
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#install()
   */
  public void install() throws ApplicationException
  {
    PluginResources res = Application.getPluginLoader().getPlugin(Fibu.class).getResources();
    I18N i18n = res.getI18N();

    String dir = res.getWorkPath() + "/db/db.conf";
    try
    {
      EmbeddedDatabase db = new EmbeddedDatabase(dir,"fibu","fibu");
      if (!db.exists())
      {
        db.create();
        db.executeSQLScript(new File(res.getPath() + "/sql/create.sql"));
        db.executeSQLScript(new File(res.getPath() + "/sql/init.sql"));
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to create sql tables",e);
      throw new ApplicationException(i18n.tr("Fehler beim Installieren des Fibu-Plugins"));
    }

    String welcome = i18n.tr("Fibu f�r Jameica");
    welcome += "\n";
    welcome += 
      i18n.tr("Beachten Sie bitte folgende erste Schritte in dieser Reihenfolge:\n" +
      "   - Legen Sie zuerst ein Finanzamt an (Men�: Fibu/Finanz�mter)\n" +
      "   - Erstellen Sie anschliessend einen neuen Mandanten (Navigation: Fibu/Mandanten)\n" +
      "   - Aktivieren Sie den angelegten Mandanten (Men�: Fibu/Einstellungen\n");

    Application.addWelcomeMessage(welcome);
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#update(double)
   */
  public void update(double oldVersion) throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: Fibu.java,v $
 * Revision 1.17  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.16  2004/02/09 13:05:13  willuhn
 * @C misc
 *
 * Revision 1.15  2004/01/29 00:31:33  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2004/01/29 00:06:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2004/01/27 22:47:30  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.9  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.8  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/25 01:23:19  willuhn
 * @N added Menu shortcuts
 *
 * Revision 1.5  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.4  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 * Revision 1.2  2003/11/14 00:49:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/13 00:36:12  willuhn
 * *** empty log message ***
 *
 **********************************************************************/