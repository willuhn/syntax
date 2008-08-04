/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/SaldenExport.java,v $
 * $Revision: 1.8.2.1 $
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
import java.util.ArrayList;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Exporter fuer die Summen- und Saldenliste.
 */
public class SaldenExport extends AbstractExportAction
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

    // Liste der Konten ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = jahr.getKontenrahmen().getKonten();
    while (i.hasNext())
    {
      Konto k = (Konto) i.next();
      if (k.getSaldo(jahr) == 0.0d && k.getUmsatz(jahr) == 0.0d && k.getAnfangsbestand(jahr) == null)
        continue; // hier gibts nichts anzuzeigen
      list.add(k);
    }
    
    Konto[] konten = (Konto[]) list.toArray(new Konto[list.size()]);
    export.addObject("konten",konten);
    export.addObject("jahr",jahr);
    export.setTemplate("saldenliste.vm");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Summen- und Saldenliste");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-salden.html",DATEFORMAT.format(new Date()));
  }
}


/*********************************************************************
 * $Log: SaldenExport.java,v $
 * Revision 1.8.2.1  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.8  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.7  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.3  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/30 23:15:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 **********************************************************************/