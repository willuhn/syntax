/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportBuchungList.java,v $
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

import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;

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
    export.addObject("jahr",jahr);
    export.setTemplate("buchungsjournal.vm");

    return export;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Buchungsjournal");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedDatum(false); // TODO: Datum sollte als Filter moeglich sein
    data.setNeedKonto(false); // TODO: Konto sollte als Filter moeglich sein
    data.setTarget(i18n.tr("syntax-{0}-journal.html",DATEFORMAT.format(new Date())));
    return data;
  }

  
}


/*********************************************************************
 * $Log: VelocityExportBuchungList.java,v $
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/