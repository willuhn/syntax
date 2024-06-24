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
import de.willuhn.jameica.fibu.gui.menus.KontozuordnungListMenu;
import de.willuhn.jameica.fibu.messaging.ObjectImportedMessage;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Kontozuordnung;
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

public class KontoZuordnungList extends TablePart {
	private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
	  
	  private MessageConsumer mc = new MyMessageConsumer();

	  /**
	   * ct.
	   * @param mandant der Mandant.
	   * @param list Liste der Knto-Zuordnungen.
	   * @param action
	   */
	  public KontoZuordnungList(Mandant mandant, GenericIterator list, Action action)
	  {
	    super(list, action);
	    addColumn(i18n.tr("Bezeichnung"),"name");
	    addColumn(i18n.tr("Hibscus Konto"),"hb_konto_id",new Formatter() {
	      public String format(Object o)
	      {
	        if (o == null)
	          return null;
	        try
	        {
	          de.willuhn.jameica.hbci.rmi.Konto k =  (de.willuhn.jameica.hbci.rmi.Konto) de.willuhn.jameica.hbci.Settings.getDBService().createObject(de.willuhn.jameica.hbci.rmi.Konto.class,o.toString());
	        	
	          //de.willuhn.jameica.hbci.rmi.Konto k = (de.willuhn.jameica.hbci.rmi.Konto) o;
	          return k.getBezeichnung() + " [" + k.getKontonummer() + "]";
	        }
	        catch (RemoteException e)
	        {
	          Logger.error("unable to read hb-konto",e);
	          return "nicht ermittelbar";
	        }
	      }});
	    addColumn(i18n.tr("Konto"),"konto_id", new Formatter() {
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
	    setContextMenu(new KontozuordnungListMenu(mandant));
	    setRememberColWidths(true);
	    setRememberOrder(true);
	    setMulti(true);
	  }
	  
	  /**
	   * Initialisiert die Liste der Kontozuordnungen.
	   * @return Liste der Kontozuordnungen.
	   * @throws RemoteException
	   */
	  private static GenericIterator init() throws RemoteException
	  {
	    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
	    DBIterator list = Settings.getDBService().createList(Kontozuordnung.class);
	    list.addFilter("(mandant_id is null or mandant_id = " + jahr.getMandant().getID() + ")");
	    list.setOrder("order by name");
	    return list;
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
	   * Listener, der das Neuladen der Kontozuordnungen uebernimmt.
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
