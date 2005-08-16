/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/FinanzamtList.java,v $
 * $Revision: 1.2 $
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
import de.willuhn.jameica.fibu.gui.menus.FinanzamtListMenu;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 */
public class FinanzamtList extends TablePart
{

  /**
   * @param action
   * @throws RemoteException
   */
  public FinanzamtList(Action action) throws RemoteException
  {
    super(init(), action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Name"),"name");
    addColumn(i18n.tr("Strasse"),"strasse");
    addColumn(i18n.tr("Postfach"),"postfach");
    addColumn(i18n.tr("PLZ"),"plz");
    addColumn(i18n.tr("Ort"),"ort");
    setContextMenu(new FinanzamtListMenu());
  }
  
  private static GenericIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Finanzamt.class);
    list.setOrder("order by name desc");
    return list;
  }

}


/*********************************************************************
 * $Log: FinanzamtList.java,v $
 * Revision 1.2  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.1  2005/08/08 22:54:15  willuhn
 * @N massive refactoring
 *
 **********************************************************************/