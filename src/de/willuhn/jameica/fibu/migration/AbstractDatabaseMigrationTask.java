package de.willuhn.jameica.fibu.migration;

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
      Settings.setInUpdate(true);
  	  
      monitor.setPercentComplete(0);
      monitor.log(i18n.tr("Starte Datenmigration"));
      Logger.info("################################################");
      Logger.info("starting data migration");
      this.copy(source,target,monitor,Kontotyp.class);
      this.copy(source,target,monitor,Kontoart.class);
      this.copy(source,target,monitor,Finanzamt.class);
      this.copy(source,target,monitor,Mandant.class);
      this.copy(source,target,monitor,Kontenrahmen.class);
      this.copy(source,target,monitor,Geschaeftsjahr.class);
      this.copy(source,target,monitor,Konto.class);
      this.copy(source,target,monitor,Steuer.class);
      this.copy(source,target,monitor,Buchungstemplate.class);
      this.copy(source,target,monitor,Buchung.class);
      this.copy(source,target,monitor,Anlagevermoegen.class);
      this.copy(source,target,monitor,Anfangsbestand.class);
      this.copy(source,target,monitor,Abschreibung.class);
      this.copy(source,target,monitor,Version.class);
      this.copy(source,target,monitor,Kontozuordnung.class);
      Logger.info("finished data migration");
      Logger.info("################################################");
 
      monitor.setStatus(ProgressMonitor.STATUS_DONE);
      monitor.setStatusText(i18n.tr("Fertig"));
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
      Settings.setInUpdate(false);
      monitor.setPercentComplete(100);
      
      // Wieder zurueck aendern, wenns vorher nicht erlaubt war
      if (!sysdataWritable)
      {
        Logger.info("de-activating change support for system data");
        Settings.setSystemDataWritable(false);
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
   */
  private void copy(DBService source, DBService target, ProgressMonitor monitor, Class type) throws Exception
  {
    monitor.setStatusText(i18n.tr("Kopiere {0}",type.getSimpleName()));
    Logger.info("  copying " + type.getSimpleName());

    long count = 0;

    final DBIterator i = source.createList(type);
    while (!cancel && i.hasNext())
    {
      final DBObject from       = (DBObject) i.next();
      final AbstractDBObject to = (AbstractDBObject) target.createObject(type,null);

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
        Logger.error("unable to copy record " + type.getSimpleName() + ":" + id + ": " + BeanUtil.toString(from),e);
        if (to != null)
        {
          try
          {
            to.transactionRollback();
          }
          catch (Exception e2)
          {
            // ignore
          }
        }
        monitor.log(i18n.tr("  Fehler beim Kopieren von [Typ: {0}, ID: {1}]: {2}", new String[]{type.getSimpleName(),id,BeanUtil.toString(to)}));
        throw e;
      }
    }
    monitor.addPercentComplete(5);
  }
}
