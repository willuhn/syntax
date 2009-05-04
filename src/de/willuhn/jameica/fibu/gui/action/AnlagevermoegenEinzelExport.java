/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/AnlagevermoegenEinzelExport.java,v $
 * $Revision: 1.1.2.3 $
 * $Date: 2009/05/04 10:55:51 $
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
 * Exporter fuer Einzeluebersicht des Anlagevermoegens.
 */
public class AnlagevermoegenEinzelExport extends AbstractExportAction
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
    
    Date end = jahr.getEnde();

    // Liste des Anlagevermoegens ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = de.willuhn.jameica.fibu.Settings.getDBService().createList(Anlagevermoegen.class);
    while (i.hasNext())
    {
      Anlagevermoegen av = (Anlagevermoegen) i.next();
      if (av.getAnfangsbestand(jahr) <= 0.0)
        continue; // AV, welches schon komplett abgeschrieben ist, ignorieren wir

      // Wurde nach dem aktuellen Jahr angeschafft -> ignorieren wir
      if (av.getAnschaffungsdatum().after(end))
        continue;
      
      list.add(av);
    }
    
    Anlagevermoegen[] av = (Anlagevermoegen[]) list.toArray(new Anlagevermoegen[list.size()]);
    export.addObject("anlagevermoegen",av);
    export.addObject("jahr",jahr);
    export.setTemplate("anlagevermoegen-einzel.vm");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Einzelübersicht des Anlagevermögens");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-av-einzel.html",DATEFORMAT.format(new Date()));    
  }

  /**
   * Liefert den Namen des Templates.
   * @return Dateiname des Templates.
   */
  String getTemplate()
  {
    return "anlagevermoegen-einzel.vm";
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenEinzelExport.java,v $
 * Revision 1.1.2.3  2009/05/04 10:55:51  willuhn
 * @B AV ignorieren, wenn es nach dem GJ-Ende angeschafft wurde
 *
 * Revision 1.1.2.2  2009/05/04 09:48:59  willuhn
 * @B falsches Template
 *
 * Revision 1.1.2.1  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.1  2006/01/04 17:59:11  willuhn
 * @B bug 171
 *
 **********************************************************************/