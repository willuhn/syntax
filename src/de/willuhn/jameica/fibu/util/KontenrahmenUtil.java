/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/util/KontenrahmenUtil.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/10/08 22:54:47 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
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
   * @param template das zu verwendende Kontenrahmen-Template
   * @param mandant
   * @param monitor Monitor zur Ueberwachung der Erstellung.
   * @return der neue Kontenrahmen.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public static Kontenrahmen create(Kontenrahmen template, Mandant mandant, ProgressMonitor monitor) throws RemoteException, ApplicationException
  {
    Kontenrahmen kr   = null;
    DBService service = Settings.getDBService();
    I18N i18n         = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      // Kontenrahmen erstellen
      if (monitor != null) monitor.setStatusText(i18n.tr("Erstelle Kontenrahmen"));
      kr = (Kontenrahmen) service.createObject(Kontenrahmen.class,null);
      kr.transactionBegin();

      kr.overwrite(template);
      kr.setMandant(mandant);
      kr.store();
      if (monitor != null) monitor.addPercentComplete(1);
      
      // Wir cachen die angelegten Steuersaetze um Doppler zu vermeiden
      Hashtable steuerCache = new Hashtable();
      Hashtable kontoCache  = new Hashtable();
      
      // Konten anlegen
      if (monitor != null) monitor.setStatusText(i18n.tr("Erstelle Konten"));
      DBIterator konten = template.getKonten();
      while (konten.hasNext())
      {
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
}


/**********************************************************************
 * $Log: KontenrahmenUtil.java,v $
 * Revision 1.1  2007/10/08 22:54:47  willuhn
 * @N Kopieren eines kompletten Kontenrahmen auf einen Mandanten
 *
 **********************************************************************/
