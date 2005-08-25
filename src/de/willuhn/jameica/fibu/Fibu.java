/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Fibu.java,v $
 * $Revision: 1.25 $
 * $Date: 2005/08/25 21:58:58 $
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import de.willuhn.datasource.db.EmbeddedDatabase;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

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
  public final static DateFormat LONGDATEFORMAT   = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

  /**
   * Dateformatter.
   */
  public final static DateFormat DATEFORMAT       = new SimpleDateFormat("dd.MM.yyyy");
  
  /**
   * Dateformatter fuer Kurz-Format.
   */
  public final static DateFormat FASTDATEFORMAT   = new SimpleDateFormat("ddMMyyyy");
  
  /**
   * Wochentage
   */
  public final static String[] WEEKDAYS = new String[] {
    "Sonntag",
    "Montag",
    "Dienstag",
    "Mittwoch",
    "Donnerstag",
    "Freitag",
    "Sonnabend"
  };

  /**
   * DecimalFormat.
   */
  public final static DecimalFormat DECIMALFORMAT = (DecimalFormat) DecimalFormat.getInstance(Application.getConfig().getLocale());

  static {
    DECIMALFORMAT.applyPattern("###,###,##0.00");
    DECIMALFORMAT.setGroupingUsed(true);
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  public void init() throws ApplicationException
  {
    ////////////////////////////////////////////////////////////////////////////
    // TODO WIEDER ENTFERNEN, WENN RELEASED
    // Damit wir die Updates nicht immer haendisch nachziehen muessen, rufen wir
    // bei einem Fehler das letzte Update-Script nochmal auf.
    if (!Application.inClientMode())
    {
      try
      {
        de.willuhn.jameica.system.Settings s = new de.willuhn.jameica.system.Settings(Fibu.class);
        double size = s.getDouble("sql-update-size",-1);
        
        File f = new File(getResources().getPath() + "/sql/update.sql");
        
        if (f.length() != size)
        {
          EmbeddedDatabase db = new EmbeddedDatabase(getResources().getWorkPath() + "/db","fibu","fibu");
          db.executeSQLScript(f);
          s.setAttribute("sql-update-size",(double)f.length());
        }
      }
      catch (Exception e2)
      {
        e2.printStackTrace();
      }
    }
    ////////////////////////////////////////////////////////////////////////////
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
    if (Application.inClientMode())
      return;

    File dbDir = new File(getResources().getWorkPath(),"db");
    if (!dbDir.exists())
      dbDir.mkdirs();

    try
    {
      EmbeddedDatabase db = new EmbeddedDatabase(dbDir.getAbsolutePath(),"fibu","fibu");

      File create = new File(getResources().getPath() + "/sql/create.sql");
      File init   = new File(getResources().getPath() + "/sql/init.sql");

      db.executeSQLScript(create);
      db.executeSQLScript(init);
    }
    catch (Exception e)
    {
      Logger.error("unable to create sql tables",e);
      throw new ApplicationException(getResources().getI18N().tr("Fehler beim Installieren des Fibu-Plugins"));
    }

    String welcome = getResources().getI18N().tr("Fibu für Jameica");
    welcome += "\n";
    welcome += getResources().getI18N().tr("Beachten Sie bitte folgende erste Schritte in dieser Reihenfolge:\n" +
      "   - Legen Sie zuerst ein Finanzamt an (Menü: Plugins/Fibu/Finanzämter)\n" +
      "   - Erstellen Sie anschliessend einen neuen Mandanten (Navigation: Fibu/Mandanten)\n");

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
 * Revision 1.25  2005/08/25 21:58:58  willuhn
 * @N SKR04
 *
 * Revision 1.24  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.23  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.20  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.19  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.18  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
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