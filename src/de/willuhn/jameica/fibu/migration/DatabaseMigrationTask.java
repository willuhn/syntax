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
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Task zum Migrieren der Datenbank.
 */
public class DatabaseMigrationTask implements BackgroundTask
{
  protected I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  private boolean cancel = false;
  protected DBService source = null;
  protected DBService target = null;

  /**
   * Legt die Datenquelle fest.
   * @param source Datenquelle.
   */
  public void setSource(DBService source)
  {
    this.source = source;
  }
  
  /**
   * Legt das Datenziel fest.
   * @param target Datenziel.
   */
  public void setTarget(DBService target)
  {
    this.target = target;
  }
  
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
   * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
   */
  public void run(ProgressMonitor monitor) throws ApplicationException
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
  	  
      monitor.setPercentComplete(0);
      monitor.log(i18n.tr("Starte Datenmigration"));
      Logger.info("################################################");
      Logger.info("starting data migration");
      copy(Kontotyp.class,monitor);
      copy(Kontoart.class,monitor);
      copy(Finanzamt.class,monitor);
      copy(Mandant.class,monitor);
      copy(Kontenrahmen.class,monitor);
      copy(Geschaeftsjahr.class,monitor);
      copy(Konto.class,monitor);
      copy(Steuer.class,monitor);
      copy(Buchungstemplate.class,monitor);
      copy(Buchung.class,monitor);
      copy(Anlagevermoegen.class,monitor);
      copy(Anfangsbestand.class,monitor);
      copy(Abschreibung.class,monitor);
 
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
      monitor.setPercentComplete(100);
      // Wieder zurueck aendern, wenns vorher nicht erlaubt war
      if (!sysdataWritable)
      {
        Logger.info("de-activating change support for system data");
        Settings.setSystemDataWritable(false);
      }
      Logger.info("re-activating foreign_key_checks");
  	  try {
		target.executeUpdate("SET foreign_key_checks = 1", null);
	  } catch (RemoteException e) {
		 Logger.error("error while re-activating foreign_key_checks");
	  }
    }
  }
  
  /**
   * Kann von der abgeleiteten Klasse ueberschrieben werden, um Daten zu korrigieren.
   * @param object das ggf noch zu korrigierende Objekt.
   * @param monitor Monitor.
   * @throws RemoteException
   */
  protected void fixObject(AbstractDBObject object, ProgressMonitor monitor) throws RemoteException
  {
  }
  
  /**
   * Kopiert eine einzelne Tabelle.
   * @param type Objekttyp.
   * @param monitor Monitor.
   * @throws Exception
   */
  protected void copy(Class type, ProgressMonitor monitor) throws Exception
  {
    monitor.setStatusText(i18n.tr("Kopiere " + type.getName()));
    Logger.info("  copying " + type.getName());

    long count          = 0;
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
          monitor.log(i18n.tr("  Kopierte Datensätze: {0}",""+count));
          Logger.info("  copied records: " + count);
          monitor.addPercentComplete(1);
        }
        to.setID(id);
        fixObject(to,monitor);
        to.insert();
        to.transactionCommit();
      }
      catch (Exception e)
      {
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
  }
}
