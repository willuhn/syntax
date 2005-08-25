/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/KontoList.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/25 23:00:02 $
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
import de.willuhn.jameica.fibu.gui.menus.KontoListMenu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit Konten.
 */
public class KontoList extends TablePart
{

  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public KontoList(Action action) throws RemoteException
  {
    this(null,action);
  }
  
  /**
   * @param list
   * @param action
   * @throws RemoteException
   */
  public KontoList(GenericIterator list, Action action) throws RemoteException
  {
    super(list == null ? init() : list, action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Kontonummer"),"kontonummer");
    addColumn(i18n.tr("Name"),"name");
    addColumn(i18n.tr("Kontoart"),"kontoart_id");
    addColumn(i18n.tr("Steuer"),"steuer_id");
    addColumn(i18n.tr("Saldo"),"saldo", new CurrencyFormatter(Settings.getActiveMandant().getWaehrung(),Fibu.DECIMALFORMAT));
    setContextMenu(new KontoListMenu());
    setMulti(true);
  }
  
  /**
   * initialisiert eine Default-Liste mit Konten.
   * @return Liste der Konten.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    DBIterator konten = Settings.getDBService().createList(Konto.class);
    konten.setOrder("order by kontonummer");
    return konten;
  }

}


/*********************************************************************
 * $Log: KontoList.java,v $
 * Revision 1.4  2005/08/25 23:00:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/