/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.51 $
 * $Date: 2011/03/21 11:17:27 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.fibu.gui.util.CustomDateFormat;
import de.willuhn.jameica.fibu.gui.util.CustomDecimalFormat;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.View;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Verwaltet die Einstellungen des Plugins.
 * @author willuhn
 */
public class Settings
{
  /**
   * Queue, die benachrtichtigt wird, wenn ein neuer Kontenrahmen angelegt wurde.
   */
  public final static String QUEUE_KONTENRAHMEN_CREATED = "syntax.kontenrahmen.created";
  
  /**
   * Die Settings.
   */
  public final static de.willuhn.jameica.system.Settings SETTINGS = new de.willuhn.jameica.system.Settings(Fibu.class);

  /**
   * Dateformatter.
   */
  public final static DateFormat LONGDATEFORMAT   = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  /**
   * Dateformatter.
   */
  public final static DateFormat DATEFORMAT       = new SimpleDateFormat("dd.MM.yyyy");

  /**
   * Dateformatter fuer Kurz-Format.
   */
  public final static DateFormat FASTDATEFORMAT   = new SimpleDateFormat("ddMMyyyy");

  /**
   * Dateformatter fuer Buchungen.
   */
  public final static DateFormat BUCHUNGDATEFORMAT   = new SimpleDateFormat("ddMMyy");

  /**
   * Unser eigenes kombiniertes Dateformat.
   */
  public final static DateFormat CUSTOM_DATEFORMAT   = new CustomDateFormat();

  /**
   * DecimalFormat.
   */
  public final static DecimalFormat DECIMALFORMAT = new CustomDecimalFormat();

  /**
   * Default-Waehrung.
   */
  public final static String WAEHRUNG = SETTINGS.getString("currency.default","EUR");

  /**
   * Encoding, welches zum Einlesen der SQL-Scripts verwendet wird.
   */
  public final static String ENCODING = SETTINGS.getString("encoding","ISO-8859-15");
  
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
  
  private static DBService db        = null;
  private static DBSupport dbSupport = null;
	private static Geschaeftsjahr jahr = null;
  private static boolean inUpdate    = false;
  
  /**
   * Liefert true, wenn die Anwendung zum ersten Mal gestartet wird.
   * @return true, beim ersten Start.
   */
  public static boolean isFirstStart()
  {
    try
    {
      return getDBSupport() == null || getActiveGeschaeftsjahr() == null;
    }
    catch (Exception e)
    {
      Logger.error("unable to load active geschaeftsjahr",e);
      return false;
    }
  }

  /**
   * Legt die Support-Klasse fuer die Datenbankanbindung fest.
   * @param support die Support-Klasse.
   */
  public static void setDBSupport(DBSupport support)
  {
    dbSupport = support;
    if (dbSupport != null)
      SETTINGS.setAttribute("database.support.class",dbSupport.getClass().getName());
  }
  
  /**
   * Liefert die Support-Klasse fuer die Datenbank.
   * @return die Support-Klasse fuer die Datenbank.
   */
  public static DBSupport getDBSupport()
  {
    if (dbSupport != null)
      return dbSupport;
    String s = SETTINGS.getString("database.support.class",null);
    if (s == null || s.length() == 0)
      return null;
    try
    {
      Logger.info("trying to load " + s);
      Class c = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getClassLoader().load(s);
      dbSupport = (DBSupport) c.newInstance();
      return dbSupport;
    }
    catch (Exception e)
    {
      Logger.error("unable to load db support class",e);
      return null;
    }
  }
  
  /**
   * Liefert das Abschreibungskonto fuer das Geschaeftsjahr.
   * @param jahr Geschaeftsjahr
   * @param gwg handelt es sich um ein GWG?
   * @return das Konto oder <code>null</code> wenn noch keines definiert ist.
   * @throws RemoteException
   */
  public static Konto getAbschreibungsKonto(Geschaeftsjahr jahr, boolean gwg) throws RemoteException
  {
    if (jahr == null)
      return null;
    
    String id = SETTINGS.getString("jahr." + jahr.getID() + ".afakonto" + (gwg ? ".gwg" : ""),null);
    if (id != null && id.length() > 0)
      return (Konto) getDBService().createObject(Konto.class,id);
    return null;
  }

