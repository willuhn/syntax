/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.part;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.SteuerListMenu;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
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
   * @param mandant
   * @param action
   * @throws RemoteException
   */
  public SteuerList(Mandant mandant, Action action) throws RemoteException
  {
    super(init(mandant), action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Name"),"name");
    addColumn(i18n.tr("Steuersatz"),"satz",new CurrencyFormatter("%",Settings.DECIMALFORMAT));
    addColumn(i18n.tr("Steuer-Sammelkonto"),"steuerKonto", new Formatter() {
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
    setRememberColWidths(true);
    setRememberOrder(true);
  }
  
  /**
   * Initialisiert die Liste der Steuersaetze.
   * @param m Mandant.
   * @return Liste der Steuersaetze.
   * @throws RemoteException
   */
  private static GenericIterator init(Mandant m) throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Steuer.class);
    if (m != null)
      list.addFilter("mandant_id is null or mandant_id = " + m.getID());
    else
      list.addFilter("mandant_id is null");
    
    list.setOrder("order by name");
    List<Steuer> al = new ArrayList<Steuer>();
    Kontenrahmen soll = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
    while (list.hasNext())
    {
      Steuer s = (Steuer) list.next();
      
      // Steuersatz hat kein Steuerkonto -> ignorieren
      Konto k = s.getSteuerKonto();
      if (k == null)
        continue;

      // Jetzt muss entweder der Kontenrahmen oder der Mandant passen
      Mandant mSoll = s.getMandant();
      if (mSoll != null && m != null && mSoll.equals(m))
      {
        al.add(s);
        continue;
      }
      
      Kontenrahmen ist = k.getKontenrahmen();
      if (soll.equals(ist))
        al.add(s);
    }
    return PseudoIterator.fromArray(al.toArray(new Steuer[al.size()]));
  }

}


/*********************************************************************
 * $Log: SteuerList.java,v $
 * Revision 1.9  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.8  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.6  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
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