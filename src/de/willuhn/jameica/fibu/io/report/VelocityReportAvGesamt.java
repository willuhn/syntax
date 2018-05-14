/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io.report;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;

/**
 * Report fuer Uebersicht des Anlagevermoegens.
 */
public class VelocityReportAvGesamt extends AbstractVelocityReport
{
  
  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractVelocityReport#getData(de.willuhn.jameica.fibu.io.report.ReportData)
   */
  protected VelocityReportData getData(ReportData data) throws Exception
  {
    Geschaeftsjahr jahr = data.getGeschaeftsjahr();
    
    Date end = jahr.getEnde();
    // Liste des Anlagevermoegens ermitteln
    List<Anlagevermoegen> list = new LinkedList<Anlagevermoegen>();
    DBIterator i = Settings.getDBService().createList(Anlagevermoegen.class);
    i.addFilter("mandant_id = " + Settings.getActiveGeschaeftsjahr().getMandant().getID());
    i.setOrder("order by anschaffungsdatum desc");
    while (i.hasNext())
    {
      Anlagevermoegen av = (Anlagevermoegen) i.next();
      if (av.getStatus() != Anlagevermoegen.STATUS_BESTAND)
        continue; // Nicht mehr im Bestand
      
      // Wurde nach dem aktuellen Jahr angeschafft -> ignorieren wir
      if (av.getAnschaffungsdatum().after(end))
        continue;
      list.add(av);
    }
    
    VelocityReportData export = new VelocityReportData();
    export.addObject("anlagevermoegen",list);
    export.setTemplate("anlagevermoegen.vm");
    return export;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.AbstractReport#createPreset()
   */
  public ReportData createPreset()
  {
    ReportData data = super.createPreset();
    data.setNeedDatum(false);
    data.setNeedKonto(false);
    data.setTarget(i18n.tr("syntax-{0}-av-gesamt.html",DATEFORMAT.format(new Date())));
    return data;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.report.Report#getName()
   */
  public String getName()
  {
    return i18n.tr("Anlagevermögen: Gesamtübersicht");
  }
}


/*********************************************************************
 * $Log: VelocityReportAvGesamt.java,v $
 * Revision 1.5  2010/12/20 11:09:09  willuhn
 * @B BUGZILLA 957
 *
 * Revision 1.4  2010-11-30 23:32:18  willuhn
 * @B BUGZILLA 953
 * @C Velocity kann inzwischen mit java.util.List-Objekten umgehen. Das Erzeugen der Arrays ist daher nicht mehr noetig
 *
 * Revision 1.3  2010-09-20 10:27:36  willuhn
 * @N Neuer Status fuer Anlagevermoegen - damit kann ein Anlagegut auch dann noch in der Auswertung erscheinen, wenn es zwar abgeschrieben ist aber sich noch im Bestand befindet. Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=69910#69910
 *
 * Revision 1.2  2010-09-20 09:19:06  willuhn
 * @B minor gui fixes
 *
 * Revision 1.1  2010-08-27 10:18:14  willuhn
 * @C Export umbenannt in Report
 *
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