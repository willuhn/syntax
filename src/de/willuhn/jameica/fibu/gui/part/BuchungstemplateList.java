/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/BuchungstemplateList.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/02 15:18:29 $
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
import de.willuhn.jameica.fibu.gui.menus.BuchungstemplateListMenu;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit Buchungs-Vorlagen.
 */
public class BuchungstemplateList extends TablePart
{

  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public BuchungstemplateList(Action action) throws RemoteException
  {
    super(init(), action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Bezeichnung"),"name");
    addColumn(i18n.tr("Buchungstext"),"buchungstext");
    addColumn(i18n.tr("Soll-Konto"),"sollkonto_id", new Formatter() {
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
    addColumn(i18n.tr("Haben-Konto"),"habenkonto_id", new Formatter() {
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
    setContextMenu(new BuchungstemplateListMenu());
  }
  
  /**
   * Initialisiert die Liste der Buchungsvorlagen.
   * @return Liste der Buchungsvorlagen.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Buchungstemplate.class);
    list.setOrder("order by name");
    return list;
  }

}


/*********************************************************************
 * $Log: BuchungstemplateList.java,v $
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/