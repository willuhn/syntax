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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import de.willuhn.jameica.fibu.gui.menus.BuchungListMenu;
import de.willuhn.jameica.fibu.messaging.ObjectChangedMessage;
import de.willuhn.jameica.fibu.messaging.ObjectImportedMessage;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.util.BuchungUtil;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.ExtensionRegistry;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.MultiInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.DelayedListener;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.gui.util.Font;
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
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static de.willuhn.jameica.system.Settings settings = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getSettings();

  private Konto konto               = null;
  private GenericIterator buchungen = null;
  private TextInput search          = null;
  private DateInput from            = null;
  private DateInput to              = null;

  private MessageConsumer mcChanged    = new ChangedMessageConsumer();
  private MessageConsumer mcImported   = new ImportedMessageConsumer();

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
    this(init(konto,getConfiguredFrom(konto),getConfiguredTo(konto)), action);
    this.konto = konto;
  }
  
  /**
   * ct.
   * @param buchungen die Liste der Buchungen.
   * @param action
   * @throws RemoteException
   */
  private BuchungList(GenericIterator buchungen, Action action) throws RemoteException
  {
    super(buchungen,action);
    this.buchungen = buchungen;
    
    final Geschaeftsjahr gj = Settings.getActiveGeschaeftsjahr();
    final CurrencyFormatter cf = new CurrencyFormatter(gj.getMandant().getWaehrung(), Settings.DECIMALFORMAT);
    final Map<String,Double> sums = BuchungUtil.getNebenbuchungSummen(gj,null,null);
    final Map<String,Double[]> splitSums = BuchungUtil.getSplitbuchungenSummen(gj,null,null,sums);

    addColumn(i18n.tr("Datum"),"datum", new DateFormatter(Settings.DATEFORMAT));
    addColumn(i18n.tr("Beleg"),"belegnummer");
    addColumn(i18n.tr("Text"),"buchungstext");
    addColumn(i18n.tr("Brutto-Betrag"),null);
    addColumn(i18n.tr("Netto-Betrag"),"betrag",cf);
    addColumn(i18n.tr("Steuer"),"steuer_id");
    addColumn(i18n.tr("Soll-Konto"),"sollKonto", new KontoFormatter());
    addColumn(i18n.tr("Haben-Konto"),"habenKonto", new KontoFormatter());
    addColumn(i18n.tr("Art"),"sollKonto", new Formatter()
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
    setRememberState(true);
    
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
          double result = b.getBetrag();
          final Double[] split = splitSums.get(b.getID());
          if (split != null) {
              item.setText(4,cf.format(split[0].doubleValue()));
              item.setText(3,cf.format(split[1].doubleValue()));
          }
          else {
	          final Double add = sums.get(b.getID());
	          if (add != null)
	            result += add.doubleValue();
	          item.setText(3,cf.format(result));
          }
        }
        catch (RemoteException re)
        {
          Logger.error("unable to calculate gross value",re);
        }

        try
        {
          //Bei Splitbuchungen keine Konten anzeigen, da die teilbuchungen andere Konten haben können
          if(splitSums.get(b.getID()) != null) {
              item.setText(5,cf.format(null));
              item.setText(6,cf.format(null));
              item.setText(7,cf.format(null));
          }
          if(b instanceof Buchung && ((Buchung)b).getSplitHauptBuchung() != null)
        	  item.setFont(Font.ITALIC.getSWTFont());
          else if (b instanceof Buchung && splitSums.get(b.getID()) != null)
          	  item.setFont(Font.BOLD.getSWTFont());
          if (b.isGeprueft())
            item.setForeground(Color.SUCCESS.getSWTColor());
          else
            item.setForeground(Color.FOREGROUND.getSWTColor());
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
   * @param konto Optional.
   * @param von Startdatum. Optional.
   * @param bis Enddatum. Optional.
   * @return Liste der Buchungen
   * @throws RemoteException
   */
  private static GenericIterator init(Konto konto, Date von, Date bis) throws RemoteException
  {
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    
    // Start- und End-Datum checken, falls angegeben. Wenn sie sich ausserhalb des 
    // Geschaeftsjahres befinden, dann zuruecksetzen
    if (von != null && !jahr.check(von))
      von = jahr.getBeginn();
    
    if (bis != null && !jahr.check(bis))
      bis = jahr.getEnde();

    // Wenn ein Konto angegeben ist, dann nur dessen Buchungen
    if (konto != null)
    {
      DBIterator hauptbuchungen;
      Kontoart ka = konto.getKontoArt();
      if (ka != null && ka.getKontoArt() == Kontoart.KONTOART_STEUER)
      {
        DBIterator hilfsbuchungen = konto.getHilfsBuchungen(jahr, von, bis);
        hauptbuchungen= konto.getHauptBuchungen(jahr, von, bis);
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
      hauptbuchungen= konto.getHauptBuchungen(jahr, von, bis);
      return hauptbuchungen;
    }
    
    // Sonst die des aktuellen Geschaeftsjahres
    DBIterator list = jahr.getHauptBuchungen(von, bis,true);
    list.addFilter("split_id is NULL");
    list.setOrder("order by belegnummer desc");
    return list;
  }
  
  /**
   * Aktualisiert die Tabelle
   * @param reload true, wenn die Buchungen aus der Datenbank neu geladen werden sollen.
   */
  private synchronized void update(final boolean reload)
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
          String text = (String) getSearch().getValue();
          Date start = (Date) getFrom().getValue();
          Date end   = (Date) getTo().getValue();

          boolean empty = text == null || text.length() == 0;
          if (!empty) text = text.toLowerCase();

          // Daten neu laden?
          if (reload)
            buchungen = init(konto,start,end);
          
          buchungen.begin();
          while (buchungen.hasNext())
          {
            a = (BaseBuchung) buchungen.next();

            // Was zum Filtern da?
            if (empty)
            {
              // ne
              addItem(a);
              continue;
            }

            String s = a.getText();
            String nr = Integer.toString(a.getBelegnummer());
            s = s == null ? "" : s.toLowerCase();
            
            if (s.contains(text) || nr.contains(text))
              addItem(a);
          }
          
          // Neu sortieren
          sort();
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
    Container group = new SimpleContainer(parent);
    
    if (this.konto == null) // Wenn ein Konto angegeben ist, befinden wir uns auf der Detailseite des Kontos
      group.addHeadline(i18n.tr("Anzeige einschränken"));

    // Eingabe-Feld fuer die Suche mit Button hinten dran.
    this.search = this.getSearch();
    group.addInput(this.search);
    this.search.getControl().addKeyListener(new KL()); // Listener fuer die Aktualisierung der Suche
    
    MultiInput m = new MultiInput(this.getFrom(),this.getTo());
    m.setName(i18n.tr("Zeitraum von"));
    group.addInput(m);

    super.paint(parent);
    Application.getMessagingFactory().registerMessageConsumer(this.mcChanged);
    Application.getMessagingFactory().registerMessageConsumer(this.mcImported);
    parent.addDisposeListener(new DisposeListener()
    {
      public void widgetDisposed(DisposeEvent e)
      {
        Application.getMessagingFactory().unRegisterMessageConsumer(mcChanged);
        Application.getMessagingFactory().unRegisterMessageConsumer(mcImported);
      }
    });
  }
  
  /**
   * Liefert den Parameter-Suffix.
   * @param konto optionale Angabe des Konto.
   * @return Suffix.
   */
  private static String getSuffix(Konto konto)
  {
    try
    {
      return konto != null ? konto.getID() : "";
    }
    catch (Exception e) {}
    return "";
  }
  
  /**
   * Liefert ein Eingabefeld mit dem Suchbegriff.
   * @return Eingabefeld.
   */
  private TextInput getSearch()
  {
    if (this.search != null)
      return this.search;
    
    this.search = new TextInput(settings.getString("buchungen.search.text" + getSuffix(this.konto),""));
    this.search.setName(i18n.tr("Buchungstext enthält"));
    this.search.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        settings.setAttribute("buchungen.search.text" + getSuffix(konto),(String) search.getValue());
      }
    });
    
    this.update(false); // einmal initial den Filter ausloesen
    return this.search;
  }
  
  /**
   * Liefert ein Auswahlfeld fuer das Startdatum.
   * @return ein Auswahlfeld fuer das Startdatum.
   */
  private DateInput getFrom()
  {
    if (this.from != null)
      return this.from;
    
    this.from = new DateInput(getConfiguredFrom(this.konto));
    this.from.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        Date d = (Date) from.getValue();
        settings.setAttribute("buchungen.search.from" + getSuffix(konto),d != null ? Settings.DATEFORMAT.format(d) : null);
        update(true);
      }
    });
    return this.from;
  }
  
  /**
   * Liefert ein Auswahlfeld fuer das Enddatum.
   * @return ein Auswahlfeld fuer das Enddatum.
   */
  private DateInput getTo()
  {
    if (this.to != null)
      return this.to;
    
    this.to = new DateInput(getConfiguredTo(this.konto));
    this.to.setName(i18n.tr("bis"));
    this.to.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        Date d = (Date) to.getValue();
        settings.setAttribute("buchungen.search.to" + getSuffix(konto),d != null ? Settings.DATEFORMAT.format(d) : null);
        update(true);
      }
    });
    return this.to;
  }

  /**
   * Liefert das konfigurierte Start-Datum.
   * @param konto optionale Angabe des Konto.
   * @return das konfigurierte Start-Datum.
   */
  private static Date getConfiguredFrom(Konto k)
  {
    try
    {
      final Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      
      Date d = getConfiguredDate("buchungen.search.from" + getSuffix(k));
      if (d != null && jahr.check(d))
        return d;
      
      return jahr.getBeginn();
    }
    catch (Exception e)
    {
      // ignore
    }
    return null;
  }
  
  /**
   * Liefert das konfigurierte End-Datum.
   * @param konto optionale Angabe des Konto.
   * @return das konfigurierte End-Datum.
   */
  private static Date getConfiguredTo(Konto k)
  {
    try
    {
      final Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();

      Date d = getConfiguredDate("buchungen.search.to" + getSuffix(k));
      if (d != null && jahr.check(d))
        return d;

      return jahr.getEnde();
    }
    catch (Exception e)
    {
      // ignore
    }
    return null;
  }
  
  /**
   * Liefert das vorkonfigurierte Datum.
   * @param s der Parameter, in dem das Datum gespeichert ist.
   * @return das Datum oder NULL.
   */
  private static Date getConfiguredDate(String s)
  {
    try
    {
      String v = settings.getString(s,null);
      if (v != null)
        return Settings.DATEFORMAT.parse(v);
    }
    catch (Exception e)
    {
      // ignore
    }
    return null;
  }

  private class KL extends KeyAdapter
  {
    private Listener forward = new DelayedListener(700,new Listener()
    {
      /**
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      public void handleEvent(Event event)
      {
        update(false);
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
  private class ChangedMessageConsumer implements MessageConsumer
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


  /**
   * Listener, der das Neuladen der Buchungen uebernimmt.
   */
  private class ImportedListener implements Listener
  {
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      update(true);
    }
  }
  
  /**
   * Mit dem Consumer werden wir ueber importierte Datensaetze informiert.
   */
  private class ImportedMessageConsumer implements MessageConsumer
  {
    private DelayedListener delay = new DelayedListener(new ImportedListener());
    
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
      return new Class[]{ObjectImportedMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      delay.handleEvent(null);
    }
  }

}
