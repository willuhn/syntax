/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/messaging/Attic/ExportMessage.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/24 10:35:55 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.messaging;

import java.io.File;

import de.willuhn.jameica.messaging.QueryMessage;

/**
 * Wird versendet, wenn ein Export erstellt wurde.
 * Als Context-Data enthaelt die Message ein File-Objekt mit
 * dem erzeugten Export.
 */
public class ExportMessage extends QueryMessage
{
  /**
   * ct.
   * @param text Hinweis-Text.
   * @param file die exportierte Datei. 
   */
  public ExportMessage(String text, File file)
  {
    super(text,file);
  }
}


/**********************************************************************
 * $Log: ExportMessage.java,v $
 * Revision 1.1.2.1  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 **********************************************************************/
