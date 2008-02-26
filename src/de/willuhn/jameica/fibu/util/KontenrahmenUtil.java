/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/util/KontenrahmenUtil.java,v $
 * $Revision: 1.4 $
 * $Date: 2008/02/26 19:13:23 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
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
      
      DBIterator konten    = template.getKonten();
      DBIterator steuern   = template.getSteuersaetze();
      DBIterator templates = template.getBuchungstemplates();
      
      if (monitor != null) monitor.setPercentComplete(0);
      double factor = 100d / (konten.size() + steuern.size() + templates.size());
      int count = 0;

      if (monitor != null) monitor.setStatusText(i18n.tr("Kopiere Konten"));
      while (konten.hasNext())
      {
        if (monitor != null) monitor.setPercentComplete((int)((++count) * factor));

        Konto source = (Konto) konten.next();
        Konto target = (Konto) service.createObject(Konto.class,null);
        
        target.overwrite(source);
        target.setKontenrahmen(kr); // Neuen Kontenrahmen angeben
        target.store();
      }
      
      if (monitor != null) monitor.setStatusText(i18n.tr("Kopiere Steuersätze"));
      while (steuern.hasNext())
      {
        if (monitor != null) monitor.setPercentComplete((int)((++count) * factor));

        Steuer source = (Steuer) steuern.next();
        Steuer target = (Steuer) service.createObject(Steuer.class,null);
        
        target.overwrite(source);
        target.setKontenrahmen(kr); // Neuen Kontenrahmen angeben
        target.store();
      }

      if (monitor != null) monitor.setStatusText(i18n.tr("Kopiere Buchungsvorlagen"));
      while (templates.hasNext())
      {
        if (monitor != null) monitor.setPercentComplete((int)((++count) * factor));

        Buchungstemplate source = (Buchungstemplate) templates.next();
        Buchungstemplate target = (Buchungstemplate) service.createObject(Buchungstemplate.class,null);
        
        target.overwrite(source);
        target.setKontenrahmen(kr); // Neuen Kontenrahmen angeben
        target.store();
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
 * Revision 1.4  2008/02/26 19:13:23  willuhn
 * *** empty log message ***
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
