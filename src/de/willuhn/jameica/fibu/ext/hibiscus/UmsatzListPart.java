/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/ext/hibiscus/UmsatzListPart.java,v $
 * $Revision: 1.6 $
 * $Date: 2011/05/12 09:10:32 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.ext.hibiscus;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Erweitert die Liste der Umsaetze um eine Spalte.
 * BUGZILLA 140 http://www.willuhn.de/bugzilla/show_bug.cgi?id=140
 */
public class UmsatzListPart implements Extension
{
  private class Cache {
	  public Map<String,Buchung> buchungen = null;
	  public Map<String,String> kuerzel = null;

	  public void clear() {
		  buchungen = null;
		  kuerzel = null;
	  }
  }

  private Cache cache = new Cache();
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
   
  
  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    if (extendable == null || !(extendable instanceof TablePart))
    {
      Logger.warn("invalid extendable, skipping extension");
      return;
    }
    
    this.cache.clear(); // Cache loeschen, damit die Daten neu gelesen werden
    
    TablePart table = (TablePart) extendable;
    Column col = new Column("id-int", i18n.tr("SynTAX-Beleg") ,new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Integer))
          return null;

        Cache cache = getCache();
        Buchung b = cache.buchungen.get(o.toString());
        String kuerzel = cache.kuerzel.get(o.toString());
        		
        if (b == null)
          return null;
        try
        {
            return String.format("%s%04d", kuerzel, b.getBelegnummer());
        }
        catch (RemoteException re)
        {
          Logger.error("unable to load beleg number",re);
        }
        return null;
      }
    
    }, false, Column.ALIGN_AUTO, Column.SORT_BY_DISPLAY);

    table.addColumn(col);
  }
  
  /**
   * Liefert den Cache zum Lookup von Hibiscus Umsatz-ID zu Buchung.
   * @return der Cache.
   */
  private Cache getCache()
  {
    if (this.cache != null && this.cache.buchungen != null && this.cache.kuerzel != null)
      return this.cache;
    
    this.cache.buchungen = new HashMap<String,Buchung>();
    this.cache.kuerzel = new HashMap<String,String>();
    try
    {
      // BUGZILLA 1593 - Wir suchen Mandanten-uebergreifend
      DBIterator list = Settings.getDBService().createList(Buchung.class);
      list.addFilter("hb_umsatz_id is not null");
      while (list.hasNext())
      {
        Buchung b = (Buchung) list.next();
        if (b.getHibiscusUmsatzID() == null)
          continue;
        cache.buchungen.put(b.getHibiscusUmsatzID(),b);
        String kuerzel = b.getGeschaeftsjahr().getMandant().getKuerzel();
        cache.kuerzel.put(b.getHibiscusUmsatzID(), kuerzel);
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to fill lookup cache",e);
    }
    return this.cache;
  }

  /**
   * Fuegt eine Buchung manuell zum Cache hinzu.
   * @param b die neue Buchung.
   * @throws RemoteException
   */
  void add(Buchung b) throws RemoteException
  {
    if (b == null)
      return;
    
    String umsatzId = b.getHibiscusUmsatzID();
    if (umsatzId == null || umsatzId.length() == 0)
      return; // Buchung ist gar nicht zugeordnet
    getCache().buchungen.put(umsatzId,b);
  }
}


/*********************************************************************
 * $Log: UmsatzListPart.java,v $
 * Revision 1.6  2011/05/12 09:10:32  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.5  2010-06-03 17:07:14  willuhn
 * @N Erste Version der vollautomatischen Uebernahme von Umsatzen in Hibiscus!
 *
 * Revision 1.4  2010/06/01 11:58:04  willuhn
 * @R removed debug output
 *
 * Revision 1.3  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.2.2.1  2008/06/25 09:40:02  willuhn
 * @B Buchungsnummer wurde unter Umstaenden nicht korrekt angezeigt
 *
 * Revision 1.2  2007/04/04 22:19:09  willuhn
 * @B Umsatzliste nur erweitern, wenn GJ vorhanden
 *
 * Revision 1.1  2006/10/09 23:48:41  willuhn
 * @B bug 140
 *
 **********************************************************************/