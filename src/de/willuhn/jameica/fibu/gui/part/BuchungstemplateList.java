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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.BuchungstemplateListMenu;
import de.willuhn.jameica.fibu.messaging.ObjectImportedMessage;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.DelayedListener;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Tabelle mit Buchungs-Vorlagen.
 */
public class BuchungstemplateList extends TablePart
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private MessageConsumer mc = new MyMessageConsumer();

  /**
   * ct.
   * @param action
   * @throws RemoteException
   */
  public BuchungstemplateList(Action action) throws RemoteException
  {
    this(init(),action);
  }

  /**
   * @see de.willuhn.jameica.gui.parts.TablePart#paint(org.eclipse.swt.widgets.Composite)
   */
  public synchronized void paint(Composite parent) throws RemoteException
  {
    super.paint(parent);
    Application.getMessagingFactory().registerMessageConsumer(this.mc);

    parent.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e)
      {
        Application.getMessagingFactory().unRegisterMessageConsumer(mc);
      }
    });
  }

  /**
   * ct.
   * @param list Liste der Buchungs-Vorlagen.
   * @param action
   */
  public BuchungstemplateList(GenericIterator list, Action action)
  {
    super(list, action);
    addColumn(i18n.tr("Bezeichnung"),"name");
    addColumn(i18n.tr("Buchungstext"),"buchungstext");
    addColumn(i18n.tr("Soll-Konto"),"sollkonto_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
          return k.getKontenrahmen().getName() + " - " + k.getKontonummer() + " [" + k.getName() + "]";
        }
        catch (RemoteException e)
        {
          Logger.error("unable to read konto",e);
          return "nicht ermittelbar";
        }
      }
    });
    addColumn(i18n.tr("Haben-Konto"),"habenkonto_id", new Formatter() {
      public String format(Object o)
      {
        if (o == null || !(o instanceof Konto))
          return null;
        try
        {
          Konto k = (Konto) o;
          return k.getKontenrahmen().getName() + " - " + k.getKontonummer() + " [" + k.getName() + "]";
        }
        catch (RemoteException e)
        {
          Logger.error("unable to read konto",e);
          return "nicht ermittelbar";
        }
      }
    });
    setContextMenu(new BuchungstemplateListMenu());
    setRememberColWidths(true);
    setRememberOrder(true);
    setMulti(true);
  }
  
  /**
   * Initialisiert die Liste der Buchungsvorlagen.
   * @return Liste der Buchungsvorlagen.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator list = Settings.getDBService().createList(Buchungstemplate.class);
    list.addFilter("(mandant_id is null or mandant_id = " + jahr.getMandant().getID() + ")");
    list.addFilter("(kontenrahmen_id is null or kontenrahmen_id = " + jahr.getKontenrahmen().getID() + ")");
    list.setOrder("order by name");
    return list;
  }

  /**
   * Listener, der das Neuladen der Buchungsvorlagen uebernimmt.
   */
  private class MyListener implements Listener
  {
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
      try
      {
        removeAll();
        GenericIterator list = init();
        while (list.hasNext())
          addItem(list.next());
        
        // Sortierung wiederherstellen
        sort();
      }
      catch (RemoteException re)
      {
        Logger.error("unable to reload data",re);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Aktualisieren der Buchungsvorlagen: {0}",re.getMessage()),StatusBarMessage.TYPE_ERROR));
      }
    }
    
  }
  
  /**
   * Mit dem Consumer werden wir ueber importierte Datensaetze informiert.
   */
  private class MyMessageConsumer implements MessageConsumer
  {
    private DelayedListener delay = new DelayedListener(new MyListener());
    
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


/*********************************************************************
 * $Log: BuchungstemplateList.java,v $
 * Revision 1.7  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 * Revision 1.6  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.4  2006/06/19 22:54:34  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2006/05/29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.2  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/