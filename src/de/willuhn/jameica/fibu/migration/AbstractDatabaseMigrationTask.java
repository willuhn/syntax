package de.willuhn.jameica.fibu.migration;

import java.rmi.RemoteException;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Kontozuordnung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.Version;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Abstrakte Basis-Klasse für das Migrieren einer Datenbank.
 */
public abstract class AbstractDatabaseMigrationTask implements BackgroundTask
{
  protected final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  private boolean cancel = false;

  /**
   * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
   */
  public void interrupt()
  {
    this.cancel = true;
  }

  /**
   * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
   */
  public boolean isInterrupted()
  {
    return cancel;
  }

  /**
   * Führt das eigentliche Kopieren durch.
   * @param source die Datenquelle.
   * @param target das Datenziel.
   * @param monitor der Monitor.
   * @throws ApplicationException
   */
  protected void copy(DBService source, DBService target, ProgressMonitor monitor) throws ApplicationException
  {
	// Wir muessen wir das Aendern des Systemkontenrahmens kurzzeitig freigeben
	  boolean sysdataWritable = Settings.getSystemDataWritable();
	  if (!sysdataWritable)
	  {
	    Logger.info("activating change support for system data");
	    Settings.setSystemDataWritable(true);
	  }
    try
    {
      //Foreign Key check vorübergehen abschalten
  	  Logger.info("deactivating foreign_key_checks");
  	  target.executeUpdate("SET foreign_key_checks = 0", null);
  	  
  	  int failCount = 0;
  	  
      monitor.setPercentComplete(0);
      monitor.log(i18n.tr("Starte Datenmigration"));
      Logger.info("################################################");
      Logger.info("starting data migration");
      failCount += copy(source,target,monitor,Kontotyp.class);
      failCount += copy(source,target,monitor,Kontoart.class);
      failCount += copy(source,target,monitor,Finanzamt.class);
      failCount += copy(source,target,monitor,Mandant.class);
      failCount += copy(source,target,monitor,Kontenrahmen.class);
      failCount += copy(source,target,monitor,Geschaeftsjahr.class);
      failCount += copy(source,target,monitor,Konto.class);
      failCount += copy(source,target,monitor,Steuer.class);
      failCount += copy(source,target,monitor,Buchungstemplate.class);
      failCount += copy(source,target,monitor,Buchung.class);
      failCount += copy(source,target,monitor,Anlagevermoegen.class);
      failCount += copy(source,target,monitor,Anfangsbestand.class);
      failCount += copy(source,target,monitor,Abschreibung.class);
      failCount += copy(source,target,monitor,Version.class);
      failCount += copy(source,target,monitor,Kontozuordnung.class);
      Logger.info("finished data migration");
      Logger.info("################################################");
 
      if(failCount > 0)
      {
        monitor.setStatus(ProgressMonitor.STATUS_ERROR);
        monitor.setStatusText(i18n.tr("Abgeschlossen. {0} Datensätze konnten nicht kopiert werden",Integer.toString(failCount)));
      }
      else
      {
        monitor.setStatus(ProgressMonitor.STATUS_DONE);
        monitor.setStatusText(i18n.tr("Fertig"));
      }
    }
    catch (ApplicationException ae)
    {
      monitor.setStatusText(ae.getMessage());
      monitor.setStatus(ProgressMonitor.STATUS_ERROR);
      throw ae;
    }
    catch (Exception e)
    {
      monitor.setStatusText(e.getMessage());
      monitor.setStatus(ProgressMonitor.STATUS_ERROR);
      throw new ApplicationException(e);
    }
    finally
    {
      monitor.setPercentComplete(100);
      
      // Wieder zurueck aendern, wenns vorher nicht erlaubt war
      if (!sysdataWritable)
      {
        Logger.info("de-activating change support for system data");
        Settings.setSystemDataWritable(false);
      }
      Logger.info("re-activating foreign_key_checks");

      try
  	  {
		    target.executeUpdate("SET foreign_key_checks = 1", null);
	    }
  	  catch (RemoteException e)
  	  {
		    Logger.error("error while re-activating foreign_key_checks");
	    }
    }
  }
  
  /**
   * Kopiert eine einzelne Tabelle.
   * @param source die Datenquelle.
   * @param target das Datenziel.
   * @param monitor der Monitor.
   * @param type
   * @throws Exception
   * @return Anzahl der Datensätze, die nicht kopiert werden konnten.
   */
  private int copy(DBService source, DBService target, ProgressMonitor monitor, Class type) throws Exception
  {
    monitor.setStatusText(i18n.tr("Kopiere {0}",type.getSimpleName()));
    Logger.info("  copying " + type.getSimpleName());

    long count          = 0;
    int failCount       = 0;
    DBIterator i        = source.createList(type);
    AbstractDBObject to = null;

    while (!cancel && i.hasNext())
    {
      DBObject from = (DBObject) i.next();
      to            = (AbstractDBObject) target.createObject(type,null);

      String id = null;
      try
      {
        id = from.getID();
        to.transactionBegin();
        to.overwrite(from);
        if (++count % 100 == 0)
        {
          monitor.log(i18n.tr("  Kopierte Datensätze: {0}",Long.toString(count)));
          Logger.info("  copied records: " + count);
          monitor.addPercentComplete(1);
        }
        to.setID(id);
        to.insert();
        to.transactionCommit();
      }
      catch (Exception e)
      {
        failCount++;
        Logger.error("unable to copy record " + type.getName() + ":" + id + ": " + BeanUtil.toString(from),e);
        if (to == null)
        {
          monitor.log(i18n.tr("Fehler beim Kopieren des Datensatzes, überspringe"));
        }
        else
        {
          try
          {
            monitor.log(i18n.tr("  Fehler beim Kopieren von [ID: {0}]: {1}, überspringe", new String[]{id,BeanUtil.toString(to)}));
            to.transactionRollback();
          }
          catch (Exception e2)
          {
            // ignore
          }
        }
      }
    }
    monitor.addPercentComplete(5);
    return failCount;
  }
}
