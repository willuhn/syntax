/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.util.Date;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.BuchungsEngine;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Schliessen des Geschaeftsjahres.
 */
public class GeschaeftsjahrClose implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Geschaeftsjahr))
      return;
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    Geschaeftsjahr jahr = (Geschaeftsjahr) context;

    try
    {
      boolean b = Application.getController().getApplicationCallback().askUser(
          i18n.tr("Sind Sie sicher, dass Sie das Geschäftsjahr \"{0}\" abschließen möchten?\n\n" +
                  "Hierbei werden automatisch die Abschreibungen gebucht, ein passendes Folgejahr erstellt\n" +
                  "und die Salden der Bestandskonten als Anfangsbestände in das neue Geschäftsjahr übernommen.",jahr.getAttribute(jahr.getPrimaryAttribute()).toString()));
      if (!b)
        return;
    }
    catch (OperationCanceledException oce)
    {
      return;
    }
    catch (Exception e)
    {
      Logger.error("error while checking gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Geschäftsjahres"));
    }
    
    try
    {
      if (new Date().before(jahr.getEnde()))
      {
        boolean b = Application.getController().getApplicationCallback().askUser(
            i18n.tr("Warnung: Sie schliessen das Geschäftsjahr, noch bevor dessen Ende erreicht ist.\n" +
                    "Sie können anschliessend keine Buchungen mehr darauf erfassen. Sind Sie sicher?"));
        if (!b)
          return;
      }
    }
    catch (OperationCanceledException oce)
    {
      return;
    }
    catch (Exception e)
    {
      Logger.error("error while checking gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Geschäftsjahres"));
    }
    
    try
    {
      BuchungsEngine engine = (BuchungsEngine) Application.getServiceFactory().lookup(Fibu.class,"engine");
      engine.close(jahr);
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Throwable t)
    {
      Logger.error("error while closing gj",t);
      throw new ApplicationException(i18n.tr("Fehler beim Schliessen des Geschäftsjahres"),t);
    }
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrClose.java,v $
 * Revision 1.9  2011/03/25 10:14:10  willuhn
 * @N Loeschen von Mandanten und Beruecksichtigen der zugeordneten Konten und Kontenrahmen
 * @C BUGZILLA 958
 *
 * Revision 1.8  2011-01-13 16:27:45  willuhn
 * @B typo
 *
 * Revision 1.7  2010-06-04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.6  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.5  2006/01/09 01:40:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/01 16:34:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.1  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 **********************************************************************/