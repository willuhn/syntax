/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.search;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.KontoNeu;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.search.Result;
import de.willuhn.jameica.search.SearchProvider;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Suchprovider fuer Konten.
 */
public class KontoSearchProvider implements SearchProvider
{

  /**
   * @see de.willuhn.jameica.search.SearchProvider#getName()
   */
  public String getName()
  {
    return Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N().tr("Konten");
  }

  /**
   * @see de.willuhn.jameica.search.SearchProvider#search(java.lang.String)
   */
  public List search(String search) throws RemoteException,
      ApplicationException
  {
    if (search == null || search.length() == 0)
      return null;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    if (jahr == null)
      return null; // Kein Geschaeftsjahr aktiv
    
    String text = "%" + search.toLowerCase() + "%";
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.setOrder("order by kontonummer desc");
    list.addFilter("LOWER(name) LIKE ? OR kontonummer LIKE ?",text,text);

    ArrayList results = new ArrayList();
    while (list.hasNext())
    {
      results.add(new MyResult((Konto)list.next()));
    }
    return results;
  }
  
  /**
   * Hilfsklasse fuer die formatierte Anzeige der Ergebnisse.
   */
  private class MyResult implements Result
  {
    private Konto konto = null;
    
    /**
     * ct.
     * @param konto
     */
    private MyResult(Konto konto)
    {
      this.konto = konto;
    }

    /**
     * @see de.willuhn.jameica.search.Result#execute()
     */
    public void execute() throws RemoteException, ApplicationException
    {
      new KontoNeu().handleAction(this.konto);
    }

    /**
     * @see de.willuhn.jameica.search.Result#getName()
     */
    public String getName()
    {
      try
      {
        I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

        return i18n.tr("{0}: {1}", new String[]{konto.getKontonummer(),
                                                konto.getName()});
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
 * $Log: KontoSearchProvider.java,v $
 * Revision 1.3  2011/08/08 10:44:35  willuhn
 * @C compiler warnings
 *
 * Revision 1.2  2009-07-03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2008/09/04 10:02:13  willuhn
 * @N Suchprovider
 *
 **********************************************************************/