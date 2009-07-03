/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportSaldenListe.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/07/03 10:52:18 $
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
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Exporter fuer die Summen- und Saldenliste.
 */
public class VelocityExportSaldenListe extends AbstractVelocityExport
{
  
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();

    // Liste der Konten ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = jahr.getKontenrahmen().getKonten();
    
    Konto start = data.getStartKonto();
    Konto end   = data.getEndKonto();
    
    if (start != null) i.addFilter("kontonummer >= ?",new Object[]{start.getKontonummer()});
    if (end != null) i.addFilter("kontonummer <= ?",new Object[]{end.getKontonummer()});

    while (i.hasNext())
    {
      Konto k = (Konto) i.next();
      if (k.getSaldo(jahr) == 0.0d && k.getUmsatz(jahr) == 0.0d && k.getAnfangsbestand(jahr) == null)
        continue; // hier gibts nichts anzuzeigen
      list.add(k);
    }
    
    Konto[] konten = (Konto[]) list.toArray(new Konto[list.size()]);
    VelocityExportData export = new VelocityExportData();
    export.addObject("konten",konten);
    export.setTemplate("saldenliste.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setTarget(i18n.tr("syntax-{0}-salden.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Konten: Summen- und Saldenliste");
  }
}


/*********************************************************************
 * $Log: VelocityExportSaldenListe.java,v $
 * Revision 1.2  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.1.2.2  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/