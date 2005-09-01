/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/SteuerList.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/09/01 23:07:17 $
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
import de.willuhn.jameica.fibu.gui.menus.SteuerListMenu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit Steuersaetzen.
 */
public class SteuerList extends TablePart
{

  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public SteuerList(Action action) throws RemoteException
  {
    super(init(), action);
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Name"),"name");
    addColumn(i18n.tr("Steuersatz"),"satz",new CurrencyFormatter("%",Fibu.DECIMALFORMAT));
    addColumn(i18n.tr("Steuer-Sammelkonto"),"steuerkonto_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
          return k.getKontenrahmen().getName() + " - " + k.getKontonummer() + " [" + k.getName() + "]";
        }
        catch (RemoteException e)
        {
          Logger.error("unable to read konto",e);
          return "nicht ermittelbar";
        }
      }
    });
    setContextMenu(new SteuerListMenu());
  }
  
  /**
   * Initialisiert die Liste der Steuersaetze.
   * @return Liste der Steuersaetze.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Steuer.class);
    list.setOrder("order by steuerkonto_id");
    return list;
  }

}


/*********************************************************************
 * $Log: SteuerList.java,v $
 * Revision 1.3  2005/09/01 23:07:17  willuhn
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