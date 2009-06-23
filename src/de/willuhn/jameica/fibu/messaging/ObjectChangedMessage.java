/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/messaging/ObjectChangedMessage.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/23 10:45:53 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.messaging;

import de.willuhn.jameica.messaging.Message;

/**
 * Kann versendet werden, wenn ein Objekt geaendert wurde.
 */
public class ObjectChangedMessage implements Message
{
  private Object object = null;

  /**
   * ct.
   * @param object das Objekt.
   */
  public ObjectChangedMessage(Object object)
  {
    this.object = object;
  }
  
  /**
   * Liefert das betreffende Objekt.
   * @return das Objekt.
   */
  public Object getObject()
  {
    return this.object;
  }

}


/**********************************************************************
 * $Log: ObjectChangedMessage.java,v $
 * Revision 1.1.2.1  2009/06/23 10:45:53  willuhn
 * @N Buchung nach Aenderung live aktualisieren
 *
 **********************************************************************/
