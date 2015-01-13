/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.ext.hibiscus;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.formatter.Formatter;
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
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private Map<String,Buchung> umsatzCache = null;
  private Map<String,String> kuerzelCache = new HashMap<String,String>();
  
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
    
    // Cache leeren, damit die Daten neu gelesen werden
    this.umsatzCache = null;
    this.kuerzelCache.clear();
    
    TablePart table = (TablePart) extendable;
    table.addColumn(i18n.tr("SynTAX-Beleg"),"id-int", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Integer))
          return null;

        Buchung b = getCache().get(o.toString());

        if (b == null)
          return null;
        
        try
        {
          Geschaeftsjahr jahr = b.getGeschaeftsjahr(); // kommt bei Bedarf automatisch aus dem Cache
          
          // Checken, wir hierzu bereits das Mandantenkuerzel geladen haben
          String kuerzel = kuerzelCache.get(jahr.getID());
          if (kuerzel == null)
          {
            Mandant m = jahr.getMandant();
            kuerzel = StringUtils.trimToEmpty(m.getKuerzel());
            kuerzelCache.put(jahr.getID(),kuerzel);
          }
          
          return kuerzel.length() > 0 ? String.format("%s%04d", kuerzel, b.getBelegnummer()) : Integer.toString(b.getBelegnummer());
        }
        catch (RemoteException re)
        {
          Logger.error("unable to load beleg number",re);
        }
        return null;
      }
    
    });
  }
  
  /**
   * Liefert den Cache zum Lookup von Hibiscus Umsatz-ID zu Buchung.
   * @return der Cache.
   */
  private Map<String,Buchung> getCache()
  {
    if (this.umsatzCache != null)
      return this.umsatzCache;
    
    this.umsatzCache = new HashMap<String,Buchung>();
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
        umsatzCache.put(b.getHibiscusUmsatzID(),b);
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to fill lookup umsatzCache",e);
    }
    return this.umsatzCache;
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
    getCache().put(umsatzId,b);
  }
}
