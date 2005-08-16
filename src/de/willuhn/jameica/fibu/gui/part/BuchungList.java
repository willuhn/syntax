/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/BuchungList.java,v $
 * $Revision: 1.5 $
 * $Date: 2005/08/16 23:14:35 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.part;

import java.rmi.RemoteException;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.BuchungListMenu;
import de.willuhn.jameica.fibu.rmi.BaseKonto;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Fertig vorkonfigurierte Tabelle mit Buchungen.
 */
public class BuchungList extends TablePart
{

  /**
   * ct.
   * @param konto
   * @param action
   * @throws RemoteException
   */
  public BuchungList(BaseKonto konto, Action action) throws RemoteException
  {
    super(init(konto), action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Datum"),"datum", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Beleg"),"belegnummer");
    addColumn(i18n.tr("Art"),"konto_id", new Formatter()
    {
      public String format(Object o)
      {
        if (o == null || !(o instanceof BaseKonto))
          return null;
        try
        {
          BaseKonto k = (BaseKonto) o;
          Kontoart ka = k.getKontoArt();
          if (ka == null)
            return null;
          return ka.getName();
        }
        catch (RemoteException e)
        {
          Logger.error("unable to detect konto art",e);
          return null;
        }
      }
    });
    addColumn(i18n.tr("Konto"),"konto_id", new KontoFormatter());
    addColumn(i18n.tr("Geldkonto"),"geldkonto_id", new KontoFormatter());
    addColumn(i18n.tr("Text"),"buchungstext");
    addColumn(i18n.tr("Netto-Betrag"),"betrag",new CurrencyFormatter(Settings.getActiveMandant().getWaehrung(), Fibu.DECIMALFORMAT));
    setContextMenu(new BuchungListMenu());
    setMulti(true);
  }
  
  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public BuchungList(Action action) throws RemoteException
  {
    this(null,action);
  }

  /**
   * Initialisiert die Liste der Buchungen.
   * @param konto
   * @return Liste der Buchungen
   * @throws RemoteException
   */
  private static GenericIterator init(BaseKonto konto) throws RemoteException
  {
    if (konto != null)
      return konto.getBuchungen();
    DBIterator list = Settings.getDBService().createList(Buchung.class);
    list.setOrder("order by id desc");
    return list;
  }
  

  /**
   * Formatiert ein Konto huebsch.
   */
  private class KontoFormatter implements Formatter
  {
    /**
     * @see de.willuhn.jameica.gui.formatter.Formatter#format(java.lang.Object)
     */
    public String format(Object o)
    {
      if (o == null)
        return null;
      if (! (o instanceof BaseKonto))
        return o.toString();
      BaseKonto k = (BaseKonto) o;
      try
      {
        return k.getKontonummer() + " [" + k.getName() + "]";
      }
      catch (RemoteException e)
      {
        Logger.error("unable to read konto",e);
        return null;
      }
    }
    
  }
}


/*********************************************************************
 * $Log: BuchungList.java,v $
 * Revision 1.5  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.4  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 22:54:15  willuhn
 * @N massive refactoring
 *
 **********************************************************************/