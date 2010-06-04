/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/GeschaeftsjahrDelete.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/06/04 00:33:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Loeschen eines Geschaeftsjahres.
 */
public class GeschaeftsjahrDelete implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();


  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Geschaeftsjahr))
      return;
    
    try
    {
      if (((Geschaeftsjahr)context).isNewObject())
        return;
      
      YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
      d.setTitle(i18n.tr("Geschäftsjahr wirklich löschen?"));
      d.setText(i18n.tr("Wollen Sie dieses Geschäftsjahr wirklich löschen?\n" +
                        "Hierbei werden auch die Buchungen und Anfangsbestände gelöscht."));
      
      if (!((Boolean) d.open()).booleanValue())
      {
        Logger.info("operation cancelled");
        return;
      }

      Geschaeftsjahr jahr = (Geschaeftsjahr)context;
      Geschaeftsjahr vorjahr = jahr.getVorjahr();
      jahr.delete();
      if (vorjahr != null)
        Settings.setActiveGeschaeftsjahr(vorjahr);
      GUI.getStatusBar().setSuccessText(i18n.tr("Geschäftsjahr gelöscht"));
      // Seite aktualisieren
      GUI.startView(GUI.getCurrentView().getClass(),GUI.getCurrentView().getCurrentObject());
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("unable to delete gj",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Daten des Geschäftsjahres"));
    }
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrDelete.java,v $
 * Revision 1.7  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.6  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.5.2.1  2008/09/08 09:03:51  willuhn
 * @C aktiver Mandant/aktives Geschaeftsjahr kann nicht mehr geloescht werden
 *
 * Revision 1.5  2006/06/20 23:27:17  willuhn
 * @C Anzeige des aktuellen Geschaeftsjahres
 * @C Oeffnen/Schliessen eines Geschaeftsjahres
 *
 * Revision 1.4  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/