  /**
   * Speichert das Abschreibungskonto fuer das Geschaeftsjahr.
   * @param jahr Geschaeftsjahr.
   * @param k Konto.
   * @param gwg handelt es sich um ein GWG?
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static void setAbschreibungsKonto(Geschaeftsjahr jahr, Konto k, boolean gwg) throws RemoteException, ApplicationException
  {
    if (jahr == null)
      return;

    if (k == null)
    {
      SETTINGS.setAttribute("jahr." + jahr.getID() + ".afakonto",(String)null);
      return;
    }

    Kontoart ka = k.getKontoArt();
    if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
    {
      I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
      throw new ApplicationException(i18n.tr("Konto {0} ist kein gültiges Aufwandskonto",k.getKontonummer()));
    }
    SETTINGS.setAttribute("jahr." + jahr.getID() + ".afakonto" + (gwg ? ".gwg" : ""),k.getID());
    
  }
  
  /**
   * Liefert das Jahr, in dem die vereinfachte Halbjahres-Regel fuer Abschreibungen abgeschafft wurde.
   * @return jahr der Abschaffung der vereinfachte Halbjahres-Regel.
   */
  public static int getGeaenderteHalbjahresAbschreibung()
  {
    return SETTINGS.getInt("abschreibung.vereinfachungsregel",2004);
  }
  
  /**
   * Liefert die Nettogrenze fuer GWG (Geringwertige Wirtschaftsgueter).
   * @param jahr Geschaeftsjahr.
   * @return Nettogrenze.
   * @throws RemoteException
   */
  public static double getGwgWert(Geschaeftsjahr jahr) throws RemoteException
  {
    double gwgDef = 150d;
    
    if (jahr == null)
      return gwgDef;

    return SETTINGS.getDouble("jahr." + jahr.getID() + ".gwg",gwgDef);
  }
  
  /**
   * Speichert die Nettogrenze fuer GWG.
   * @param jahr Geschaeftsjahr.
   * @param gwg Nettogrenze.
   * @throws RemoteException
   */
  public static void setGwgWert(Geschaeftsjahr jahr, double gwg) throws RemoteException
  {
    if (jahr == null)
      return;
    
    if (gwg < 0d)
      gwg = 150d;

    SETTINGS.setAttribute("jahr." + jahr.getID() + ".gwg",gwg);
  }
 
  /**
   * Liefert den Datenbank-Service.
   * @return Datenbank.
   * @throws RemoteException
   */
  private static DBService getInternalDBService() throws RemoteException
  {
    if (db == null)
    {
      try
      {
        db = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
      }
      catch (RemoteException e)
      {
        throw e;
      }
      catch (Exception e2)
      {
        Logger.error("unable to load database service",e2);
        throw new RemoteException("Fehler beim Laden des Datenbank-Service",e2);
      }
    }
    return db;
  }

  /**
	 * Liefert den Datenbank-Service.
	 * @return Datenbank.
	 * @throws RemoteException
	 */
	public static DBService getDBService() throws RemoteException
	{
    db = getInternalDBService();
    db.setActiveGeschaeftsjahr(getActiveGeschaeftsjahr());
    return db;
	}

  /**
   * Laedt das aktuelle Geschaeftsjahr neu.
   */
  public static void reloadActiveGeschaeftsjahr()
  {
    if (jahr == null)
      return;
    
    try
    {
      jahr = (Geschaeftsjahr) getInternalDBService().createObject(Geschaeftsjahr.class,jahr.getID());
      getInternalDBService().setActiveGeschaeftsjahr(jahr);
      setStatus();
    }
    catch (RemoteException e)
    {
      Logger.error("unable to reload active gj",e);
    }
    
  }
  /**
   * Legt das aktuelle Geschaeftsjahr manuell fest.
   * @param j zu aktivierendes Geschaeftsjahr.
   */
  public static void setActiveGeschaeftsjahr(Geschaeftsjahr j)
  {
    if (j == null)
    {
      jahr = null;
      SETTINGS.setAttribute("gj.active",(String)null);
      try
      {
        getInternalDBService().setActiveGeschaeftsjahr(null);
      }
      catch (RemoteException e)
      {
        Logger.error("unable to disable active gj",e);
      }
      setStatus();
      return;
    }

    try
    {
      jahr = j;
      SETTINGS.setAttribute("gj.active",jahr.getID());
      try
      {
        getInternalDBService().setActiveGeschaeftsjahr(jahr);
      }
      catch (RemoteException e)
      {
        Logger.error("unable to activate active gj",e);
      }
    }
    catch (RemoteException e)
    {
      Logger.error("error while activating gj",e);
    }
    setStatus();
  }
  
