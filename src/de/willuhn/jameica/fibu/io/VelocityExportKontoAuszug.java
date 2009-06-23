/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportKontoAuszug.java,v $
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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;

/**
 * Exporter fuer Konten.
 */
public class VelocityExportKontoAuszug extends AbstractVelocityExport
{
  private static Map filenameMap = new HashMap();
  
  static
  {
    filenameMap.put("Anlagevermögen"    ,"anlagevermoegen");
    filenameMap.put("Aufwände"          ,"aufwaende");
    filenameMap.put("Erlöse"            ,"erloese");
    filenameMap.put("Geldkonto"         ,"geldkonto");
    filenameMap.put("Privatkonto"       ,"privatkonto");
    filenameMap.put("Steuer-Sammelkonto","steuer-sammelkonto");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();

    VelocityExportData export = new VelocityExportData();
    export.addObject("jahr",jahr);
    export.addObject("filenames",filenameMap);
    export.setTemplate("kontoauszug.vm");

    //////////////////////////////////////////////////////////////////////////
    // Konten
    DBIterator konten   = jahr.getKontenrahmen().getKonten();
    Konto start = data.getStartKonto();
    Konto end   = data.getEndKonto();
    if (start != null) konten.addFilter("kontonummer >= ?", new String[]{start.getKontonummer()});
    if (end != null) konten.addFilter("kontonummer <= ?", new String[]{end.getKontonummer()});

    ArrayList l = new ArrayList();
    while (konten.hasNext())
    {
      Konto k1 = (Konto) konten.next();
      Anfangsbestand ab = k1.getAnfangsbestand(jahr);
      if (k1.getUmsatz(jahr) == 0.0d && (ab == null || ab.getBetrag() == 0.0d))
        continue;
      l.add(k1);
    }
    Konto[] k = (Konto[]) l.toArray(new Konto[l.size()]);
    export.addObject("konten",k);
    //
    //////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    // Buchungen
    Date startDate = data.getStartDatum();
    Date endDate   = data.getEndDatum();
      
    for (int i=0;i<k.length;++i)
    {
      Vector buchungen = new Vector();

      DBIterator list = k[i].getHauptBuchungen(jahr,startDate,endDate);
      while (list.hasNext())
        buchungen.add(list.next());

      Kontoart ka = k[i].getKontoArt();
      if (ka != null && ka.getKontoArt() == Kontoart.KONTOART_STEUER)
      {
        // Ein Steuerkonto enthaelt normalerweise nur automatisch
        // erzeugte Hilfsbuchungen. Da der User aber auch echte
        // Hauptbuchungen darauf erzeugen kann, muss die Liste
        // hier noch um die Hauptbuchungen ergaenzt werden.
        list = k[i].getHilfsBuchungen(jahr,startDate,endDate);
        while (list.hasNext())
          buchungen.add(list.next());
      }
      export.addObject("buchungen." + k[i].getKontonummer(),buchungen);
    }
    //////////////////////////////////////////////////////////////////////////

    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setTarget(i18n.tr("syntax-{0}-kontoauszug.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Konto-Auszug");
  }
}


/*********************************************************************
 * $Log: VelocityExportKontoAuszug.java,v $
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/