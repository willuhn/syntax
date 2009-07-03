/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/ext/hibiscus/UmsatzListPart.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/07/03 10:52:19 $
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
import java.util.Hashtable;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
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
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public UmsatzListPart()
  {
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    if (extendable == null || !(extendable instanceof TablePart))
    {
      Logger.warn("invalid extendable (" + extendable.getClass().getName() + ", skipping extension");
      return;
    }
    TablePart table = (TablePart) extendable;

    final Hashtable matches = new Hashtable();

    try
    {
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      if (jahr == null)
      {
        // noch kein Mandant/Geschaeftsjahr eingerichtet
        return;
      }
      DBIterator list = jahr.getHauptBuchungen();
      list.addFilter("hb_umsatz_id is not null");
      while (list.hasNext())
      {
        Buchung b = (Buchung) list.next();
        if (b.getHibiscusUmsatzID() == null)
          continue;
        matches.put(b.getHibiscusUmsatzID(),b);
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to load assignments",e);
    }
    

    table.addColumn(i18n.tr("SynTAX-Belegnummer"),"id-int", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Integer))
          return null;

        Buchung b = (Buchung) matches.get(o.toString());
        if (b == null)
          return null;
        try
        {
          String s = Integer.toString(b.getBelegnummer());
          System.out.println("+++" + s);
          return s;
        }
        catch (RemoteException re)
        {
          Logger.error("unable to load beleg number",re);
        }
        return null;
      }
    
    });
    
  }

}


/*********************************************************************
 * $Log: UmsatzListPart.java,v $
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