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

import de.willuhn.jameica.messaging.QueryMessage;

/**
 * Wird verschickt, wenn ein Datensatz geloescht wurde.
 */
public class ObjectDeletedMessage extends QueryMessage
{
  /**
   * ct.
   * @param data
   */
  public ObjectDeletedMessage(Object data)
  {
    super(data);
  }
}



/**********************************************************************
 * $Log: ObjectDeletedMessage.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/