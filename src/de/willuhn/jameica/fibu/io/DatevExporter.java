/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Exportiert Daten im Datev-Format.
 */
public class DatevExporter implements Exporter
{
  private final static Map<Double,String> buMap    = new HashMap<Double,String>();
  private final static I18N i18n                   = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static Settings settings           = new Settings(DatevExporter.class);
  private final static DateFormat DATEFORMAT       = new SimpleDateFormat("ddMM");
  
  static
  {
    buMap.put(null,"01");
    buMap.put( 0d,"01");
    buMap.put(16d,"07");
    buMap.put( 7d,"08");
    buMap.put(19d,"09");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.io.Exporter#doExport(java.lang.Object[], de.willuhn.jameica.fibu.io.IOFormat, java.io.OutputStream, de.willuhn.util.ProgressMonitor)
   */
  public void doExport(Object[] objects, IOFormat format,OutputStream os, final ProgressMonitor monitor) throws RemoteException, ApplicationException
  {
    Writer writer = null;

    try
    {
      double factor = 1;
      if (monitor != null)
      {
        factor = ((double)(100 - monitor.getPercentComplete())) / objects.length;
        monitor.setStatusText(i18n.tr("Exportiere Daten"));
      }

      writer = new BufferedWriter(new OutputStreamWriter(os,settings.getString("charset","ISO-8859-15")));
      
      String header = "Währungskennzeichen;" +
      		            "Soll/Haben-Kennzeichen;" +
      		            "Umsatz (ohne Soll/Haben-Kz);" +
      		            "BU-Schlüssel;" +
      		            "Gegenkonto (ohne BU-Schlüssel);" +
      		            "Belegfeld1;" +
      		            "Belegfeld2;" +
      		            "Datum;" +
      		            "Konto;" +
      		            "Kostfeld1;" +
      		            "Kostfeld2;" +
      		            "Kostmenge;" +
      		            "Skonto;" +
      		            "Buchungstext;" +
      		            "EU-Land und UStID;" +
      		            "EU-Steuersatz;" +
      		            "Basiswährungskennung;" +
      		            "Basiswährungsbetrag;" +
      		            "Kurs";
      writer.write(header);
      writer.write("\r\n");
        
      for (int i=0;i<objects.length;++i)
      {
        if (monitor != null)  monitor.setPercentComplete((int)((i) * factor));
        Object name = BeanUtil.toString(objects[i]);
        if (name != null && monitor != null)
          monitor.log(i18n.tr("Exportiere {0}",name.toString()));
        
        BaseBuchung b = (BaseBuchung)objects[i];
        writer.write(serialize(b));

        // Wenn es eine Hauptbuchung ist, dann auch noch die zugehoerigen Hilfebuchungen exportieren
        if (b instanceof Buchung)
        {
          // Jetzt noch die zugehoerigen Hilfs-Buchungen
          DBIterator list = ((Buchung)b).getHilfsBuchungen();
          while (list.hasNext())
          {
            BaseBuchung bb = (BaseBuchung) list.next();
            writer.write(serialize(bb));
          }
        }
      }
    }
    catch (IOException e)
    {
      Logger.error("unable to write csv file",e);
      throw new ApplicationException(i18n.tr("Fehler beim Export der Daten. " + e.getMessage()));
    }
    finally
    {
      if (monitor != null)
      {
        monitor.setStatusText(i18n.tr("Schliesse Export-Datei"));
      }
      try
      {
        if (writer != null)
          writer.close();
      }
      catch (Exception e) {/*useless*/}
    }
  }
  
  /**
   * Serialisiert eine Buchung.
   * @param b die zu serialisierende Buchung.
   * @return die serialisierte Zeile.
   * @throws RemoteException
   */
  private String serialize(BaseBuchung b) throws RemoteException
  {
    String curr = b.getGeschaeftsjahr().getMandant().getWaehrung();
    String sep  = settings.getString("separator",";");

    // Format-Definition gemaess dvrewe_standart_importormate2.pdf, Seite 3
    StringBuffer sb = new StringBuffer();
    sb.append(format(curr)); sb.append(sep);                                    // Waehrungskennung
    sb.append(format("S")); sb.append(sep);                                     // Soll/Haben-Kennzeichen
    sb.append(de.willuhn.jameica.fibu.Settings.DECIMALFORMAT.format(b.getBetrag()));sb.append(sep); // Umsatz (ohne Soll/Haben-Kz)
    sb.append(buMap.get(b.getSteuer())); sb.append(sep);                        // BU-Schluessel
    sb.append(b.getHabenKonto().getKontonummer()); sb.append(sep);              // Gegenkonto (ohne BU-Schlüssel)
    sb.append(b.getBelegnummer()); sb.append(sep);                              // Belegfeld 1
    sb.append(format("")); sb.append(sep);                                      // Belegfeld 2
    sb.append(DATEFORMAT.format(b.getDatum())); sb.append(sep);                 // Datum
    sb.append(b.getSollKonto().getKontonummer()); sb.append(sep);               // Konto
    sb.append(format("")); sb.append(sep);                                      // Kostfeld 1
    sb.append(format("")); sb.append(sep);                                      // Kostfeld 2
    sb.append(""); sb.append(sep);                                              // Kostmenge
    sb.append(""); sb.append(sep);                                              // Skonto
    sb.append(format(b.getText())); sb.append(sep);                             // Buchungstext
    sb.append(format("")); sb.append(sep);                                      // EU-Land und UStID
    sb.append(""); sb.append(sep);                                              // EU-Steuersatz
    sb.append(format("")); sb.append(sep);                                      // Basiswährungskennung
    sb.append(""); sb.append(sep);                                              // Basiswährungsbetrag
    sb.append(""); sb.append(sep);                                              // Kurs
    
    sb.append("\r\n"); // Ich gehe mal davon aus, dass DATEV Windows-Zeilenumbrueche verwendet
    return sb.toString();
  }
  
  /**
   * Formatiert die Spalte.
   * @param value der Wert der Spalte.
   * @return die formatierte Spalte.
   */
  private String format(Object value)
  {
    String quote = settings.getString("quote","\"");
    String s = value == null ? "" : value.toString();
    return quote + s + quote;
  }

  /**
   * @see de.willuhn.jameica.fibu.io.IO#getIOFormats(java.lang.Class)
   */
  public IOFormat[] getIOFormats(Class objectType)
  {
    if (objectType == null)
      return null;
    
    if (!(BaseBuchung.class.isAssignableFrom(objectType)))
      return null;

    return new IOFormat[]{new IOFormat() {
      public String getName()
      {
        return DatevExporter.this.getName();
      }
    
      /**
       * @see de.willuhn.jameica.fibu.io.IOFormat#getFileExtensions()
       */
      public String[] getFileExtensions()
      {
        return new String[]{"csv"};
      }
    }};
  }

  /**
   * @see de.willuhn.jameica.fibu.io.IO#getName()
   */
  public String getName()
  {
    return i18n.tr("CSV-Format (DATEV-Format: \"Ex/Import (Buchungssätze)\")");
  }

}


/*********************************************************************
 * $Log: DatevExporter.java,v $
 * Revision 1.4  2012/01/27 22:22:59  willuhn
 * @N Belegnummer im Feld "Belegfeld1" im DATEV-Export mit exportieren
 *
 * Revision 1.3  2010-10-14 10:48:33  willuhn
 * @N Steuer-Satz in der Spalte "BU-Schluessel"
 *
 * Revision 1.2  2010-10-13 21:51:05  willuhn
 * @N Testweise den Steuersatz der Buchung in Spalte "EU-Steuersatz" eingetragen.
 *
 * Revision 1.1  2010-10-04 09:00:07  willuhn
 * @N CSV-Export von Buchungen
 *
 **********************************************************************/