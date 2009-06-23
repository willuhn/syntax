/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Attic/ExportData.java,v $
 * $Revision: 1.1.2.1 $
 * $Date: 2009/06/23 16:53:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.util.Date;

import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;

/**
 * Haelt die Meta-Daten fuer den Export.
 * Anhand dieser Daten soll die Export-Implementierung in der Lage
 * sein, die Export-Datei zu erstellen.
 */
public class ExportData
{
  private Geschaeftsjahr geschaeftsjahr = null;
  private Date startDatum               = null;
  private Date endDatum                 = null;
  private Konto startKonto              = null;
  private Konto endKonto                = null;
  
  private boolean needGeschaeftsjahr    = true;
  private boolean needDatum             = true;
  private boolean needKonto             = true;
  
  private String target                 = null;
  
  /**
   * Liefert das Geschaeftsjahr.
   * @return das Geschaeftsjahr.
   */
  public Geschaeftsjahr getGeschaeftsjahr()
  {
    return this.geschaeftsjahr;
  }
  
  /**
   * Speichert das Geschaeftsjahr.
   * @param geschaeftsjahr
   */
  public void setGeschaeftsjahr(Geschaeftsjahr geschaeftsjahr)
  {
    this.geschaeftsjahr = geschaeftsjahr;
  }
  
  /**
   * Liefert das Start-Datum.
   * @return das Start-Datum.
   */
  public Date getStartDatum()
  {
    return this.startDatum;
  }
  
  /**
   * Speichert das Start-Datum.
   * @param startDatum das Start-Datum.
   */
  public void setStartDatum(Date startDatum)
  {
    this.startDatum = startDatum;
  }
  
  /**
   * Liefert das End-Datum.
   * @return das End-Datum.
   */
  public Date getEndDatum()
  {
    return this.endDatum;
  }
  
  /**
   * Speichert das End-Datum.
   * @param endDatum das End-Datum.
   */
  public void setEndDatum(Date endDatum)
  {
    this.endDatum = endDatum;
  }
  
  /**
   * Liefert das Start-Konto.
   * @return das Start-Konto.
   */
  public Konto getStartKonto()
  {
    return this.startKonto;
  }
  
  /**
   * Speichert das Start-Konto.
   * @param startKonto das Start-Konto.
   */
  public void setStartKonto(Konto startKonto)
  {
    this.startKonto = startKonto;
  }
  
  /**
   * Liefert das End-Konto.
   * @return das End-Konto.
   */
  public Konto getEndKonto()
  {
    return this.endKonto;
  }
  
  /**
   * Speichert das End-Konto.
   * @param endKonto das End-Konto.
   */
  public void setEndKonto(Konto endKonto)
  {
    this.endKonto = endKonto;
  }

  /**
   * Liefert true, wenn der User ein Geschaeftsjahr auswaehlen soll.
   * @return true, wenn der User ein Geschaeftsjahr auswaehlen soll.
   */
  public boolean isNeedGeschaeftsjahr()
  {
    return this.needGeschaeftsjahr;
  }

  /**
   * Legt fest, ob der User ein Geschaeftsjahr auswaehlen soll.
   * @param needGeschaeftsjahr true, wenn der User ein Geschaeftsjahr auswaehlen soll.
   */
  public void setNeedGeschaeftsjahr(boolean needGeschaeftsjahr)
  {
    this.needGeschaeftsjahr = needGeschaeftsjahr;
  }

  /**
   * Liefert true, wenn der User fuer den Export einen Zeitraum angeben soll.
   * @return true, wenn der User einen Zeitraum angeben soll.
   */
  public boolean isNeedDatum()
  {
    return this.needDatum;
  }

  /**
   * Legt fest, ob der User fuer den Export einen Zeitraum angeben soll.
   * @param needDatum true, wenn er einen Zeitraum angeben soll.
   */
  public void setNeedDatum(boolean needDatum)
  {
    this.needDatum = needDatum;
  }

  /**
   * Liefert true, wenn der User einen Konten-Bereich auswaehlen soll.
   * @return true, wenn der User einen Konten-Bereich auswaehlen soll.
   */
  public boolean isNeedKonto()
  {
    return this.needKonto;
  }

  /**
   * Legt fest, ob der User einen Konten-Bereich auswaehlen soll.
   * @param needKonto true, wenn der User einen Konten-Bereich auswaehlen soll.
   */
  public void setNeedKonto(boolean needKonto)
  {
    this.needKonto = needKonto;
  }

  /**
   * Liefert die Ziel-Datei.
   * @return Ziel-Datei.
   */
  public String getTarget()
  {
    return this.target;
  }

  /**
   * Speichert die Ziel-Datei.
   * @param target die Ziel-Datei.
   */
  public void setTarget(String target)
  {
    this.target = target;
  }
  

}


/**********************************************************************
 * $Log: ExportData.java,v $
 * Revision 1.1.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 **********************************************************************/
