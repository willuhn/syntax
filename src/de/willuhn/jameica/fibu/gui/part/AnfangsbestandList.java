/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/AnfangsbestandList.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/22 21:44:09 $
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
import de.willuhn.jameica.fibu.gui.menus.AnfangsbestandListMenu;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit den Anfangsbestaenden.
 */
public class AnfangsbestandList extends TablePart
{

  /**
   * @param m
   * @param action
   * @throws RemoteException
   */
  public AnfangsbestandList(Mandant m, Action action) throws RemoteException
  {
    super(init(m), action);

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Konto"),"konto_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
          return k.getKontonummer() + " [" + k.getName() + "]";
        }
        catch (RemoteException e)
        {
          Logger.error("unable to read konto",e);
          return "nicht ermittelbar";
        }
      }
    });
    addColumn(i18n.tr("Anfangsbestand"),"betrag", new CurrencyFormatter(m.getWaehrung(),Fibu.DECIMALFORMAT));
    setContextMenu(new AnfangsbestandListMenu());
  }

  /**
   * Liefert die Liste der Anfangsbestaende.
   * @param m Mandant.
   * @return Liste der Anfangsbestaende.
   * @throws RemoteException
   */
  private static GenericIterator init(Mandant m) throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Anfangsbestand.class);
    list.addFilter("mandant_id = " + m.getID());
    return list;
  }
}


/*********************************************************************
 * $Log: AnfangsbestandList.java,v $
 * Revision 1.2  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/