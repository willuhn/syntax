/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.messaging.QueryMessage;
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
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();


  /**
   * Erstellt einen neuen Kontenrahmen basierend auf dem uebergebenen.
   * @param template das zu verwendende Kontenrahmen-Template
   * @param mandant optionale Angabe des Mandanten, dem der Kontenrahmen zugeordnet werden soll.
   * @param name Name des neuen Kontenrahmen.
   * @param monitor Monitor zur Ueberwachung der Erstellung.
   * @return der neue Kontenrahmen.
   * @throws ApplicationException
   */
  public static Kontenrahmen clone(Kontenrahmen template, Mandant mandant, String name, ProgressMonitor monitor) throws ApplicationException
  {
    Kontenrahmen kr = null;
    
    if (name == null || name.trim().length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Namen für den neuen Kontenrahmen an"));
    
    if (template == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Kontenrahmen als Vorlage aus"));
    
    // Wenn kein Mandant angegeben ist, muessen wir das Aendern des Systemkontenrahmens kurzzeitig freigeben
    //Auch wenn ein Mandant angegeben ist, da die Konten im neuen Kontenrahemn als Systemkonten gespeichert werden
    boolean sysdataWritable = Settings.getSystemDataWritable();
    if (!sysdataWritable)
    {
      Logger.info("activating change support for system data");
      Settings.setSystemDataWritable(true);
    }
    
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
      
      // Checken, ob ein Kontenrahmen mit diesem Namen schon existiert
      DBIterator list = service.createList(Kontenrahmen.class);
      list.addFilter("name = ?",new Object[]{name});
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Ein Kontenrahmen mit diesem Namen existiert bereits"));
      
      // Kontenrahmen erstellen
      if (monitor != null) monitor.setStatusText(i18n.tr("Erstelle Kontenrahmen"));
      kr = (Kontenrahmen) service.createObject(Kontenrahmen.class,null);
      kr.transactionBegin();

      kr.overwrite(template);
      kr.setName(name);
      kr.setMandant(mandant);
      kr.store();
      
      // Wir cachen die angelegten Steuersaetze um Doppler zu vermeiden
      Map<String,Steuer> steuerCache = new HashMap<String,Steuer>();
      Map<String,Konto> kontoCache  = new HashMap<String,Konto>();
      
      // Konten anlegen
      if (monitor != null) monitor.setStatusText(i18n.tr("Erstelle Konten"));
      DBIterator konten = template.getKonten();
      konten.addFilter("mandant_id is null");
      
      if (monitor != null) monitor.setPercentComplete(0);
      double factor = 100d / konten.size();
      int count = 0;

      while (konten.hasNext())
      {
        Konto kt  = (Konto) konten.next();

        if (monitor != null)
        {
          monitor.setPercentComplete((int)((++count) * factor));
          monitor.log("  " + kt.getKontonummer() + ": " + kt.getName());
        }

        Konto k   = (Konto) service.createObject(Konto.class,null);
        k.overwrite(kt);
        k.setKontenrahmen(kr);
        k.setMandant(kt.getMandant() != null ? mandant : null);

        // ggf. vorhandener Steuersatz
        Steuer st = kt.getSteuer();
        if (st != null)
        {
          // checken, ob wir den Steuersatz schon angelegt haben
          Steuer s = steuerCache.get(st.getID());
          if (s == null)
          {
            // Haben wir noch nicht, also anlegen
            s = (Steuer) service.createObject(Steuer.class,null);
            s.overwrite(st);
            s.setMandant(st.getMandant() != null ? mandant : null);
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
      Iterator<Steuer> i = steuerCache.values().iterator();
      while (i.hasNext())
      {
        Steuer s = i.next();
        Konto  k = kontoCache.get(s.getSteuerKonto().getID());
        s.setSteuerKonto(k);
        s.store();
      }
      
      kr.transactionCommit();
      if (monitor != null) monitor.setStatusText(i18n.tr("Kontenrahmen erstellt"));
      Application.getMessagingFactory().getMessagingQueue(Settings.QUEUE_KONTENRAHMEN_CREATED).sendMessage(new QueryMessage(kr));
      return kr;
    }
    catch (Exception e)
    {
      if (kr != null)
      {
        try
        {
          kr.transactionRollback();
        }
        catch (Exception e2)
        {
          Logger.error("unable to rollback tx",e2);
        }
      }
      
      if (e instanceof ApplicationException)
        throw (ApplicationException) e;

      Logger.error("unable to clone data",e);
      throw new ApplicationException(i18n.tr("Kopieren fehlgeschlagen: {0}",e.getMessage()));
    }
    finally
    {
      // Wieder zurueck aendern, wenns vorher nicht erlaubt war
      if (mandant == null && !sysdataWritable)
      {
        Logger.info("de-activating change support for system data");
        Settings.setSystemDataWritable(false);
      }
    }
  }
}

/**********************************************************************
 * $Log: KontenrahmenUtil.java,v $
 * Revision 1.7  2011/03/25 10:14:10  willuhn
 * @N Loeschen von Mandanten und Beruecksichtigen der zugeordneten Konten und Kontenrahmen
 * @C BUGZILLA 958
 *
 * Revision 1.6  2011-03-21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 * Revision 1.5  2009-07-03 10:52:19  willuhn
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
