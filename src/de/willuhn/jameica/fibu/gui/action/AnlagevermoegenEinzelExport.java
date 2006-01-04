/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/AnlagevermoegenEinzelExport.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/01/04 17:59:11 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.util.Date;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Exporter fuer Einzeluebersicht des Anlagevermoegens.
 */
public class AnlagevermoegenEinzelExport extends AnlagevermoegenExport
{
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public AnlagevermoegenEinzelExport()
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Einzelübersicht des Anlagevermögens");
  }

  /**
   * Liefert den Namen des Templates.
   * @return Dateiname des Templates.
   */
  String getTemplate()
  {
    return "anlagevermoegen-einzel.vm";
  }
  
  /**
   * Liefert den vorzuschlagenden Dateinamen fuer den Report.
   * @return Dateiname.
   */
  String getOutputFile()
  {
    return i18n.tr("fibu-anlagevermoegen-einzel-{0}.html",Fibu.FASTDATEFORMAT.format(new Date()));    
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenEinzelExport.java,v $
 * Revision 1.1  2006/01/04 17:59:11  willuhn
 * @B bug 171
 *
 **********************************************************************/