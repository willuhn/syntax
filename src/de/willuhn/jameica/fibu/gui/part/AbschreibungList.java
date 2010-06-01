/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/AbschreibungList.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/06/01 16:37:22 $
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
import java.util.Date;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.AbschreibungListMenu;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Liste mit Abschreibungen zu einem Anlagevermoegen.
 */
public class AbschreibungList extends TablePart
{

  /**
   * ct.
   * @param a das Anlagevermoegen.
   * @param action
   * @throws RemoteException
   */
  public AbschreibungList(Anlagevermoegen a, Action action) throws RemoteException
  {
    super(a.getAbschreibungen(Settings.getActiveGeschaeftsjahr()), action);
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    addColumn(i18n.tr("Datum"),"buchung_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof AbschreibungsBuchung))
          return null;
        try
        {
          AbschreibungsBuchung b = (AbschreibungsBuchung) o;
          Date d = b.getDatum();
          if (d == null)
            return null;
          return Settings.DATEFORMAT.format(b.getDatum());
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading buchung",e);
          return null;
        }
      }
    });
    
    Mandant m = a.getMandant();
    
    addColumn(i18n.tr("Betrag"),"buchung_id", new CurrencyFormatter(m.getWaehrung(), Settings.DECIMALFORMAT) {
      public String format(Object o)
      {
        if (o == null || !(o instanceof AbschreibungsBuchung))
          return null;
        try
        {
          AbschreibungsBuchung b = (AbschreibungsBuchung) o;
          return super.format(new Double(b.getBetrag()));
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading buchung",e);
          return null;
        }
      }
    });
    
    addColumn(i18n.tr("Text"),"buchung_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof AbschreibungsBuchung))
          return null;
        try
        {
          AbschreibungsBuchung b = (AbschreibungsBuchung) o;
          return b.getText();
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading buchung",e);
          return null;
        }
      }
    });

    setContextMenu(new AbschreibungListMenu());
    setRememberColWidths(true);
    setRememberOrder(true);
  }

}


/*********************************************************************
 * $Log: AbschreibungList.java,v $
 * Revision 1.7  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.6  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.5  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/25 22:18:22  willuhn
 * @B bug 122
 *
 * Revision 1.2  2005/08/29 15:20:51  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/