/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/util/KontenrahmenUtil.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/11/05 01:08:09 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.util;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;


/**
 * Util-Klasse fuer den Kontenrahmen.
 */
public class KontenrahmenUtil
{

  /**
   * Erstellt einen neuen Kontenrahmen basierend auf dem uebergebenen.
   * @param service der Datenbank-Service.
   * @param template das zu verwendende Kontenrahmen-Template
   * @param mandant
   * @param monitor Monitor zur Ueberwachung der Erstellung.
   * @return der neue Kontenrahmen.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static Kontenrahmen create(DBService service, Kontenrahmen template, Mandant mandant, ProgressMonitor monitor) throws RemoteException, ApplicationException
  {
    Kontenrahmen kr   = null;
    I18N i18n         = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      // Kontenrahmen erstellen
      if (monitor != null) monitor.setStatusText(i18n.tr("Erstelle Kontenrahmen"));
      kr = (Kontenrahmen) service.createObject(Kontenrahmen.class,null);
      kr.transactionBegin();

      kr.overwrite(template);
      kr.setName(kr.getName() + " (" + mandant.getFirma() + ")");
      kr.setMandant(mandant);
      kr.store();
      
      // Wir cachen die angelegten Steuersaetze um Doppler zu vermeiden
      Hashtable steuerCache = new Hashtable();
      Hashtable kontoCache  = new Hashtable();
      
      // Konten anlegen
      if (monitor != null) monitor.setStatusText(i18n.tr("Erstelle Konten"));
      DBIterator konten = template.getKonten();
      konten.addFilter("mandant_id is null");
      
      if (monitor != null) monitor.setPercentComplete(0);
      double factor = 100d / konten.size();
      int count = 0;

      while (konten.hasNext())
      {
        if (monitor != null) monitor.setPercentComplete((int)((++count) * factor));

        Konto kt  = (Konto) konten.next();
        Konto k   = (Konto) service.createObject(Konto.class,null);
        
        k.overwrite(kt);
        k.setKontenrahmen(kr);
        k.setMandant(mandant);

        // ggf. vorhandener Steuersatz
        Steuer st = (Steuer) kt.getSteuer();
        if (st != null)
        {
          // checken, ob wir den Steuersatz schon angelegt haben
          Steuer s = (Steuer) steuerCache.get(st.getID());
          if (s == null)
          {
            // Haben wir noch nicht, also anlegen
            s = (Steuer) service.createObject(Steuer.class,null);
            s.overwrite(st);
            s.setMandant(mandant);
            s.store();
            steuerCache.put(st.getID(),s);
          }
          k.setSteuer(s);
        }
        k.store();
        kontoCache.put(kt.getID(),k);
        if (monitor != null) monitor.addPercentComplete(1);
      }
      
      
      // Jetzt muessen wir noch die bei den neu angelegten
      // Steuersaetzen hinterlegten Steuerkonten auf die
      // neuen Konten des Mandanten umbiegen
      Enumeration e = steuerCache.elements();
      while (e.hasMoreElements())
      {
        Steuer s = (Steuer) e.nextElement();
        Konto  k = (Konto) kontoCache.get(s.getSteuerKonto().getID());
        s.setSteuerKonto(k);
        s.store();
      }
      
      kr.transactionCommit();
      return kr;
    }
    catch (RemoteException re)
    {
      if (kr != null)
        kr.transactionRollback();
      throw re;
    }
  }
  
  /**
   * Verschiebt einen kompletten Mandanten auf einen anderen Kontenrahmen.
   * Funktioniert nur, wenn fuer alle Geschaeftsjahre des Mandanten der
   * gleiche Kontenrahmen verwendet wurde. Ist das nicht der Fall, kehrt
   * die Funktion kommentarlos zurueck.
   * @param service der Datenbank-Service.
   * @param mandant der Mandant.
   * @param target Zielkontenrahmen.
   * @param monitor Progress-Monitor.
   * @throws ApplicationException
   * @throws RemoteException
   */
  public static void move(DBService service, Mandant mandant, Kontenrahmen target, ProgressMonitor monitor) throws ApplicationException, RemoteException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    if (monitor != null) monitor.setStatusText(i18n.tr("Prüfe Kontenrahmen des Mandanten"));

    DBIterator jahre = mandant.getGeschaeftsjahre();
    Kontenrahmen source = null;
    while (jahre.hasNext())
    {
      Geschaeftsjahr gj = (Geschaeftsjahr) jahre.next();
      Kontenrahmen kr   = gj.getKontenrahmen(); 
      if (source != null && !kr.equals(source))
      {
        Logger.warn(i18n.tr("Mandant [id: {0}] kann nicht verschoben werden, da unterschiedliche Kontenrahmen genutzt wurden",mandant.getID()));
        return;
      }
      source = kr;
    }
    
    if (source == null)
      return; // Kunde hatte noch gar keine Geschaeftsjahre - also nichts zu tun

    if (monitor != null) monitor.setStatusText(i18n.tr("Kopiere benutzerdefinierte Konten"));
    source.transactionBegin();

