/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/messaging/ObjectChangedMessage.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/07/03 10:52:19 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.messaging;

import de.willuhn.jameica.messaging.QueryMessage;

/**
 * Kann versendet werden, wenn ein Objekt geaendert wurde.
 */
public class ObjectChangedMessage extends QueryMessage
{
  /**
   * ct.
   * @param data das geaenderte Objekt.
   */
  public ObjectChangedMessage(Object data)
  {
    super(data);
  }
}


/**********************************************************************
 * $Log: ObjectChangedMessage.java,v $
 * Revision 1.2  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.2  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.1.2.1  2009/06/23 10:45:53  willuhn
 * @N Buchung nach Aenderung live aktualisieren
 *
 **********************************************************************/
