/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/ext/hibiscus/UmsatzListMenu.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/10/12 21:51:10 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Erweitert das Kontextmenu der Umsatzliste.
 * BUGZILLA 140 http://www.willuhn.de/bugzilla/show_bug.cgi?id=140
 */
public class UmsatzListMenu implements Extension
{
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public UmsatzListMenu()
  {
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    if (extendable == null || !(extendable instanceof ContextMenu))
    {
      Logger.warn("invalid extendable (" + extendable.getClass().getName() + ", skipping extension");
      return;
    }
    
    ContextMenu menu = (ContextMenu) extendable;
    menu.addItem(ContextMenuItem.SEPARATOR);
    
    menu.addItem(new MyContextMenuItem(i18n.tr("Umsatz in SynTAX übernehmen..."), new Action() {
    
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof Umsatz))
        {
          Logger.warn("not a valid umsatz (" + context.getClass().getName() + ", skipping action");
          return;
        }
        
        try
        {
          Umsatz u = (Umsatz) context;
          final Buchung buchung = (Buchung) Settings.getDBService().createObject(Buchung.class,null);
          buchung.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
          buchung.setBetrag(Math.abs(u.getBetrag()));
          buchung.setText(u.getZweck());
          buchung.setDatum(u.getDatum());
          buchung.setHibiscusUmsatzID(u.getID());
          new BuchungNeu().handleAction(buchung);
        }
        catch (RemoteException re)
        {
          Logger.error("unable to create buchung",re);
          throw new ApplicationException(i18n.tr("Fehler beim Anlegen der Buchung"));
        }
        
      }
    
    }));
  }
  
  /**
   * Hilfsklasse, um den Menupunkt zu deaktivieren, wenn die Buchung bereits zugeordnet ist.
   */
  private class MyContextMenuItem extends CheckedContextMenuItem
  {
    /**
     * ct.
     * @param text
     * @param a
     */
    public MyContextMenuItem(String text, Action a)
    {
      super(text, a);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.CheckedContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o == null)
        return false;
      if (o instanceof Umsatz[])
        return false; // Nene, einer nach dem andern ;)
      
      Umsatz u = (Umsatz) o;
      boolean found = false;
      try
      {
        DBIterator list = Settings.getActiveGeschaeftsjahr().getHauptBuchungen();
        list.addFilter("hb_umsatz_id = ?",new Object[]{u.getID()});
        found = list.hasNext();
        
      }
      catch (Exception e)
      {
        Logger.error("unable to detect if buchung is allready assigned",e);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Prüfen, ob die Buchung bereits in SynTAX zugeordnet ist"), StatusBarMessage.TYPE_ERROR));
      }
      
      return !found && super.isEnabledFor(o);
    }
    
  }

}


/*********************************************************************
 * $Log: UmsatzListMenu.java,v $
 * Revision 1.2  2006/10/12 21:51:10  willuhn
 * @Uebernahme der Buchungen aus Hibiscus.
 *
 * Revision 1.1  2006/10/09 23:48:41  willuhn
 * @B bug 140
 *
 **********************************************************************/