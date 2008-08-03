/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/UstVaExport.java,v $
 * $Revision: 1.1.2.2 $
 * $Date: 2008/08/03 23:10:44 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Hashtable;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Exporter fuer die Auswertung zur UST-Voranmeldung.
 */
public class UstVaExport extends AbstractExportAction
{
  private I18N i18n = null;
  private Geschaeftsjahr jahr = null;
  
  /**
   * ct.
   */
  public UstVaExport()
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    File file = null;
    try
    {
      file = storeTo(i18n.tr("fibu-ust-{0}.html",Fibu.FASTDATEFORMAT.format(new Date())));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info("operation cancelled");
      return;
    }
    
    try
    {
      if (context != null && (context instanceof Geschaeftsjahr))
        jahr = (Geschaeftsjahr) context;
      else
        jahr = de.willuhn.jameica.fibu.Settings.getActiveGeschaeftsjahr();
      
      DBIterator konten = jahr.getKontenrahmen().getKonten();

      double vst     = 0.0d;
      double ust     = 0.0d;

      Hashtable erloese = new Hashtable();

      while (konten.hasNext())
      {
        Konto kt = (Konto) konten.next();
        int type = kt.getKontoArt().getKontoArt();
        switch (type)
        {
          case Kontoart.KONTOART_ERLOES:
            String satz = "0.0";
            Steuer st = kt.getSteuer();
            if (st != null)
              satz = Double.toString(st.getSatz());
            Double betrag = (Double) erloese.get(satz);
            if (betrag == null)
              betrag = new Double(0.0d);

            betrag = new Double(betrag.doubleValue() + getUmsatz(kt));
            erloese.put(satz,betrag);
            break;
          case Kontoart.KONTOART_STEUER:
            Kontotyp ktyp = kt.getKontoTyp();
            if (ktyp == null)
              Logger.warn("SUSPEKT: Steuerkonto " + kt.getKontonummer() + " besitzt keinen Konto-Typ");
            else if (ktyp.getKontoTyp() == Kontotyp.KONTOTYP_EINNAHME)
              ust += getUmsatz(kt);
            else if (ktyp.getKontoTyp() == Kontotyp.KONTOTYP_AUSGABE)
              vst += getUmsatz(kt);
            else
              Logger.warn("SUSPEKT: Steuerkonto " + kt.getKontonummer() + " besitzt einen ungueltigen Konto-Typ");

            break;
        }
      }
        
      Export export = new Export();
      export.addObject("erloes",erloese);
      export.addObject("ust",new Double(ust));
      export.addObject("vst",new Double(vst));
      export.addObject("betrag",new Double(ust - vst));
      
      export.addObject("jahr",jahr);
      export.addObject("start",getStart());
      export.addObject("end",getEnd());
      export.setTarget(new FileOutputStream(file));
      export.setTitle(getName());
      export.setTemplate("ustva.vm");

      VelocityExporter.export(export);

      GUI.getStatusBar().setSuccessText(i18n.tr("Daten exportiert nach {0}",file.getAbsolutePath()));
      new Program().handleAction(file);
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("error while writing objects to " + file.getAbsolutePath(),e);
      throw new ApplicationException(i18n.tr("Fehler beim Exportieren der Daten in {0}",file.getAbsolutePath()),e);
    }
  }
  
  /**
   * Liefert den Umsatz des Kontos im genannten Zeitraum.
   * @param konto Konto.
   * @return Umsatz
   * @throws RemoteException
   */
  private double getUmsatz(Konto konto) throws RemoteException
  {
    double sum = 0.0d;
    DBIterator buchungen = null;
    if (konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_STEUER)
      buchungen = konto.getHilfsBuchungen(jahr,getStart(),getEnd());
    else
      buchungen = konto.getHauptBuchungen(jahr,getStart(),getEnd());
    while (buchungen.hasNext())
    {
      BaseBuchung b = (BaseBuchung) buchungen.next();
      sum += Math.abs(b.getBetrag());
    }
    return sum;
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getName()
   */
  public String getName()
  {
    return i18n.tr("Umsatzsteuer-Voranmeldung");
  }
}


/*********************************************************************
 * $Log: UstVaExport.java,v $
 * Revision 1.1.2.2  2008/08/03 23:10:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2008/08/03 23:02:47  willuhn
 * @N UST-Voranmeldung
 * @B Typos
 * @B Altes 16%-VST-Konto war nicht korrekt registriert. War aber nicht weiter schlimm, weil es ohnehin nirgends als Steuerkonto registriert war.
 *
 **********************************************************************/