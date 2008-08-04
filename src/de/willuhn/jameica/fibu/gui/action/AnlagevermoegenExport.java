/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/AnlagevermoegenExport.java,v $
 * $Revision: 1.10.2.1 $
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
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Exporter fuer Uebersicht des Anlagevermoegens.
 */
public class AnlagevermoegenExport extends AbstractExportAction
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
    
    // Liste des Anlagevermoegens ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = de.willuhn.jameica.fibu.Settings.getDBService().createList(Anlagevermoegen.class);
    while (i.hasNext())
    {
      Anlagevermoegen av = (Anlagevermoegen) i.next();
      if (av.getAnfangsbestand(jahr) <= 0.0)
        continue; // AV, welches schon komplett abgeschrieben ist, ignorieren wir
        list.add(av);
    }
    
    Anlagevermoegen[] av = (Anlagevermoegen[]) list.toArray(new Anlagevermoegen[list.size()]);
    export.addObject("anlagevermoegen",av);
    export.addObject("jahr",jahr);
    export.setTemplate("anlagevermoegen.vm");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Gesamtübersicht des Anlagevermögens");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-av.html",DATEFORMAT.format(new Date()));    
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenExport.java,v $
 * Revision 1.10.2.1  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.10  2007/03/06 15:22:36  willuhn
 * @C Anlagevermoegen in Auswertungen ignorieren, wenn Anfangsbestand bereits 0
 * @B Formatierungsfehler bei Betraegen ("-0,00")
 * @C Afa-Buchungen werden nun auch als GWG gebucht, wenn Betrag zwar groesser als GWG-Grenze aber Afa-Konto=GWG-Afa-Konto (laut Einstellungen)
 *
 * Revision 1.9  2006/01/06 00:30:01  willuhn
 * @C report fixes
 *
 * Revision 1.8  2006/01/04 17:59:27  willuhn
 * @B bug 171
 *
 * Revision 1.7  2006/01/04 17:59:11  willuhn
 * @B bug 171
 *
 * Revision 1.6  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.5  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/29 22:44:05  willuhn
 * @N added templates
 *
 **********************************************************************/