/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Fibu.java,v $
 * $Revision: 1.15 $
 * $Date: 2004/01/29 00:31:33 $
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
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.jar.JarFile;

import de.willuhn.datasource.db.EmbeddedDatabase;
import de.willuhn.jameica.AbstractPlugin;
import de.willuhn.jameica.Application;
import de.willuhn.util.I18N;

/**
 * Basisklasse des Fibu-Plugins fuer das Jameica-Framework.
 * @author willuhn
 */
public class Fibu extends AbstractPlugin
{

  public static DateFormat DATEFORMAT       = new SimpleDateFormat("dd.MM.yyyy");
  public static DateFormat FASTDATEFORMAT   = new SimpleDateFormat("ddMMyyyy");
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

	private boolean freshInstall = false;

  static {
    DECIMALFORMAT.applyPattern("#0.00");
  }

  /**
   * ct.
   * @param jar
   */
  public Fibu(JarFile jar)
  {
  	super(jar);
  }

  /**
   * Initialisiert das Plugin.
   * @see de.willuhn.jameica.Plugin#init()
   */
  public boolean init()
  {
		try {
			Settings.setDatabase(getDatabase().getDBService());
		}
		catch (RemoteException e)
		{
			Application.getLog().error("unable to open database",e);
			return false;
		}
		return true;
  }

  /**
   * Beendet das Plugin.
   * @see de.willuhn.jameica.Plugin#shutDown()
   */
  public void shutDown()
  {
  }

  /**
   * @see de.willuhn.jameica.Plugin#install()
   */
  public boolean install()
  {
		EmbeddedDatabase db = getDatabase();
		if (!db.exists())
		{
			try {
				db.create();
			}
			catch (IOException e)
			{
				Application.getLog().error("unable to create database",e);
				return false;
			}
			try
			{
				db.executeSQLScript(new File(getPath() + "/sql/create.sql"));
			}
			catch (Exception e)
			{
				Application.getLog().error("unable to create sql tables",e);
				return false;
			}
			try
			{
				db.executeSQLScript(new File(getPath() + "/sql/init.sql"));
			}
			catch (Exception e)
			{
				Application.getLog().error("unable to insert init data",e);
				return false;
			}
      
		}
		freshInstall = true;
		return true;
  }

  /**
   * @see de.willuhn.jameica.Plugin#update(double)
   */
  public boolean update(double oldVersion)
  {
    return true;
  }

  /**
   * @see de.willuhn.jameica.AbstractPlugin#getPassword()
   */
  protected String getPassword()
  {
    return "fibu";
  }

  /**
   * @see de.willuhn.jameica.AbstractPlugin#getUsername()
   */
  protected String getUsername()
  {
    return "fibu";
  }

  /**
   * @see de.willuhn.jameica.Plugin#getWelcomeText()
   */
  public String getWelcomeText()
  {
    String  welcome = I18N.tr("Finanzbuchhaltung für Jameica ") + getVersion() + "\n";
    
    if (!freshInstall)
    	return welcome;
    	
    welcome += "\n" +
      I18N.tr("Beachten Sie bitte folgende erste Schritte in dieser Reihenfolge:\n" +      "   - Legen Sie zuerst ein Finanzamt an (Menü: Fibu/Finanzämter)\n" +      "   - Erstellen Sie anschliessend einen neuen Mandanten (Navigation: Fibu/Mandanten)\n" +      "   - Aktivieren Sie den angelegten Mandanten (Menü: Fibu/Einstellungen\n");
    return welcome;  }

}

/*********************************************************************
 * $Log: Fibu.java,v $
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