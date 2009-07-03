/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/ext/Attic/JameicaCompatibility.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/07/03 11:12:09 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.ext;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.jameica.fibu.gui.part.BuchungList;
import de.willuhn.jameica.fibu.gui.part.KontoList;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Diese Klasse ruestet Funktionen nach, die erst in Jameica 1.8 verfuegbar sind.
 * Auf diese Weise ist SynTAX auch noch zu Jameica 1.7 kompatibel, da die zusaetzlichen
 * Funktionen nur dann aktiviert werden, wenn 1.8 genutzt wird.
 */
public class JameicaCompatibility implements Extension
{

  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    Manifest mf = Application.getManifest();
    
    // Wir koennen die Funktion getVersion() nicht direkt
    // aufrufen, weil sie beim Compilieren ggf. gegen die
    // falsche Version gelinkt ist.
    try
    {
      Object version = BeanUtil.get(mf,"version");
      if (version instanceof Double)
        return; // <= 1.7 - da gibt es "TablePart#setRememberState" noch nicht
      
      String id = extendable.getExtendableID();
      if (KontoList.class.getName().equals(id) ||
          BuchungList.class.getName().equals(id))
      {
        TablePart t = (TablePart) extendable;
        t.setRememberState(true);
      }
    }
    catch (Throwable t)
    {
      Logger.error("unable to activate feature",t);
    }
  }

}


/**********************************************************************
 * $Log: JameicaCompatibility.java,v $
 * Revision 1.3  2009/07/03 11:12:09  willuhn
 * @B 1.7 compatibility
 *
 * Revision 1.2  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 **********************************************************************/
