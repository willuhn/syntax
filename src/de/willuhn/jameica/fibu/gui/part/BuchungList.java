/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/BuchungList.java,v $
 * $Revision: 1.11 $
 * $Date: 2005/08/29 22:26:19 $
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
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Konto;
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
  public BuchungList(Konto konto, Action action) throws RemoteException
  {
    super(init(konto), action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Datum"),"datum", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Beleg"),"belegnummer");
    addColumn(i18n.tr("Text"),"buchungstext");
    addColumn(i18n.tr("Art"),"sollkonto_id", new Formatter()
    {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
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
    addColumn(i18n.tr("Soll-Konto"),"sollkonto_id", new KontoFormatter());
    addColumn(i18n.tr("Haben-Lonto"),"habenkonto_id", new KontoFormatter());
    addColumn(i18n.tr("Brutto-Betrag"),"brutto",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(), Fibu.DECIMALFORMAT));
    addColumn(i18n.tr("Netto-Betrag"),"betrag",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(), Fibu.DECIMALFORMAT));
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
  private static GenericIterator init(Konto konto) throws RemoteException
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
  private static class KontoFormatter implements Formatter
  {
    /**
     * @see de.willuhn.jameica.gui.formatter.Formatter#format(java.lang.Object)
     */
    public String format(Object o)
    {
      if (o == null)
        return null;
      if (! (o instanceof Konto))
        return o.toString();
      Konto k = (Konto) o;
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
 * Revision 1.11  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.10  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.9  2005/08/25 21:58:58  willuhn
 * @N SKR04
 *
 * Revision 1.8  2005/08/24 23:02:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.6  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
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