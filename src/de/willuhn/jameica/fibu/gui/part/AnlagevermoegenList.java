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

import org.eclipse.swt.widgets.TableItem;

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
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
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
    addColumn(i18n.tr("Anschaffungsdatum"),"anschaffungsdatum", new DateFormatter(Settings.DATEFORMAT));
    addColumn(i18n.tr("Anschaffungskosten"),"anschaffungskosten", new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Settings.DECIMALFORMAT));
    addColumn(i18n.tr("Nutzungsdauer"),"nutzungsdauer");
    addColumn(i18n.tr("Restwert"),"restwert",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Settings.DECIMALFORMAT));
    setContextMenu(new AnlagevermoegenListMenu());
    setRememberColWidths(true);
    setRememberOrder(true);
    
    setFormatter(new TableFormatter() {
      public void format(TableItem item)
      {
        try
        {
          Anlagevermoegen a = (Anlagevermoegen) item.getData();
          if (a == null)
            return;
          item.setForeground(a.getStatus() == Anlagevermoegen.STATUS_BESTAND ? Color.FOREGROUND.getSWTColor() : Color.COMMENT.getSWTColor());
        }
        catch (Exception e)
        {
          Logger.error("unable to format item",e);
        }
      }
    });
  }
  
  /**
   * Erzeugt die Liste des Anlagevermoegens.
   * @return Liste des Anlagevermoegens.
   * @throws RemoteException
   */
  private static DBIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Anlagevermoegen.class);
    list.addFilter("mandant_id = " + Settings.getActiveGeschaeftsjahr().getMandant().getID());
    list.setOrder("order by anschaffungsdatum desc");
    return list;
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenList.java,v $
 * Revision 1.11  2010/09/20 10:27:36  willuhn
 * @N Neuer Status fuer Anlagevermoegen - damit kann ein Anlagegut auch dann noch in der Auswertung erscheinen, wenn es zwar abgeschrieben ist aber sich noch im Bestand befindet. Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=69910#69910
 *
 * Revision 1.10  2010-06-01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.9  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.8.2.1  2008/07/03 09:56:04  willuhn
 * @C Nur Anlagevermoegen des aktiven Mandanten anzeigen
 *
 * Revision 1.8  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
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