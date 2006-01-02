/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/SteuerList.java,v $
 * $Revision: 1.5 $
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
import java.util.ArrayList;

import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.SteuerListMenu;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
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
   * @param list
   * @param action
   */
  public SteuerList(GenericIterator list, Action action)
  {
    super(list, action);
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
    setFormatter(new TableFormatter()
    {
      /**
       * @see de.willuhn.jameica.gui.formatter.TableFormatter#format(org.eclipse.swt.widgets.TableItem)
       */
      public void format(TableItem item)
      {
        try
        {
          if (item == null)
            return;
          Steuer s = (Steuer) item.getData();
          if (s.isUserObject())
            item.setForeground(Color.SUCCESS.getSWTColor());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to check steuer",e);
        }
      }
    });
    setContextMenu(new SteuerListMenu());
  }
  
  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public SteuerList(Action action) throws RemoteException
  {
    this(init(), action);
  }
  
  /**
   * Initialisiert die Liste der Steuersaetze.
   * @return Liste der Steuersaetze.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Steuer.class);
    list.addFilter("mandant_id is null or mandant_id = " + Settings.getActiveGeschaeftsjahr().getMandant().getID());
    list.setOrder("order by name");
    ArrayList al = new ArrayList();
    Kontenrahmen soll = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
    while (list.hasNext())
    {
      Steuer s = (Steuer) list.next();
      Konto k = s.getSteuerKonto();
      if (k == null)
        continue;
      Kontenrahmen ist = k.getKontenrahmen();
      if (soll.equals(ist))
        al.add(s);
    }
    return PseudoIterator.fromArray((Steuer[]) al.toArray(new Steuer[al.size()]));
  }

}


/*********************************************************************
 * $Log: SteuerList.java,v $
 * Revision 1.5  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.4  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
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