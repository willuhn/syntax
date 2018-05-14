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

import de.willuhn.datasource.GenericIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.AnfangsbestandListMenu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit den Anfangsbestaenden.
 */
public class AnfangsbestandList extends TablePart
{

  /**
   * @param action
   * @throws RemoteException
   */
  public AnfangsbestandList(Action action) throws RemoteException
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
    addColumn(i18n.tr("Anfangsbestand"),"betrag", new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Settings.DECIMALFORMAT));
    setContextMenu(new AnfangsbestandListMenu());
    setRememberColWidths(true);
    setRememberOrder(true);
  }
  
  /**
   * Initialisiert die Liste der Anfangsbestaende.
   * @return Liste der Anfangsbestaende.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    return Settings.getActiveGeschaeftsjahr().getAnfangsbestaende();
  }
}


/*********************************************************************
 * $Log: AnfangsbestandList.java,v $
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
 * Revision 1.5  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.3  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.2  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/