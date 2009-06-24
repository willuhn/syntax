/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportBuchungList.java,v $
 * $Revision: 1.1.2.2 $
 * $Date: 2009/06/24 10:35:55 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Exporter fuer das Buchungsjournal.
 */
public class VelocityExportBuchungList extends AbstractVelocityExport
{
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    DBIterator list = jahr.getHauptBuchungen();

    // Filter Start- und End-Datum
    Date start       = data.getStartDatum();
    Date end         = data.getEndDatum();
    if (start != null || end != null)
    {
      DBService service = Settings.getDBService();
      if (start != null) list.addFilter(service.getSQLTimestamp("datum") + " >= " + start.getTime());
      if (end != null)   list.addFilter(service.getSQLTimestamp("datum") + " <=" + end.getTime());
    }
 
    // Filter Konto
    Konto startKonto = data.getStartKonto();
    Konto endKonto   = data.getEndKonto();
    if (startKonto != null)
      list.addFilter("(sollkonto_id >= " + startKonto.getID() + " OR habenkonto_id >= " + startKonto.getID() + ")");
    if (endKonto != null)
      list.addFilter("(sollkonto_id <= " + endKonto.getID() + " OR habenkonto_id <= " + endKonto.getID() + ")");
    
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

    VelocityExportData export = new VelocityExportData();
    export.addObject("buchungen",b);
    export.addObject("anfangsbestaende",ab);
    export.setTemplate("buchungsjournal.vm");

    return export;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Buchungen: Journal");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedDatum(true);
    data.setNeedKonto(true);
    data.setTarget(i18n.tr("syntax-{0}-journal.html",DATEFORMAT.format(new Date())));
    return data;
  }

  
}


/*********************************************************************
 * $Log: VelocityExportBuchungList.java,v $
 * Revision 1.1.2.2  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/