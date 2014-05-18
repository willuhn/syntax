/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/KontenrahmenList.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/03/21 11:17:26 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.part;

import java.rmi.RemoteException;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.KontenrahmenClone;
import de.willuhn.jameica.fibu.gui.action.KontoListe;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.parts.CheckedSingleContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Zeigt eine Liste aller Kontenrahmen an.
 */
public class KontenrahmenList extends TablePart
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  private MessageConsumer mc = new CreatedMessageConsumer();
  
  /**
   * ct.
   * @throws RemoteException
   */
  public KontenrahmenList() throws RemoteException
  {
    super(init(),new KontoListe());
    this.setRememberColWidths(true);
    this.setRememberOrder(true);
    this.setSummary(false);
    this.addColumn(i18n.tr("Bezeichnung"),"name");
    this.addColumn(i18n.tr("Zugeordneter Mandant"),"mandant_id");
    this.setFormatter(new TableFormatter() {
      public void format(TableItem item)
      {
        if (item == null)
          return;
        
        Object data = item.getData();
        if (data == null || !(data instanceof Kontenrahmen))
          return;
        
        try
        {
          Kontenrahmen kr = (Kontenrahmen) data;
          Color c = kr.getMandant() == null ? Color.ERROR : Color.FOREGROUND;
          item.setForeground(c.getSWTColor());
        }
        catch (Exception e)
        {
          Logger.error("unable to format table",e);
        }
      }
    });
    
    ContextMenu menu = new ContextMenu();
    menu.addItem(new CheckedSingleContextMenuItem(i18n.tr("Duplizieren..."),new KontenrahmenClone(),"gtk-dnd-multiple.png"));
    
    // TODO: Support zum Loeschen von Kontenrahmen fehlt noch
//    menu.addItem(ContextMenuItem.SEPARATOR);
//    menu.addItem(new CheckedSingleContextMenuItem(i18n.tr("Löschen..."),new DBObjectDelete(),"user-trash-full.png"));
    this.setContextMenu(menu);
  }
  
  /**
   * @see de.willuhn.jameica.gui.parts.TablePart#paint(org.eclipse.swt.widgets.Composite)
   */
  public synchronized void paint(Composite parent) throws RemoteException
  {
    Application.getMessagingFactory().getMessagingQueue(Settings.QUEUE_KONTENRAHMEN_CREATED).registerMessageConsumer(this.mc);
    parent.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e)
      {
        Application.getMessagingFactory().getMessagingQueue(Settings.QUEUE_KONTENRAHMEN_CREATED).unRegisterMessageConsumer(mc);
      }
    });
    
    super.paint(parent);
  }

  /**
   * Initialisiert die Liste der Kontenrahmen.
   * @return Liste der Kontenrahmen.
   * @throws RemoteException
   */
  private static GenericIterator init() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Kontenrahmen.class);
    list.setOrder("order by name");
    return list;
  }
  
  /**
   * Wird benachrichtigt, wenn ein neuer Kontenrahmen angelegt wurde.
   */
  private class CreatedMessageConsumer implements MessageConsumer
  {
    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
     */
    public Class[] getExpectedMessageTypes()
    {
      return new Class[]{QueryMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      if (message == null || !(message instanceof QueryMessage))
        return;
      
      final Object data = ((QueryMessage)message).getData();
      if (data == null || !(data instanceof Kontenrahmen))
        return;

      GUI.getDisplay().asyncExec(new Runnable() {
        public void run()
        {
          try
          {
            addItem(data);
            sort();
          }
          catch (Exception e)
          {
            Logger.error("error while adding new data",e);
          }
        }
      });
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
     */
    public boolean autoRegister()
    {
      return false;
    }
    
  }
}



/**********************************************************************
 * $Log: KontenrahmenList.java,v $
 * Revision 1.1  2011/03/21 11:17:26  willuhn
 * @N BUGZILLA 1004
 *
 **********************************************************************/