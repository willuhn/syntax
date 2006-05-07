/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/AnlagevermoegenList.java,v $
 * $Revision: 1.7 $
 * $Date: 2006/05/07 16:27:37 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.AnlagevermoegenListMenu;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit dem Anlagevermoegen.
 */
public class AnlagevermoegenList extends TablePart
{

  /**
   * @param action
   * @throws RemoteException
   */
  public AnlagevermoegenList(Action action) throws RemoteException
  {
    super(init(), action);

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Konto"),"konto_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
          return k.getKontonummer() + " [" + k.getName() + "]";
        }
        catch (RemoteException e)
        {
          Logger.error("unable to read konto",e);
          return "nicht ermittelbar";
        }
      }
    });
    addColumn(i18n.tr("Bezeichnung"),"name");
    addColumn(i18n.tr("Anschaffungsdatum"),"anschaffungsdatum", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Anschaffungskosten"),"anschaffungskosten", new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Fibu.DECIMALFORMAT));
    addColumn(i18n.tr("Nutzungsdauer"),"nutzungsdauer");
    addColumn(i18n.tr("Restwert"),"restwert",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Fibu.DECIMALFORMAT));
    setContextMenu(new AnlagevermoegenListMenu());
    setRememberOrder(true);
  }
  
  /**
   * Erzeugt die Liste des Anlagevermoegens.
   * @return Liste des Anlagevermoegens.
   * @throws RemoteException
   */
  private static DBIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Anlagevermoegen.class);
    list.setOrder("order by anschaffungsdatum desc");
    return list;
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenList.java,v $
 * Revision 1.7  2006/05/07 16:27:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2006/01/09 01:40:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/10/06 22:27:17  willuhn
 * @N KontoInput
 *
 * Revision 1.4  2005/09/01 23:28:15  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.2  2005/08/29 15:20:51  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/