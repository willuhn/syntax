/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.messaging;

import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;

/**
 * Wird benachrichtigt, wenn eine Buchung aus Hibiscus als geprueft/ungeprueft markiert wurde.
 * Insofern wir eine Buchung haben, die aus diesem Umsatz erzeugt wurde, uebernehmen wir den
 * Status dann auch gleich in SynTAX.
 */
public class HibiscusUmsatzMarkCheckedMessageConsumer implements MessageConsumer
{
  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  @Override
  public Class[] getExpectedMessageTypes()
  {
    return new Class[] {QueryMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  @Override
  public void handleMessage(Message message) throws Exception
  {
    if (!Settings.getSyncCheckmarks())
      return;
    
    final QueryMessage m = (QueryMessage) message;
    final String name    = m.getName();
    final Object data    = m.getData();
    
    if (name == null || data == null)
      return;
    
    final boolean state = Boolean.valueOf(name);
    
    // Wir muessen hier nichtmal auf Umsatz casten - uns genuegt die ID des Datensatzes
    if (!(data instanceof GenericObject))
      return;
    
    final GenericObject o = (GenericObject) data;
    final String id = o.getID();
    if (id == null || id.length() == 0)
      return;
    
    DBIterator<Buchung> list = Settings.getDBService().createList(Buchung.class);
    list.addFilter("hb_umsatz_id = ?",id);
    if (!list.hasNext())
      return;
    
    Buchung b = list.next();
    b.setGeprueft(state);
    b.store();
    Application.getMessagingFactory().sendMessage(new ObjectChangedMessage(b));
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  @Override
  public boolean autoRegister()
  {
    // Per plugin.xml registriert.
    return false;
  }

}


