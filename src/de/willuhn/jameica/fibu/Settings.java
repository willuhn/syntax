/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.37 $
 * $Date: 2006/01/04 16:04:33 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.GUI;
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
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(Fibu.class);

  /**
   * Default-Waehrung.
   */
  public final static String WAEHRUNG = settings.getString("currency.default","EUR");

  private static DBService db = null;
	private static Geschaeftsjahr jahr = null;
  
  /**
   * Liefert das Abschreibungskonto fuer das Geschaeftsjahr.
   * @param jahr Geschaeftsjahr
   * @param gwg handelt es sich um ein GWG?
   * @return das Konto oder <code>null</code> wenn noch keines definiert ist.
   * @throws RemoteException
   */
  public static Konto getAbschreibunsgKonto(Geschaeftsjahr jahr, boolean gwg) throws RemoteException
  {
    if (jahr == null)
      return null;
    
    String id = settings.getString("jahr." + jahr.getID() + ".afakonto" + (gwg ? ".gwg" : ""),null);
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
      settings.setAttribute("jahr." + jahr.getID() + ".afakonto",(String)null);
      return;
    }

    Kontoart ka = k.getKontoArt();
    if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
    {
      I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
      throw new ApplicationException(i18n.tr("Konto {0} ist kein gültiges Aufwandskonto",k.getKontonummer()));
    }
    settings.setAttribute("jahr." + jahr.getID() + ".afakonto" + (gwg ? ".gwg" : ""),k.getID());
    
  }
  
  /**
   * Liefert das Jahr, in dem die vereinfachte Halbjahres-Regel fuer Abschreibungen abgeschafft wurde.
   * @return jahr der Abschaffung der vereinfachte Halbjahres-Regel.
   */
  public static int getGeaenderteHalbjahresAbschreibung()
  {
    return settings.getInt("abschreibung.vereinfachungsregel",2004);
  }
  
  /**
   * Liefert die Nettogrenze fuer GWG (Geringwertige Wirtschaftsgueter).
   * @param jahr Geschaeftsjahr.
   * @return Nettogrenze.
   * @throws RemoteException
   */
  public static double getGwgWert(Geschaeftsjahr jahr) throws RemoteException
  {
    double gwgDef = 410d;
    
    if (jahr == null)
      return gwgDef;

    return settings.getDouble("jahr." + jahr.getID() + ".gwg",gwgDef);
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
      gwg = 410d;

    settings.setAttribute("jahr." + jahr.getID() + ".gwg",gwg);
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
      settings.setAttribute("gj.active",(String)null);
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
      settings.setAttribute("gj.active",jahr.getID());
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

    String id = settings.getString("gj.active",null);

    if (id != null)
    {
      try
      {
        jahr = (Geschaeftsjahr) getInternalDBService().createObject(Geschaeftsjahr.class,id);
      }
      catch (ObjectNotFoundException oe)
      {
        Logger.warn("defined geschaeftsjahr does not exist");
      }
    }
    else
    {
      try
      {
        jahr = createGeschaeftsjahr();
      }
      catch (ApplicationException ae)
      {
        throw new RemoteException(ae.getMessage());
      }
    }
    setActiveGeschaeftsjahr(jahr);
    return jahr;
  }
  
  /**
   * Liefert das erste gefundene Finanzamt.
   * Falls keines existiert, wird ein neues angelegt.
   * @return Finanzamt. Niemals null. 
   * @throws RemoteException
   * @throws ApplicationException
   */
  private static Finanzamt createFinanzamt() throws RemoteException, ApplicationException
  {
    DBIterator list = getInternalDBService().createList(Finanzamt.class);

    if (list.size() > 0)
      return (Finanzamt) list.next();

    Finanzamt fa = (Finanzamt) getInternalDBService().createObject(Finanzamt.class,null);
    fa.setName("default");
    fa.store();
    return fa;
  }
  
  /**
   * Liefert den ersten gefundenen Mandanten.
   * Falls keiner existiert, wird ein neuer angelegt.
   * @return Mandant. Niemals null. 
   * @throws RemoteException
   * @throws ApplicationException
   */
  private static Mandant createMandant() throws RemoteException, ApplicationException
  {
    DBIterator list = getInternalDBService().createList(Mandant.class);

    if (list.size() > 0)
      return (Mandant) list.next();

    Mandant m = (Mandant) getInternalDBService().createObject(Mandant.class,null);
    m.setFinanzamt(createFinanzamt());
    m.setSteuernummer("");
    m.setFirma("default");
    m.store();
    return m;
  }

  /**
   * Liefert das erste gefundene Geschaeftsjahr.
   * Falls keines existiert, wird ein neues angelegt.
   * @return Geschaeftsjahr. Niemals null. 
   * @throws RemoteException
   * @throws ApplicationException
   */
  private static Geschaeftsjahr createGeschaeftsjahr() throws RemoteException, ApplicationException
  {
    DBIterator list = getInternalDBService().createList(Geschaeftsjahr.class);

    if (list.size() > 0)
      return (Geschaeftsjahr) list.next();

    Geschaeftsjahr j = (Geschaeftsjahr) getInternalDBService().createObject(Geschaeftsjahr.class,null);
    j.setKontenrahmen((Kontenrahmen) getInternalDBService().createObject(Kontenrahmen.class,"2"));
    j.setMandant(createMandant());
    j.store();
    if (!Application.inServerMode())
      GUI.getStatusBar().setSuccessText("Neues Geschäftsjahr automatisch angelegt");
    return j;
  }

  /**
   * Setzt den Statustext in der Statuszeile.
   */
  private static void setStatus()
  {
    if (Application.inServerMode())
      return;

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    if (jahr == null)
    {
      GUI.getStatusBar().setStatusText(i18n.tr("Kein aktives Geschäftsjahr vorhanden"));
      return;
    }
    try
    {
      Logger.debug("aktives Geschaeftsjahr: " + jahr.getAttribute(jahr.getPrimaryAttribute()));
      Mandant m = jahr.getMandant();
      String[] params = 
      {
        m.getFirma(),
        (String)jahr.getAttribute(jahr.getPrimaryAttribute()),
        jahr.isClosed() ? i18n.tr("geschlossen") : "in Bearbeitung"
      };
      GUI.getStatusBar().setStatusText(i18n.tr("Mandant: {0}, Jahr: {1}, Status: {2}", params));
    }
    catch (RemoteException e)
    {
      Logger.error("error while refreshing statusbar",e);
    }
  }
}

/*********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.37  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.36  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.35  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.34  2006/01/03 13:48:14  willuhn
 * @N Halbjahresregel bei Abschreibungen
 *
 * Revision 1.33  2005/10/21 15:59:06  willuhn
 * @C getActiveGeschaeftsjahr cleanup
 *
 * Revision 1.32  2005/10/20 23:03:44  willuhn
 * @N network support
 *
 * Revision 1.31  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.30  2005/10/13 15:44:33  willuhn
 * @B bug 139
 *
 * Revision 1.29  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 * Revision 1.28  2005/09/28 17:25:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.27  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.26  2005/09/05 15:00:43  willuhn
 * *** empty log message ***
 *
 * Revision 1.25  2005/09/05 14:32:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.24  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.23  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2005/09/01 14:04:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.20  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.19  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.18  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.17  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.16  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.15  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.14  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.12  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.11  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.10  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.9  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.8  2004/02/09 13:05:13  willuhn
 * @C misc
 *
 * Revision 1.7  2004/01/28 00:37:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/28 00:31:34  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.3  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/