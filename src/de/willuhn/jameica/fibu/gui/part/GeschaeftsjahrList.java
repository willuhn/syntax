/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/GeschaeftsjahrList.java,v $
 * $Revision: 1.7 $
 * $Date: 2006/05/08 22:44:18 $
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

import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.GeschaeftsjahrListMenu;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Liste mit Geschaeftsjahren eines Mandanten.
 */
public class GeschaeftsjahrList extends TablePart
{

  /**
   * ct.
   * @param m der Mandant.
   * @param action
   * @throws RemoteException
   */
  public GeschaeftsjahrList(Mandant m, Action action) throws RemoteException
  {
    super(init(m), action);
    
    final I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Mandant"), "mandant_id");
    addColumn(i18n.tr("Kontenrahmen"), "kontenrahmen_id");
    addColumn(i18n.tr("Beginn"), "beginn", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Ende"), "ende", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Status"), "closed", new Formatter() {
      public String format(Object o)
      {
        if (o == null)
          return null;
        return ((Integer)o).intValue() == 1 ? i18n.tr("Geschlossen") : i18n.tr("Offen");
      }
    });
    setFormatter(new TableFormatter() {
      public void format(TableItem item)
      {
        Object o = item.getData();
        if (o == null || !(o instanceof Geschaeftsjahr))
          return;
        try
        {
          Geschaeftsjahr jahr = (Geschaeftsjahr) o;
          if (jahr.isClosed())
            item.setForeground(Color.COMMENT.getSWTColor());
          
          if (jahr.equals(Settings.getActiveGeschaeftsjahr()))
            item.setForeground(Color.SUCCESS.getSWTColor());
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading gj status",e);
        }
      }
    });
    setContextMenu(new GeschaeftsjahrListMenu(m));
  }
  
  /**
   * Initialisiert die Liste.
   * @param m
   * @return Liste.
   * @throws RemoteException
   */
  private static GenericIterator init(Mandant m) throws RemoteException
  {
    if (m == null)
      return PseudoIterator.fromArray(new Geschaeftsjahr[0]);
    DBIterator list = m.getGeschaeftsjahre();
    list.setOrder("order by beginn");
    return list;
  }
}


/*********************************************************************
 * $Log: GeschaeftsjahrList.java,v $
 * Revision 1.7  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.6  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.5  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/29 22:59:17  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:28  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/