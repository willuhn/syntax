/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/Settings.java,v $
 * $Revision: 1.31 $
 * $Date: 2005/10/18 23:28:55 $
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
import de.willuhn.jameica.fibu.gui.dialogs.GeschaeftsjahrAuswahlDialog;
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
   * @return das Konto oder <code>null</code> wenn noch keines definiert ist.
   * @throws RemoteException
   */
  public static Konto getAbschreibunsgKonto(Geschaeftsjahr jahr) throws RemoteException
  {
    if (jahr == null)
      return null;
    
    String id = settings.getString("jahr." + jahr.getID() + ".afakonto",null);
    if (id != null && id.length() > 0)
      return (Konto) getDBService().createObject(Konto.class,id);
    return null;
  }

  /**
   * Speichert das Abschreibungskonto fuer das Geschaeftsjahr.
   * @param jahr Geschaeftsjahr.
   * @param k Konto.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static void setAbschreibungsKonto(Geschaeftsjahr jahr, Konto k) throws RemoteException, ApplicationException
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
    settings.setAttribute("jahr." + jahr.getID() + ".afakonto",k.getID());
    
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
	public static DBService getDBService() throws RemoteException
	{
	  // TODO Das ist noch nicht Client/Server tauglich
    if (db == null)
    {
      try
      {
        db = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
        db.setActiveGeschaeftsjahr(getActiveGeschaeftsjahr());
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
   * Legt das aktuelle Geschaeftsjahr manuell fest.
   * @param j zu aktivierendes Geschaeftsjahr.
   */
  public static void setActiveGeschaeftsjahr(Geschaeftsjahr j)
  {
    if (j == null)
    {
      jahr = null;
      return;
    }

    try
    {
      jahr = (Geschaeftsjahr) getDBService().createObject(Geschaeftsjahr.class,j.getID());
      settings.setAttribute("gj.active",jahr.getID());
    }
    catch (RemoteException e)
    {
      Logger.error("error while activating gj",e);
    }
    setStatus();
  }
  
  private static boolean inProgress = false;
  
  /**
   * Liefert das aktuelle Geschaeftsjahr oder einen Dialog zur Abfrage, falls dieses noch
   * nicht ausgeaehlt ist.
   * @return das aktive Geschaeftsjahr.
   * @throws RemoteException
   */
  public static Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException
  {
    if (inProgress)
      return null;
  	if (jahr != null && !jahr.isNewObject())
  		return jahr;

  	try
    {
      boolean ask = true;
      inProgress = true;
      String id = settings.getString("gj.active",null);

      if (id != null)
      {
        try
        {
          jahr = (Geschaeftsjahr) getDBService().createObject(Geschaeftsjahr.class,id);
          ask = false;
        }
        catch (ObjectNotFoundException oe)
        {
          // ignore
        }
      }

      if (ask)
      {
        DBIterator list = getDBService().createList(Geschaeftsjahr.class);
        if (list.size() > 0 && !Application.inServerMode())
        {
          // TODO Das funktioniert im Client/Server-Mode noch nicht.
          Logger.info("open gj select dialog");
          GeschaeftsjahrAuswahlDialog d = new GeschaeftsjahrAuswahlDialog(GeschaeftsjahrAuswahlDialog.POSITION_CENTER);
          jahr = (Geschaeftsjahr) d.open();
        }
        else
        {
          Logger.info("auto creating new mandant/geschaeftsjahr");
          Finanzamt fa = null;
          try
          {
            DBIterator faList = getDBService().createList(Finanzamt.class);
            if (faList.size() > 0)
            {
              Logger.info("reusing existing finanzamt");
              fa = (Finanzamt) faList.next();
              fa.transactionBegin();
            }
            else
            {
              fa = (Finanzamt) getDBService().createObject(Finanzamt.class,null);
              fa.setName("default");
              fa.transactionBegin();
              fa.store();
            }
            
            Mandant m = null;
            DBIterator mList = getDBService().createList(Mandant.class);
            if (mList.size() > 0)
            {
              Logger.info("reusing existing mandant");
              m = (Mandant) mList.next();
            }
            else
            {
              m = (Mandant) getDBService().createObject(Mandant.class,null);
              m.setFinanzamt(fa);
              m.setSteuernummer("");
              m.setFirma("default");
              m.store();
            }
            
            jahr = (Geschaeftsjahr) getDBService().createObject(Geschaeftsjahr.class,null);
            jahr.setKontenrahmen((Kontenrahmen) getDBService().createObject(Kontenrahmen.class,"2"));
            jahr.setMandant(m);
            jahr.store();
            
            fa.transactionCommit();
            Logger.info("finanzamt, mandant, geschaeftsjahr created");
          }
          catch (Exception e)
          {
            Logger.error("unable to create finanzamt, mandant, geschaeftsjahr",e);
            if (fa != null)
              fa.transactionRollback();
            throw e;
          }
          
        }
      }

      setStatus();
      setActiveGeschaeftsjahr(jahr);
      return jahr;
    }
    catch (Exception e)
    {
      Logger.error("error while choosing mandant",e);
      throw new RemoteException("Fehler beim Auswählen/Erstellen des aktiven Geschäftsjahres");
    }
    finally
    {
      inProgress = false;
    }
  }
  
  /**
   * Setzt den Statustext in der Statuszeile.
   */
  private static void setStatus()
  {
    if (jahr == null || Application.inServerMode())
      return;
    try
    {
      Logger.debug("aktives Geschaeftsjahr: " + jahr.getAttribute(jahr.getPrimaryAttribute()));
      Mandant m = jahr.getMandant();
      I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
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