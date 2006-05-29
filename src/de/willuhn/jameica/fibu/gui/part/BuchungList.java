/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/BuchungList.java,v $
 * $Revision: 1.19 $
 * $Date: 2006/05/29 13:02:30 $
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
import java.util.ArrayList;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.BuchungListMenu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Fertig vorkonfigurierte Tabelle mit Buchungen.
 */
public class BuchungList extends TablePart
{
  
  private I18N i18n             = null;

  private TextInput search      = null;

  private GenericIterator list  = null;
  private ArrayList buchungen   = null;

  /**
   * ct.
   * @param konto
   * @param action
   * @throws RemoteException
   */
  public BuchungList(Konto konto, Action action) throws RemoteException
  {
    this(init(konto), action);
  }
  
  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public BuchungList(Action action) throws RemoteException
  {
    this((Konto)null,action);
  }

  /**
   * ct.
   * @param buchungen die Liste der Buchungen.
   * @param action
   * @throws RemoteException
   */
  public BuchungList(GenericIterator buchungen, Action action) throws RemoteException
  {
    super(buchungen,action);
    this.list = buchungen;

    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Datum"),"datum", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Beleg"),"belegnummer");
    addColumn(i18n.tr("Text"),"buchungstext");
    addColumn(i18n.tr("Brutto-Betrag"),"brutto",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(), Fibu.DECIMALFORMAT));
    addColumn(i18n.tr("Netto-Betrag"),"betrag",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(), Fibu.DECIMALFORMAT));
    addColumn(i18n.tr("Soll-Konto"),"sollkonto_id", new KontoFormatter());
    addColumn(i18n.tr("Haben-Lonto"),"habenkonto_id", new KontoFormatter());
    addColumn(i18n.tr("Art"),"sollkonto_id", new Formatter()
    {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
          Kontoart ka = k.getKontoArt();
          if (ka == null)
            return null;
          return ka.getName();
        }
        catch (RemoteException e)
        {
          Logger.error("unable to detect konto art",e);
          return null;
        }
      }
    });
    setContextMenu(new BuchungListMenu());
    setMulti(true);
    setRememberColWidths(true);
    setRememberOrder(true);
    
    setFormatter(new TableFormatter() {
      public void format(TableItem item)
      {
        if (item == null)
          return;
        Buchung b = (Buchung) item.getData();
        if (b == null)
          return;
        try
        {
          if (b.isGeprueft())
            item.setForeground(Color.SUCCESS.getSWTColor());
          else
            item.setForeground(Color.WIDGET_FG.getSWTColor());
        }
        catch (Exception e)
        {
          Logger.error("unable to check buchung",e);
        }
      }
    });
  }

  /**
   * Initialisiert die Liste der Buchungen.
   * @param konto
   * @return Liste der Buchungen
   * @throws RemoteException
   */
  private static GenericIterator init(Konto konto) throws RemoteException
  {
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();

    // Wenn ein Konto angegeben ist, dann nur dessen Buchungen
    if (konto != null)
      return konto.getBuchungen(jahr);
    
    // Sonst die des aktuellen Geschaeftsjahres
    DBIterator list = jahr.getBuchungen();
    list.setOrder("order by belegnummer desc");
    return list;
  }
  

  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public synchronized void paint(Composite parent) throws RemoteException
  {
    LabelGroup group = new LabelGroup(parent,i18n.tr("Filter"));

    // Eingabe-Feld fuer die Suche mit Button hinten dran.
    this.search = new TextInput("");
    group.addLabelPair(i18n.tr("Buchungstext enthält"), this.search);

    this.search.getControl().addKeyListener(new KL());

    super.paint(parent);

    // Wir kopieren den ganzen Kram in eine ArrayList, damit die
    // Objekte beim Filter geladen bleiben
    buchungen = new ArrayList();
    list.begin();
    while (list.hasNext())
    {
      Buchung a = (Buchung) list.next();
      buchungen.add(a);
    }
  }

  private class KL extends KeyAdapter
  {
    private Thread timeout = null;
   
    /**
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     */
    public void keyReleased(KeyEvent e)
    {
      // Mal schauen, ob schon ein Thread laeuft. Wenn ja, muessen wir den
      // erst killen
      if (timeout != null)
      {
        timeout.interrupt();
        timeout = null;
      }
      
      // Ein neuer Timer
      timeout = new Thread("BuchungList")
      {
        public void run()
        {
          try
          {
            // Wir warten 900ms. Vielleicht gibt der User inzwischen weitere
            // Sachen ein.
            sleep(700l);

            // Ne, wir wurden nicht gekillt. Also machen wir uns ans Werk
            process();

          }
          catch (InterruptedException e)
          {
            return;
          }
          finally
          {
            timeout = null;
          }
        }
      };
      timeout.start();
    }
    
    private synchronized void process()
    {
      GUI.getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            // Erstmal alle rausschmeissen
            removeAll();

            Buchung a = null;

            // Wir holen uns den aktuellen Text
            String text = (String) search.getValue();

            boolean empty = text == null || text.length() == 0;
            if (!empty) text = text.toLowerCase();

            for (int i=0;i<buchungen.size();++i)
            {
              a = (Buchung) buchungen.get(i);

              // Was zum Filtern da?
              if (empty)
              {
                // ne
                BuchungList.this.addItem(a);
                continue;
              }

              String s = a.getText();
              
              s = s == null ? "" : s.toLowerCase();

              if (s.indexOf(text) != -1)
              {
                BuchungList.this.addItem(a);
              }
            }
            BuchungList.this.sort();
          }
          catch (Exception e)
          {
            Logger.error("error while loading address",e);
          }
        }
      });
    }
  }
  
  
  /**
   * Formatiert ein Konto huebsch.
   */
  private static class KontoFormatter implements Formatter
  {
    /**
     * @see de.willuhn.jameica.gui.formatter.Formatter#format(java.lang.Object)
     */
    public String format(Object o)
    {
      if (o == null)
        return null;
      if (! (o instanceof Konto))
        return o.toString();
      Konto k = (Konto) o;
      try
      {
        String name = k.getName();
        if (name.length() > 15)
          name = name.substring(0,10) + "...";
        return k.getKontonummer() + " [" + name + "]";
      }
      catch (RemoteException e)
      {
        Logger.error("unable to read konto",e);
        return null;
      }
    }
    
  }
}


/*********************************************************************
 * $Log: BuchungList.java,v $
 * Revision 1.19  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.18  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 * Revision 1.17  2006/05/07 16:40:12  willuhn
 * @N Suchfilter
 *
 * Revision 1.16  2006/05/07 16:27:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2006/01/09 01:17:12  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.12  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.11  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.10  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.9  2005/08/25 21:58:58  willuhn
 * @N SKR04
 *
 * Revision 1.8  2005/08/24 23:02:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.6  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.5  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.4  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.1  2005/08/08 22:54:15  willuhn
 * @N massive refactoring
 *
 **********************************************************************/