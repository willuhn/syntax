/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/BuchungList.java,v $
 * $Revision: 1.25 $
 * $Date: 2009/07/03 10:52:19 $
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
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.menus.BuchungListMenu;
import de.willuhn.jameica.fibu.messaging.ObjectChangedMessage;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.ExtensionRegistry;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.DelayedListener;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Fertig vorkonfigurierte Tabelle mit Buchungen.
 */
public class BuchungList extends TablePart implements Extendable
{
  
  private I18N i18n             = null;

  private TextInput search      = null;
  private boolean showFilter    = true;

  private GenericIterator list  = null;
  private ArrayList buchungen   = null;
  
  private MessageConsumer mc    = new MyMessageConsumer();

  /**
   * ct.
   * @throws RemoteException
   */
  public BuchungList() throws RemoteException
  {
    this(new BuchungNeu());
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
    addColumn(i18n.tr("Haben-Konto"),"habenkonto_id", new KontoFormatter());
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
        BaseBuchung b = (BaseBuchung) item.getData();
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
    ExtensionRegistry.extend(this);
  }

  /**
   * @see de.willuhn.jameica.gui.extension.Extendable#getExtendableID()
   */
  public String getExtendableID()
  {
    return this.getClass().getName();
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
    {
      DBIterator hauptbuchungen = konto.getHauptBuchungen(jahr);
      Kontoart ka = konto.getKontoArt();
      if (ka != null && ka.getKontoArt() == Kontoart.KONTOART_STEUER)
      {
        DBIterator hilfsbuchungen = konto.getHilfsBuchungen(jahr);
        if (hauptbuchungen.size() == 0)
          return hilfsbuchungen;
        
        // Ein Steuerkonto enthaelt normalerweise nur automatisch
        // erzeugte Hilfsbuchungen. Da der User aber auch echte
        // Hauptbuchungen darauf erzeugen kann, muss die Liste
        // ggf. um die Hauptbuchungen ergaenzt werden.
        List l = new ArrayList();
        while (hilfsbuchungen.hasNext()) l.add(hilfsbuchungen.next());
        while (hauptbuchungen.hasNext()) l.add(hauptbuchungen.next());
        return PseudoIterator.fromArray((BaseBuchung[])l.toArray(new BaseBuchung[l.size()]));
      }
      return hauptbuchungen;
    }
    
    // Sonst die des aktuellen Geschaeftsjahres
    DBIterator list = jahr.getHauptBuchungen();
    list.setOrder("order by belegnummer desc");
    return list;
  }
  

  /**
   * Legt fest, ob der Filter angezeigt werden soll.
   * @param filter true, wenn der Filter angezeigt werden soll.
   * Default: true.
   */
  public void showFilter(boolean filter)
  {
    this.showFilter = filter;
  }
  
  /**
   * Aktualisiert die Tabelle
   */
  private synchronized void update()
  {
    GUI.getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          // Erstmal alle rausschmeissen
          removeAll();

          BaseBuchung a = null;

          // Wir holen uns den aktuellen Text
          String text = (String) search.getValue();

          boolean empty = text == null || text.length() == 0;
          if (!empty) text = text.toLowerCase();

          for (int i=0;i<buchungen.size();++i)
          {
            a = (BaseBuchung) buchungen.get(i);

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

  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public synchronized void paint(Composite parent) throws RemoteException
  {
    if (showFilter)
    {
      LabelGroup group = new LabelGroup(parent,i18n.tr("Filter"));

      // Eingabe-Feld fuer die Suche mit Button hinten dran.
      this.search = new TextInput("");
      group.addLabelPair(i18n.tr("Buchungstext enthält"), this.search);
      this.search.getControl().addKeyListener(new KL());
    }

    super.paint(parent);
    Application.getMessagingFactory().registerMessageConsumer(this.mc);
    parent.addDisposeListener(new DisposeListener()
    {
      public void widgetDisposed(DisposeEvent e)
      {
        Application.getMessagingFactory().unRegisterMessageConsumer(mc);
      }
    });

    if (showFilter)
    {
      // Wir kopieren den ganzen Kram in eine ArrayList, damit die
      // Objekte beim Filter geladen bleiben
      buchungen = new ArrayList();
      list.begin();
      while (list.hasNext())
      {
        BaseBuchung a = (BaseBuchung) list.next();
        buchungen.add(a);
      }
    }
  }

  private class KL extends KeyAdapter
  {
    private Listener forward = new DelayedListener(new Listener()
    {
      /**
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      public void handleEvent(Event event)
      {
        update();
      }

    });

    /**
     * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
     */
    public void keyReleased(KeyEvent e)
    {
      forward.handleEvent(null); // Das Event-Objekt interessiert uns eh nicht
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
  
  /**
   * erhaelt Updates ueber geaenderte Buchungen und aktualisiert die Tabelle live.
   */
  private class MyMessageConsumer implements MessageConsumer
  {
    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
     */
    public boolean autoRegister()
    {
      return false;
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
     */
    public Class[] getExpectedMessageTypes()
    {
      return new Class[]{ObjectChangedMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      ObjectChangedMessage m = (ObjectChangedMessage) message;
      final Object buchung = m.getData();
      if (buchung == null || !(buchung instanceof BaseBuchung))
        return;
      
      GUI.getDisplay().syncExec(new Runnable() {
        public void run()
        {
          try
          {
            int index = removeItem(buchung);
            if (index == -1)
              return; // Objekt war nicht in der Tabelle

            // Aktualisieren, in dem wir es neu an der gleichen Position eintragen
           addItem(buchung,index);

           // Wir markieren es noch in der Tabelle
           select(buchung);
          }
          catch (Exception e)
          {
            Logger.error("unable to add object to list",e);
          }
        }
      });
    }
  }
}


/*********************************************************************
 * $Log: BuchungList.java,v $
 * Revision 1.25  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.24.2.3  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.24.2.2  2009/06/23 11:04:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.24.2.1  2009/06/23 10:45:53  willuhn
 * @N Buchung nach Aenderung live aktualisieren
 *
 * Revision 1.24  2007/07/30 21:05:50  willuhn
 * @B typo
 *
 * Revision 1.23  2006/07/17 21:58:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2006/05/30 23:33:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.20  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
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