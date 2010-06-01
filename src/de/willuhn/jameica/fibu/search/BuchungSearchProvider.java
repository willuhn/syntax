/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/search/BuchungSearchProvider.java,v $
 * $Revision: 1.3 $
 * $Date: 2010/06/01 16:37:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.search;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.search.Result;
import de.willuhn.jameica.search.SearchProvider;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Suchprovider fuer Buchungen.
 */
public class BuchungSearchProvider implements SearchProvider
{
  private Geschaeftsjahr jahr = null;

  /**
   * @see de.willuhn.jameica.search.SearchProvider#getName()
   */
  public String getName()
  {
    return Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N().tr("Buchungen");
  }

  /**
   * @see de.willuhn.jameica.search.SearchProvider#search(java.lang.String)
   */
  public List search(String search) throws RemoteException,
      ApplicationException
  {
    if (search == null || search.length() == 0)
      return null;

    this.jahr = Settings.getActiveGeschaeftsjahr();
    if (this.jahr == null)
      return null; // Kein Geschaeftsjahr aktiv
    
    DBIterator list = this.jahr.getHauptBuchungen();
    list.setOrder("order by belegnummer desc");
    list.addFilter("LOWER(buchungstext) LIKE ?",new String[]{"%" + search.toLowerCase() + "%"});

    ArrayList results = new ArrayList();
    while (list.hasNext())
    {
      results.add(new MyResult((Buchung)list.next()));
    }
    return results;
  }
  
  /**
   * Hilfsklasse fuer die formatierte Anzeige der Ergebnisse.
   */
  private class MyResult implements Result
  {
    private Buchung buchung = null;
    
    /**
     * ct.
     * @param buchung
     */
    private MyResult(Buchung buchung)
    {
      this.buchung = buchung;
    }

    /**
     * @see de.willuhn.jameica.search.Result#execute()
     */
    public void execute() throws RemoteException, ApplicationException
    {
      new BuchungNeu().handleAction(this.buchung);
    }

    /**
     * @see de.willuhn.jameica.search.Result#getName()
     */
    public String getName()
    {
      try
      {
        I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

        Date datum    = buchung.getDatum();
        Konto soll    = buchung.getSollKonto();
        Konto haben   = buchung.getHabenKonto();
        String text   = buchung.getText();
        int nummer    = buchung.getBelegnummer();
        double betrag = buchung.getBetrag();
        
        return i18n.tr("#{0} vom {1}: {2} an {3}  {4} ({5})", new String[]{Integer.toString(nummer),
                                                                           Settings.DATEFORMAT.format(datum),
                                                                           soll.getKontonummer(),
                                                                           haben.getKontonummer(),
                                                                           new CurrencyFormatter(jahr.getMandant().getWaehrung(),Settings.DECIMALFORMAT).format(new Double(betrag)),
                                                                           text});
      }
      catch (RemoteException re)
      {
        Logger.error("unable to determin result name",re);
        return null;
      }
    }
    
  }
}


/*********************************************************************
 * $Log: BuchungSearchProvider.java,v $
 * Revision 1.3  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.2  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2008/09/04 10:02:13  willuhn
 * @N Suchprovider
 *
 **********************************************************************/