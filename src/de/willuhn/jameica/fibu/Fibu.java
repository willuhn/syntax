/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu;


import de.willuhn.jameica.gui.MenuItem;
import de.willuhn.jameica.gui.NavigationItem;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.extension.ExtensionRegistry;
import de.willuhn.jameica.gui.internal.views.Start;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Basisklasse des Fibu-Plugins fuer das Jameica-Framework.
 * @author willuhn
 */
public class Fibu extends AbstractPlugin
{

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  public void init() throws ApplicationException
  {
    // Wir registrieren noch einen Hook, der nach dem Starten der GUI
    // aktiv wird, um Menu und Navi freizuschalten, wenn DB,Mandant und GJ
    // eingerichtet sind.
    if (!Application.inServerMode())
    {
      ExtensionRegistry.register(new Extension() {
        /**
         * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
         */
        public void extend(Extendable extendable)
        {
          if (!Settings.isFirstStart())
          {
            try
            {
              // Wir koennen starten. Navigation und Menu freigeben.
              Manifest manifest = Application.getPluginLoader().getManifest(Fibu.class);
              NavigationItem navi = manifest.getNavigation();
              if (navi != null)
                navi.setEnabled(true,true);
              
              MenuItem menu = manifest.getMenu();
              if (menu != null)
                menu.setEnabled(true,true);

              // Ansonsten aktualisieren wir die Anzeige des Geschaeftsjahres
              Settings.setStatus();
            }
            catch (Exception e)
            {
              Logger.error("unable to activate navigation/menu",e);
            }
            return;
          }
        }
      }, Start.class.getName());
    }
  }
}

/*********************************************************************
 * $Log: Fibu.java,v $
 * Revision 1.47  2010/06/01 16:35:48  willuhn
 * @C Konstanten verschoben
 *
 * Revision 1.46  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.44.2.2  2008/12/30 15:21:33  willuhn
 * @N Umstellung auf neue Versionierung
 *
 * Revision 1.44.2.1  2008/06/25 09:17:18  willuhn
 * @N First code in 1.3 branch
 *
 * Revision 1.44  2008/02/07 23:08:39  willuhn
 * @R KontenrahmenUtil#move() entfernt - hoffnungsloses Unterfangen
 **********************************************************************/