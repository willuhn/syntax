/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/KontoList.java,v $
 * $Revision: 1.17.2.2 $
 * $Date: 2009/06/24 10:35:55 $
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

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.KontoListMenu;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.ExtensionRegistry;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit Konten.
 */
public class KontoList extends TablePart implements Extendable
{
  
  private I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  private TextInput search      = null;
  private CheckboxInput filter  = null;
  
  private GenericIterator list  = null;

  private boolean filterEnabled = true;

  /**
   * @param list
   * @param action
   * @throws RemoteException
   */
  public KontoList(GenericIterator list, Action action) throws RemoteException
  {
    super(list,action);
    this.list = list;

    addColumn(i18n.tr("Kontonummer"),"kontonummer");
    addColumn(i18n.tr("Name"),"name");
    addColumn(i18n.tr("Kontoart"),"kontoart_id");
    addColumn(i18n.tr("Steuer"),"steuer_id");
    addColumn(i18n.tr("Saldo"),"saldo", new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(),Fibu.DECIMALFORMAT));
    setContextMenu(new KontoListMenu());
    setMulti(true);
    setRememberColWidths(true);
    setRememberOrder(true);

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
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    if (filterEnabled)
    {
      SimpleContainer container = new SimpleContainer(parent);
      
      this.filter = new CheckboxInput(false);
      this.search = new TextInput("");
      
      container.addLabelPair(i18n.tr("Bezeichnung oder Kto-Nr. enthält"), this.search);
      container.addCheckbox(this.filter,i18n.tr("Nur Konten mit Buchungen anzeigen"));

      KL kl = new KL();
      this.search.getControl().addKeyListener(kl);
      ((Button)this.filter.getControl()).addSelectionListener(kl);
    }

    super.paint(parent);
    
  }
  
  private class KL extends KeyAdapter implements SelectionListener
  {
    private Thread timeout = null;
    private long count = 900l;
    
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
      timeout = new Thread()
      {
        public void run()
        {
          try
          {
            // Wir warten 900ms. Vielleicht gibt der User inzwischen weitere
            // Sachen ein.
            sleep(count);
            
            // Ne, wir wurden nicht gekillt. Also machen wir uns ans Werk

            GUI.getDisplay().syncExec(new Runnable()
            {
              public void run()
              {
                try
                {
                  // Erstmal alle rausschmeissen
                  removeAll();

                  list.begin();

                  // Wir holen uns den aktuellen Text
                  String text = (String) search.getValue();
                  if (text != null) text = text.toLowerCase();

                  boolean checkSaldo = ((Boolean)filter.getValue()).booleanValue();

                  Konto k     = null;
                  String name = null;
                  String nr   = null;

                  while (list.hasNext())
                  {
                    k = (Konto) list.next();

                    // BUGZILLA 128
                    if (checkSaldo && k.getNumBuchungen(Settings.getActiveGeschaeftsjahr()) == 0)
                      continue;

                    // Was zum Filtern da?
                    if (text == null || text.length() == 0)
                    {
                      // ne
                      addItem(k);
                      continue;
                    }

                    name = k.getName();
                    nr   = k.getKontonummer();
                    
                    if (name.toLowerCase().indexOf(text) != -1 || nr.indexOf(text) != -1)
                      addItem(k);
                    
                  }
                }
                catch (Exception e)
                {
                  Logger.error("error while loading konto",e);
                }
              }
            });
          }
          catch (InterruptedException e)
          {
            return;
          }
          finally
          {
            timeout = null;
            count = 900l;
          }
        }
      };
      timeout.start();
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e)
    {
      // Beim Klick auf die Checkbox muessen wir nichts warten
      count = 0l;
      keyReleased(null);
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }
  }

  /**
   * Schaltet die Anzeige der Kontofilter an oder aus.
   * @param visible true, wenn die Kontofilter angezeigt werden sollen. Default: true.
   */
  public void setFilterVisible(boolean visible)
  {
    this.filterEnabled = visible;
  }

}


/*********************************************************************
 * $Log: KontoList.java,v $
 * Revision 1.17.2.2  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.17.2.1  2009/06/23 11:04:32  willuhn
 * @N Haupt- und Hilfsbuchungen in Steuerkonten anzeigen
 *
 * Revision 1.17  2007/04/23 23:41:26  willuhn
 * @B reset des Konten-Iterators
 *
 * Revision 1.16  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.14  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.13  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.12  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.11  2005/10/03 21:55:24  willuhn
 * @B bug 128, 129
 *
 * Revision 1.10  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.8  2005/09/01 21:18:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/01 21:08:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.4  2005/08/25 23:00:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/16 23:14:35  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/