/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.fibu.gui.util.CustomDateFormat;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.CustomDecimalFormat;
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
   * Encoding, welches fuer die Reports verwendet wird.
   */
  public final static String ENCODING_REPORTS = SETTINGS.getString("encoding","ISO-8859-15");
  
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
      Class c = Application.getPluginLoader().getManifest(Fibu.class).getClassLoader().load(s);
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
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Kein aktives Geschäftsjahr definiert"),StatusBarMessage.TYPE_INFO));
      return;
    }

    try
    {
      Mandant mandant = jahr.getMandant();
      String[] params = {mandant.getFirma(),
                         (String)jahr.getAttribute(jahr.getPrimaryAttribute()),
                         jahr.isClosed() ? i18n.tr("geschlossen") : "in Bearbeitung"};
      
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Mandant: {0}, Jahr: {1}, Status: {2}", params),StatusBarMessage.TYPE_INFO));
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
  
  /**
   * Liefert true, wenn die Geprueft-Markierung von SynTAX- mit Hibiscus-Buchungen synchronisiert werden soll.
   * @return true, wenn die Geprueft-Markierung von SynTAX- mit Hibiscus-Buchungen synchronisiert werden soll.
   */
  public static boolean getSyncCheckmarks()
  {
    return SETTINGS.getBoolean("checkmarks.sync",true);
  }
  
  /**
   * Legt fest, ob die Geprueft-Markierung von SynTAX- mit Hibiscus-Buchungen synchronisiert werden soll.
   * @param b true, wenn die Geprueft-Markierung von SynTAX- mit Hibiscus-Buchungen synchronisiert werden soll.
   */
  public static void setSyncCheckmarks(boolean b)
  {
    SETTINGS.setAttribute("checkmarks.sync",b);
  }
  
  /**
   * Legt fest, ob beim Schliessen eines Geschaeftsjahres alternativ zur automatischen Erstellung des
   * Folgejahres auch ein eventuell bereits vorhandenes verwendet werden kann.
   * @return true, wenn ein existierendes Folgejahr benutzt werden kann.
   */
  public static boolean getUseExistingGjOnClose()
  {
    return SETTINGS.getBoolean("gj.close.use-existing",true);
  }
  
  /**
   * Legt fest, ob beim Schliessen eines Geschaeftsjahres alternativ zur automatischen Erstellung des
   * Folgejahres auch ein eventuell bereits vorhandenes verwendet werden kann.
   * @param b true, wenn ein existierendes Folgejahr benutzt werden kann.
   */
  public static void setUseExistingGjOnClose(boolean b)
  {
    SETTINGS.setAttribute("gj.close.use-existing",b);
  }
  
}
