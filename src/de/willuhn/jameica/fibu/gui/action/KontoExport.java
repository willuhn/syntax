/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/KontoExport.java,v $
 * $Revision: 1.15.2.2 $
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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Exporter fuer Konten.
 */
public class KontoExport extends AbstractExportAction
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
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#fill(de.willuhn.jameica.fibu.io.Export, java.lang.Object)
   */
  protected void fill(Export export, Object context) throws ApplicationException, RemoteException, OperationCanceledException
  {
    if (context == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie mindestens ein Konto/Geschäftsjahr aus"));

    Geschaeftsjahr jahr = de.willuhn.jameica.fibu.Settings.getActiveGeschaeftsjahr();

    Konto[] k = null;
    if (context instanceof Konto)
    {
      k = new Konto[1];
      k[0] = (Konto) context;
    }
    else if (context instanceof Konto[])
    {
      k = (Konto[]) context;
    }
    else if (context instanceof Geschaeftsjahr)
    {
      jahr = (Geschaeftsjahr) context;
      DBIterator konten = jahr.getKontenrahmen().getKonten();

      Konto start = getStartKonto();
      Konto end = getEndKonto();
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
      k = (Konto[]) l.toArray(new Konto[l.size()]);
    }
        
    export.addObject("konten",k);
    export.addObject("jahr",jahr);
    export.addObject("filenames",filenameMap);

    Date start = getStart();
    Date end = getEnd();
      
    for (int i=0;i<k.length;++i)
    {
      Vector buchungen = new Vector();
      DBIterator list = null;
      Kontoart ka = k[i].getKontoArt();
      if (ka != null && ka.getKontoArt() == Kontoart.KONTOART_STEUER)
      {
        // TODO: Ein Steuerkonto enthaelt normalerweise nur automatisch
        // erzeugte Hilfsbuchungen. Da der User aber auch echte
        // Hauptbuchungen darauf erzeugen kann, muss die Liste
        // hier noch um die Hauptbuchungen ergaenzt werden.
        list = k[i].getHilfsBuchungen(jahr,start,end);
      }
      else
      {
        list = k[i].getHauptBuchungen(jahr,start,end);
      }
      
      while (list.hasNext())
      {
        buchungen.add(list.next());
      }
      export.addObject("buchungen." + k[i].getKontonummer(),buchungen);
    }
    export.setTemplate("kontoauszug.vm");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Konto-Auszug");
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractExportAction#getFilename()
   */
  protected String getFilename()
  {
    return i18n.tr("syntax-{0}-kontoauszug.html",DATEFORMAT.format(new Date()));
  }
}


/*********************************************************************
 * $Log: KontoExport.java,v $
 * Revision 1.15.2.2  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.15.2.1  2008/07/09 10:15:18  willuhn
 * @B Umlaut-Problem in Dateinamen gefixt
 *
 * Revision 1.15  2007/01/04 12:58:50  willuhn
 * @B wrong type for kontonummer
 *
 * Revision 1.14  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.13  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.12  2006/01/06 00:30:01  willuhn
 * @C report fixes
 *
 * Revision 1.11  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.10  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.9  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2005/09/26 23:57:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 * Revision 1.2  2005/08/24 23:02:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/16 23:14:36  willuhn
 * @N velocity export
 * @N context menus
 * @B bugfixes
 *
 **********************************************************************/