/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/AbstractExport.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/07/03 10:52:18 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;


/**
 * Abstrakte Basis-Implementierung eines Exports.
 */
public abstract class AbstractExport implements Export
{
  final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.fibu.io.Export#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData d = new ExportData();
    d.setNeedGeschaeftsjahr(true);
    d.setNeedKonto(true);
    d.setNeedDatum(true);
    return d;
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (!(o instanceof Export))
      return -1;
    
    // Alphabetisch nach Name sortieren
    String name = ((Export)o).getName();
    return this.getName().compareTo(name);
  }
}


/**********************************************************************
 * $Log: AbstractExport.java,v $
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/
