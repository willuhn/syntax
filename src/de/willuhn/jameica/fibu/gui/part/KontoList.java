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
import java.util.List;
import java.util.Set;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.KontoListMenu;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.util.KontoUtil;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.ExtensionRegistry;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.DelayedListener;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit Konten.
 */
public class KontoList extends TablePart implements Extendable
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(KontoList.class);
  
  private String query                           = null;
  private TextInput search                       = null;
  private CheckboxInput onlyAccountsWithBookings = null;
  private CheckboxInput onlyAccountsWithSaldo    = null;
  private DelayedListener listener               = null;
  
  private List<Konto> list                       = null;

  private boolean filterEnabled                  = true;

  /**
   * @param mandant der Mandant.
   * @param list die Liste der Konten.
   * @param action
   * @throws RemoteException
   */
  public KontoList(Mandant mandant, GenericIterator list, Action action) throws RemoteException
  {
    this(mandant,list,null,action);
  }

  /**
   * @param mandant der Mandant.
   * @param list die Liste der Konten.
   * @param query optionale Angabe eines Suchbegriffes.
   * @param action
   * @throws RemoteException
   */
  public KontoList(Mandant mandant, GenericIterator list, String query, Action action) throws RemoteException
  {
    super(action);
    this.list = PseudoIterator.asList(list);
    this.query = query;
    
    this.listener = new DelayedListener(700,new Listener() {
      public void handleEvent(Event event)
      {
        reload();
      }
    });

    addColumn(i18n.tr("Kontonummer"),"kontonummer");
    addColumn(i18n.tr("Name"),"name");
    addColumn(i18n.tr("Kontoart"),"kontoArt");
    addColumn(i18n.tr("Steuer"),"steuer");
    addColumn(i18n.tr("Saldo"),"saldo", new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Settings.DECIMALFORMAT));
    setContextMenu(new KontoListMenu(mandant));
    setMulti(true);
    setRememberColWidths(true);
    setRememberOrder(true);
    setRememberState(true);

    setFormatter(new TableFormatter()
    {
      /**
       * @see de.willuhn.jameica.gui.formatter.TableFormatter#format(org.eclipse.swt.widgets.TableItem)
       */
      public void format(TableItem item)
      {
        try
        {
          if (item == null)
            return;
          Konto k = (Konto) item.getData();
          if (k.isUserObject())
            item.setForeground(Color.SUCCESS.getSWTColor());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to check konto",e);
        }
      }
    });
    
    // Initial laden
    this.reload();
    
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
   * Ueberschrieben, um noch weitere Details anzuzeigen.
   * @see de.willuhn.jameica.gui.parts.TablePart#paint(org.eclipse.swt.widgets.Composite)
   */
  public synchronized void paint(Composite parent) throws RemoteException
  {
    if (filterEnabled)
    {
      Container container = new SimpleContainer(parent);
      container.addHeadline(i18n.tr("Anzeige einschränken"));

      TextInput text = this.getText();
      
      container.addInput(text);
      container.addInput(this.getOnlyAccountsWithBookings());
      container.addInput(this.getOnlyAccountsWithSaldo());
      
      // Nach dem Rendern noch den delayed Listener dran haengen
      text.getControl().addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e)
        {
          listener.handleEvent(null);
        }
      });
    }

    super.paint(parent);
  }
  
  /**
   * Liefert eine Checkbox fuer "Nur Konten mit Buchungen anzeigen".
   * @return Checkbox.
   */
  private CheckboxInput getOnlyAccountsWithBookings()
  {
    if (this.onlyAccountsWithBookings != null)
      return this.onlyAccountsWithBookings;
    
    // Der Name des Parameters passt 
    this.onlyAccountsWithBookings = new CheckboxInput(settings.getBoolean("filter.checkbookings.enabled",false));
    this.onlyAccountsWithBookings.setName(i18n.tr("Nur Konten mit Buchungen anzeigen"));
    
    // Hier gibts kein Delay
    this.onlyAccountsWithBookings.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        reload();
      }
    });
    return this.onlyAccountsWithBookings;
  }
  
  /**
   * Liefert eine Checkbox fuer "Nur Konten mit Saldo != 0".
   * @return Checkbox.
   */
  private CheckboxInput getOnlyAccountsWithSaldo()
  {
    if (this.onlyAccountsWithSaldo != null)
      return this.onlyAccountsWithSaldo;
    
    this.onlyAccountsWithSaldo = new CheckboxInput(settings.getBoolean("filter.checksaldo.enabled",false));
    this.onlyAccountsWithSaldo.setName(i18n.tr("Nur Konten mit Saldo ungleich 0 anzeigen"));
    
    // Hier gibts kein Delay
    this.onlyAccountsWithSaldo.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        reload();
      }
    });
    return this.onlyAccountsWithSaldo;
  }

  /**
   * Liefert ein Eingabefeld fuer einen Suchbegriff.
   * @return Eingabefeld.
   */
  private TextInput getText()
  {
    if (this.search != null)
      return this.search;
    
    this.search = new TextInput(this.query);
    this.search.setName(i18n.tr("Bezeichnung oder Kto-Nr. enthält"));
    return this.search;
  }
    
  
  /**
   * Schaltet die Anzeige der Kontofilter an oder aus.
   * @param visible true, wenn die Kontofilter angezeigt werden sollen. Default: true.
   */
  public void setFilterVisible(boolean visible)
  {
    this.filterEnabled = visible;
  }
  
  /**
   * Fuehrt das Reload durch.
   */
  private void reload()
  {
    // Erstmal alle rausschmeissen
    this.removeAll();

    // Wir holen uns den aktuellen Text
    String text = (String) getText().getValue();
    if (text != null) text = text.toLowerCase();

    boolean checkBookings = ((Boolean)getOnlyAccountsWithBookings().getValue()).booleanValue();
    settings.setAttribute("filter.checkbookings.enabled",checkBookings);

    boolean checkSaldo = ((Boolean)getOnlyAccountsWithSaldo().getValue()).booleanValue();
    settings.setAttribute("filter.checksaldo.enabled",checkSaldo);

    String name = null;
    String nr   = null;

    try
    {
      final Geschaeftsjahr gj = Settings.getActiveGeschaeftsjahr();
      final Set<String> set = checkBookings ? KontoUtil.getKontenMitBuchungen(gj) : null;

      for (Konto k:this.list)
      {
        try
        {
          // BUGZILLA 128
          if (checkBookings && set != null && !set.contains(k.getID()))
            continue;
          
          if (checkSaldo)
          {
            final Double d = k.getSaldo(gj);
            if (Math.abs(d.doubleValue()) < 0.01d)
              continue;
          }

          // Was zum Filtern da?
          if (text == null || text.length() == 0)
          {
            // ne
            this.addItem(k);
            continue;
          }

          name = k.getName();
          nr   = k.getKontonummer();
          
          if (name.toLowerCase().indexOf(text) != -1 || nr.indexOf(text) != -1)
            this.addItem(k);
        }
        catch (RemoteException re)
        {
          Logger.error("unable to load account",re);
        }
      }
    }
    catch (RemoteException re)
    {
      Logger.error("unable to load accounts",re);
    }
  }
}