    try
    {
      // Bevor wir vergleichen, ob alle noetigen Konten existieren
      // muessen erst die Benutzerkonten verschoben werden
      DBIterator kontenList = source.getKonten();
      kontenList.addFilter("mandant_id = " + mandant.getID());
      while (kontenList.hasNext())
      {
        Konto k = (Konto) kontenList.next();
        k.setKontenrahmen(target);
        k.store();
      }

      if (monitor != null) monitor.setStatusText(i18n.tr("Prüfe, ob alle benötigten Konten vorhanden sind"));
      
      // Wir checken vorher, ob der neue Kontenrahmen mindestens die Konten des
      // alten enthaelt.
      DBIterator konten = source.getKonten();
      while (konten.hasNext())
      {
        Konto k = (Konto) konten.next();
        String kn = k.getKontonummer();
        if (target.findByKontonummer(kn) == null)
          throw new ApplicationException(i18n.tr("Konto Nr. {0} existiert nicht in Ziel-Kontenrahmen",kn));
      }
      
      if (monitor != null) monitor.setStatusText(i18n.tr("Verschiebe Steuersätze"));
      DBIterator steuerlist = service.createList(Steuer.class);
      steuerlist.addFilter("mandant_id = " + mandant.getID());
      while (steuerlist.hasNext())
      {
        Steuer s = (Steuer) steuerlist.next();
        s.setSteuerKonto(target.findByKontonummer(s.getSteuerKonto().getKontonummer()));
        s.store();
      }

      if (monitor != null) monitor.setStatusText(i18n.tr("Verschiebe Buchungsvorlagen"));
      DBIterator btlist = service.createList(Buchungstemplate.class);
      btlist.addFilter("mandant_id = " + mandant.getID());
      while (btlist.hasNext())
      {
        Buchungstemplate bt = (Buchungstemplate) btlist.next();
        bt.setHabenKonto(target.findByKontonummer(bt.getHabenKonto().getKontonummer()));
        bt.setSollKonto(target.findByKontonummer(bt.getSollKonto().getKontonummer()));
        bt.store();
      }

      jahre.begin();
      while (jahre.hasNext())
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) jahre.next();
        if (monitor != null) monitor.setStatusText(i18n.tr("Verschiebe Buchungen aus Geschäftsjahr {0}",BeanUtil.toString(jahr)));
        DBIterator buchungen = jahr.getHauptBuchungen();
        while (buchungen.hasNext())
        {
          Buchung b = (Buchung) buchungen.next();
          b.setHabenKonto(target.findByKontonummer(b.getHabenKonto().getKontonummer()));
          b.setSollKonto(target.findByKontonummer(b.getSollKonto().getKontonummer()));
          // TODO: Schlaegt fehl, wenn das Geschaeftsjahr schon geschlossen ist
          b.store();
        }
    
        if (monitor != null) monitor.setStatusText(i18n.tr("Verschiebe Anfangsbestände"));
        DBIterator ablist = jahr.getAnfangsbestaende();
        while (ablist.hasNext())
        {
          Anfangsbestand ab = (Anfangsbestand) ablist.next();
          ab.setKonto(target.findByKontonummer(ab.getKonto().getKontonummer()));
          ab.store();
        }

        if (monitor != null) monitor.setStatusText(i18n.tr("Verschiebe Abschreibungsbuchungen"));
        GenericIterator abschreibungen = jahr.getAbschreibungen();
        while (abschreibungen.hasNext())
        {
          Abschreibung a = (Abschreibung) abschreibungen.next();
          AbschreibungsBuchung ab = a.getBuchung();
          ab.setHabenKonto(target.findByKontonummer(ab.getHabenKonto().getKontonummer()));
          ab.setSollKonto(target.findByKontonummer(ab.getSollKonto().getKontonummer()));
          ab.store();
        }

        if (monitor != null) monitor.setStatusText(i18n.tr("Weise dem Geschäftsjahr neuen Kontenrahmen zu"));
        jahr.setKontenrahmen(target);
        jahr.store();
      }
      
      if (monitor != null) monitor.setStatusText(i18n.tr("Verschiebe Anlagevermögen"));
      DBIterator avlist = mandant.getAnlagevermoegen();
      while (avlist.hasNext())
      {
        Anlagevermoegen av = (Anlagevermoegen) avlist.next();
        av.setAbschreibungskonto(target.findByKontonummer(av.getAbschreibungskonto().getKontonummer()));
        av.setKonto(target.findByKontonummer(av.getKonto().getKontonummer()));
        av.store();
      }

      source.transactionCommit();
    }
    catch (RemoteException re)
    {
      source.transactionRollback();
      throw re;
    }
  }
}

/**********************************************************************
 * $Log: KontenrahmenUtil.java,v $
 * Revision 1.2  2007/11/05 01:08:09  willuhn
 * @N Funktion zum Verschieben eines Kontenrahmens (in progress)
 *
 * Revision 1.1  2007/10/08 22:54:47  willuhn
 * @N Kopieren eines kompletten Kontenrahmen auf einen Mandanten
 *
 **********************************************************************/
