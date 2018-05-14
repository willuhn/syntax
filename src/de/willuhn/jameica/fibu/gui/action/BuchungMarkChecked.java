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

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.messaging.ObjectChangedMessage;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Markiert Buchungen als geprueft oder ungeprueft.
 */
public class BuchungMarkChecked implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  private boolean state = false;
  
  /**
   * ct.
   * @param state true, wenn die Markierung gesetzt werden soll, sonst false.
   */
  public BuchungMarkChecked(boolean state)
  {
    this.state = state;
  }
  

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null)
      return;
    
    if (!(context instanceof Buchung) && !(context instanceof Buchung[]))
      return;
    
    try
    {
      Buchung[] b = null;
      
      if (context instanceof Buchung)
        b = new Buchung[] {(Buchung) context};
      else
        b = (Buchung[]) context;

      boolean haveHibiscus = (Application.getPluginLoader().isInstalled("de.willuhn.jameica.hbci.HBCI"));
      boolean haveSync     = Settings.getSyncCheckmarks();
      
      for (int i=0;i<b.length;++i)
      {
        try
        {
          b[i].setGeprueft(this.state);
          b[i].store();
          Application.getMessagingFactory().sendMessage(new ObjectChangedMessage(b[i]));
          
          // Wenn Hibiscus installiert ist und die Buchung aus einem Hibiscus-Umsatz erzeugt wurde, synchronisieren wir
          // den Geprueft-Status zurueck nach Hibiscus
          if (haveHibiscus && haveSync)
          {
            String hid = b[i].getHibiscusUmsatzID();
            if (hid != null && hid.length() > 0)
              Application.getMessagingFactory().getMessagingQueue("syntax.buchung.markchecked").sendMessage(new QueryMessage(Boolean.toString(this.state),hid));
          }
          
        }
        catch (Exception e)
        {
          Logger.error("unable to change state for buchung",e);
        }
      }

      if (b.length > 1)
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("{0} Buchungen bearbeitet.", ""+b.length),StatusBarMessage.TYPE_SUCCESS));
      else
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Buchung Nr. {0} bearbeitet.", ""+b[0].getBelegnummer()),StatusBarMessage.TYPE_SUCCESS));
    }
    catch (Exception e)
    {
      Logger.error("unable to change state for buchungen",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Buchung(en): {0}",e.getMessage()));
    }
  }
}
