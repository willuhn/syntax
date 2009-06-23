/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/VelocityExportEinnahmeUeberschussRechnung.java,v $
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

/**
 * Exporter fuer Uebersicht die Uberschuss-Rechnung.
 */
public class VelocityExportEinnahmeUeberschussRechnung extends AbstractVelocityExport
{
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractVelocityExport#getData(de.willuhn.jameica.fibu.io.ExportData)
   */
  protected VelocityExportData getData(ExportData data) throws Exception
  {
    VelocityExportData export = new VelocityExportData();
    export.addObject("jahr",data.getGeschaeftsjahr());
    export.setTemplate("ueberschussrechnung.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.Export#getName()
   */
  public String getName()
  {
    return i18n.tr("Überschuss-Rechnung");
  }

  
  /**
   * @see de.willuhn.jameica.fibu.io.AbstractExport#createPreset()
   */
  public ExportData createPreset()
  {
    ExportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-einnahme-ueberschuss.html",DATEFORMAT.format(new Date())));
    return data;
  }
}


/*********************************************************************
 * $Log: VelocityExportEinnahmeUeberschussRechnung.java,v $
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/