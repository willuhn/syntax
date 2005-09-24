/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/MandantList.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/09/24 13:00:13 $
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
import de.willuhn.jameica.fibu.gui.menus.MandantListMenu;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit den Mandanten.
 */
public class MandantList extends TablePart
{

  /**
   * @param action
   * @throws RemoteException
   */
  public MandantList(Action action) throws RemoteException
  {
    super(init(), action);

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Name 1"),"name1");
    addColumn(i18n.tr("Name 2"),"name2");
    addColumn(i18n.tr("Firma"),"firma");
    addColumn(i18n.tr("Ort"),"ort");
    addColumn(i18n.tr("Steuernummer"),"steuernummer");
    // BUGZILLA 120
    setContextMenu(new MandantListMenu());
  }

  /**
   * Liefert die Liste der Mandanten.
   * @return Liste der Mandanten.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Mandant.class);    
    list.setOrder("order by firma desc");
    return list;
  }
}


/*********************************************************************
 * $Log: MandantList.java,v $
 * Revision 1.4  2005/09/24 13:00:13  willuhn
 * @B bugfixes according to bugzilla
 *
 * Revision 1.3  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.2  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.1  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 **********************************************************************/