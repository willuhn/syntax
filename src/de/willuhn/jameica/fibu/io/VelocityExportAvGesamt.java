/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportAvGesamt.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/23 16:53:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.util.ArrayList;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;

/**
 * Exporter fuer Uebersicht des Anlagevermoegens.
 */
public class VelocityExportAvGesamt extends AbstractVelocityExport
{
  
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    
    Date end = jahr.getEnde();
    // Liste des Anlagevermoegens ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = Settings.getDBService().createList(Anlagevermoegen.class);
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
    
    VelocityExportData export = new VelocityExportData();
    export.addObject("anlagevermoegen",av);
    export.addObject("jahr",jahr);
    export.setTemplate("anlagevermoegen.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-av-gesamt.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Anlagevermögen: Gesamtübersicht");
  }
}


/*********************************************************************
 * $Log: VelocityExportAvGesamt.java,v $
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/