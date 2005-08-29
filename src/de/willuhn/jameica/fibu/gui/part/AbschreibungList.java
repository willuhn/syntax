/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/AbschreibungList.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/29 14:26:56 $
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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
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
    super(a.getAbschreibungen(), action);
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    addColumn(i18n.tr("Datum"),"buchung_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Buchung))
          return null;
        try
        {
          Buchung b = (Buchung) o;
          return Fibu.DATEFORMAT.format(b.getDatum());
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading buchung",e);
          return null;
        }
      }
    });
    
    Mandant m = a.getMandant();
    
    addColumn(i18n.tr("Betrag"),"buchung_id", new CurrencyFormatter(m.getWaehrung(), Fibu.DECIMALFORMAT) {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Buchung))
          return null;
        try
        {
          Buchung b = (Buchung) o;
          return super.format(b.getAttribute("betrag"));
        }
        catch (RemoteException e)
        {
          Logger.error("error while reading buchung",e);
          return null;
        }
      }
    });
  }

}


/*********************************************************************
 * $Log: AbschreibungList.java,v $
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/