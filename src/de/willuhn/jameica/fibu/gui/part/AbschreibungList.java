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
          return super.format(Double.valueOf(b.getBetrag()));
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
