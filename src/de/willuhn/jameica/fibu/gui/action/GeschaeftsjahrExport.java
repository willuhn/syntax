/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/GeschaeftsjahrExport.java,v $
 * $Revision: 1.7.2.1 $
 * $Date: 2008/08/04 22:33:16 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Exporter fuer Uebersicht die Uberschuss-Rechnung.
 */
public class GeschaeftsjahrExport extends AbstractExportAction
{
  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#fill(de.willuhn.jameica.fibu.io.Export, java.lang.Object)
   */
  protected void fill(Export export, Object context) throws ApplicationException, RemoteException, OperationCanceledException
  {
    if (context == null || !(context instanceof Geschaeftsjahr))
      throw new ApplicationException("Kein Geschäftsjahr angegeben");
    
    export.addObject("jahr",context);
    export.setTemplate("ueberschussrechnung.vm");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Überschuss-Rechnung");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-einnahme-ueberschuss.html",DATEFORMAT.format(new Date()));
  }
}


/*********************************************************************
 * $Log: GeschaeftsjahrExport.java,v $
 * Revision 1.7.2.1  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.7  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.6  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 **********************************************************************/