/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/messaging/ReportMessage.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 11:19:40 $
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
 * Wird versendet, wenn ein Report erstellt wurde.
 * Als Context-Data enthaelt die Message ein File-Objekt mit
 * dem erzeugten Report.
 */
public class ReportMessage extends QueryMessage
{
  /**
   * ct.
   * @param text Hinweis-Text.
   * @param file die Report-Datei. 
   */
  public ReportMessage(String text, File file)
  {
    super(text,file);
  }
}


/**********************************************************************
 * $Log: ReportMessage.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 * Revision 1.2  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 **********************************************************************/
