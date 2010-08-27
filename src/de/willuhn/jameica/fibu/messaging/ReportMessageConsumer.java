/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/messaging/ReportMessageConsumer.java,v $
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

import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

/**
 * Empfaengt Messages ueber erstellte Reports und zeigt sie dem User an.
 */
public class ReportMessageConsumer implements MessageConsumer
{
  
  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  public boolean autoRegister()
  {
    return true;
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  public Class[] getExpectedMessageTypes()
  {
    return new Class[]{ReportMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  public void handleMessage(Message message) throws Exception
  {
    final ReportMessage m = (ReportMessage) message;

    Application.getMessagingFactory().sendMessage(new StatusBarMessage(m.getName(),StatusBarMessage.TYPE_SUCCESS));

    if (Application.inServerMode())
      return;
    
    GUI.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          new Program().handleAction(m.getData());
        }
        catch (ApplicationException ae)
        {
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
        }
      }
    });
  }

}


/**********************************************************************
 * $Log: ReportMessageConsumer.java,v $
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