  /**
   * Liefert das aktuelle Geschaeftsjahr oder einen Dialog zur Abfrage, falls dieses noch
   * nicht ausgeaehlt ist.
   * @return das aktive Geschaeftsjahr.
   * @throws RemoteException
   */
  public static synchronized Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException
  {
  	if (jahr != null && !jahr.isNewObject())
  		return jahr;

    String id = SETTINGS.getString("gj.active",null);

    if (id != null)
    {
      try
      {
        jahr = (Geschaeftsjahr) getInternalDBService().createObject(Geschaeftsjahr.class,id);
        if (jahr != null)
          setActiveGeschaeftsjahr(jahr);
      }
      catch (ObjectNotFoundException oe)
      {
        Logger.warn("defined geschaeftsjahr does not exist");
      }
    }
    return jahr;
  }

  /**
   * Setzt den Statustext zum aktuellen Geschaeftsjahr.
   */
  public static void setStatus()
  {
    if (Application.inServerMode())
      return;

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    if (jahr == null)
    {
      View view = GUI.getView();
      if (view != null)
        view.setLogoText(i18n.tr("Kein aktives Geschäftsjahr definiert"));
      return;
    }

    try
    {
      Mandant mandant = jahr.getMandant();
      String[] params = {mandant.getFirma(),
                         (String)jahr.getAttribute(jahr.getPrimaryAttribute()),
                         jahr.isClosed() ? i18n.tr("geschlossen") : "in Bearbeitung"};
      
      View view = GUI.getView();
      if (view != null)
        view.setLogoText(i18n.tr("Mandant: {0}, Jahr: {1}, Status: {2}", params));
    }
    catch (RemoteException e)
    {
      Logger.error("error while refreshing statusbar",e);
    }
  }
  
  /**
   * Legt fest, ob sich die Anwendung gerade im Update-Prozess befindet.
   * @param b true, wenn sie sich im Update befindet.
   */
  static void setInUpdate(boolean b)
  {
    inUpdate = b;
  }
  
  /**
   * Prueft, ob sich die Anwendung gerade in einem Update befindet.
   * @return true, wenn sie sich in einem Update befindet.
   */
  public static boolean inUpdate()
  {
    return inUpdate;
  }
  
  /**
   * Liefert true, wenn das Aendern des Systemkontenrahmens erlaubt ist.
   * @return true, wenn das Aendern des Systemkontenrahmens erlaubt ist.
   */
  public static boolean getSystemDataWritable()
  {
    return SETTINGS.getBoolean("systemdata.writable",false);
  }
  
  /**
   * Legt fest, ob das Aendern des Systemkontenrahmens erlaubt ist.
   * @param b true, wenn das Aendern des Systemkontenrahmens erlaubt ist.
   */
  public static void setSystemDataWritable(boolean b)
  {
    SETTINGS.setAttribute("systemdata.writable",b);
  }
}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.51  2011/03/21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 * Revision 1.50  2010-11-12 16:27:27  willuhn
 * @C Plugin-Classloader statt dem von Jameica verwenden
 *
 * Revision 1.49  2010-08-02 22:42:03  willuhn
 * @N BUGZILLA 891 - Betraege in der Datenbank nur noch gerundet speichern
 *
 * Revision 1.48  2010/06/01 16:35:48  willuhn
 * @C Konstanten verschoben
 *
 * Revision 1.47  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.45.2.3  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 * Revision 1.45.2.2  2008/06/25 10:06:51  willuhn
 * *** empty log message ***
 *
 * Revision 1.45.2.1  2008/06/25 10:06:19  willuhn
 * @C Default-GWG-Wert aktualisiert
 **********************************************************************/