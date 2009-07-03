/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/util/KontenrahmenUtil.java,v $
 * $Revision: 1.5 $
 * $Date: 2009/07/03 10:52:19 $
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
}

/**********************************************************************
 * $Log: KontenrahmenUtil.java,v $
 * Revision 1.5  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.3  2008/02/07 23:08:39  willuhn
 * @R KontenrahmenUtil#move() entfernt - hoffnungsloses Unterfangen
 *
 * Revision 1.2  2007/11/05 01:08:09  willuhn
 * @N Funktion zum Verschieben eines Kontenrahmens (in progress)
 *
 * Revision 1.1  2007/10/08 22:54:47  willuhn
 * @N Kopieren eines kompletten Kontenrahmen auf einen Mandanten
 *
 **********************************************************************/
