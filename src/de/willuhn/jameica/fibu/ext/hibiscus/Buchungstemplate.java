/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/ext/hibiscus/Buchungstemplate.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/06/07 12:57:33 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.ext.hibiscus;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.fibu.gui.controller.BuchungstemplateControl;
import de.willuhn.jameica.fibu.gui.views.BuchungstemplateNeu;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.gui.input.UmsatzTypInput;
import de.willuhn.jameica.hbci.rmi.UmsatzTyp;
import de.willuhn.logging.Logger;

/**
 * Erweitert die Buchungsvorlagen um die Zuordnung einer Umsatzkategorie aus
 * Hibiscus - jedoch nur, insofern Hibiscus installiert ist.
 */
public class Buchungstemplate implements Extension
{

  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    if (extendable == null || !(extendable instanceof BuchungstemplateNeu))
      return;

    try
    {
      BuchungstemplateNeu view = (BuchungstemplateNeu) extendable;
      
      Container container             = view.getContainer();
      BuchungstemplateControl control = view.getControl();
      
      if (container == null || control == null)
        return; // die View wird offensichtlich nicht mehr angezeigt

      final de.willuhn.jameica.fibu.rmi.Buchungstemplate template = control.getBuchung();

      // Zugeordnete Kategorie ermitteln
      UmsatzTyp typ = null;
      String id = template.getHibiscusUmsatzTypID();
      if (id != null)
      {
        try
        {
          typ = (UmsatzTyp) Settings.getDBService().createObject(UmsatzTyp.class,id);
        }
        catch (ObjectNotFoundException e)
        {
          // Die Kategorie wurde in Hibiscus zwischenzeitlich geloescht, ignorieren wir
        }
      }

      // Auswahlfeld hinzufuegen
      final UmsatzTypInput input = new UmsatzTypInput(typ,UmsatzTyp.TYP_EGAL);
      input.addListener(new Listener() {
        public void handleEvent(Event event)
        {
          try
          {
            UmsatzTyp t = (UmsatzTyp) input.getValue();
            template.setHibiscusUmsatzTypID(t != null ? t.getID() : null);
          }
          catch (Exception e)
          {
            Logger.error("unable to apply hibiscus category",e);
          }
        }
      });
      container.addInput(input);

      // Ein Abfangen des Events, wenn der User auf den "Speichern"-Button
      // klickt, ist nicht notwendig, da wir die Aenderung via Listener
      // am Input-Feld sofort uebernehmen.
    }
    catch (Exception e)
    {
      Logger.error("unable to extend buchungstemplate",e);
    }
    
  }

}



/**********************************************************************
 * $Log: Buchungstemplate.java,v $
 * Revision 1.2  2010/06/07 12:57:33  willuhn
 * @N Tolerieren, wenn eine Kategorie in Hibiscus geloescht wurde
 *
 * Revision 1.1  2010/06/03 14:26:16  willuhn
 * @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
 * @C Code-Cleanup
 *
 **********************************************************************/