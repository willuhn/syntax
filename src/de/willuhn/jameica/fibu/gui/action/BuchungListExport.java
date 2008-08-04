/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/BuchungListExport.java,v $
 * $Revision: 1.9.2.1 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Exporter fuer das Buchungsjournal.
 */
public class BuchungListExport extends AbstractExportAction
{
  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#fill(de.willuhn.jameica.fibu.io.Export, java.lang.Object)
   */
  protected void fill(Export export, Object context) throws ApplicationException, RemoteException, OperationCanceledException
  {
    Geschaeftsjahr jahr = null;
    if (context != null && context instanceof Geschaeftsjahr)
      jahr = (Geschaeftsjahr) context;
    else
      jahr = de.willuhn.jameica.fibu.Settings.getActiveGeschaeftsjahr();

    DBIterator list = jahr.getHauptBuchungen();
    list.setOrder("order by datum");
    Buchung[] b = new Buchung[list.size()];
    int count = 0;
    while (list.hasNext())
    {
      b[count++] = (Buchung) list.next();
    }
    
    list = jahr.getAnfangsbestaende();
    Anfangsbestand[] ab = new Anfangsbestand[list.size()];
    count = 0;
    while (list.hasNext())
    {
      ab[count++] = (Anfangsbestand) list.next();
    }

    export.addObject("buchungen",b);
    export.addObject("anfangsbestaende",ab);
    export.addObject("jahr",jahr);
    export.setTemplate("buchungsjournal.vm");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Buchungsjournal");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-journal.html",DATEFORMAT.format(new Date()));
  }
}


/*********************************************************************
 * $Log: BuchungListExport.java,v $
 * Revision 1.9.2.1  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.9  2006/05/30 23:33:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2005/10/17 22:59:38  willuhn
 * @B bug 135
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
 * Revision 1.3  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 * Revision 1.2  2005/08/24 23:02:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/