package de.willuhn.jameica.fibu.migration;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.server.DBServiceImpl;
import de.willuhn.jameica.fibu.server.DBSupportH2Impl;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.FileClose;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Task zum Migrieren der McKoi-Datenbank in die H2-Datenbank.
 */
public class McKoiToH2MigrationTask extends AbstractDatabaseMigrationTask
{
  /**
   * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
   */
  public void run(ProgressMonitor monitor) throws ApplicationException
  {
    // Checken, ob die Migration schon lief
    if (Settings.SETTINGS.getString("migration.mckoi-to-h2",null) != null)
      throw new ApplicationException(i18n.tr("Datenmigration bereits durchgeführt"));

    DBService target = null;
    try
    {
      final DBService source = Settings.getDBService();
      target = new H2DBServiceImpl(monitor);
      target.start();
      
      // Foreign Key check vorübergehen abschalten
      try
      {
        Logger.info("deactivating foreign_key_checks");
        target.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE", null);
      }
      catch (Exception e)
      {
        Logger.error("unable to disable foreign key checks",e);
      }

      this.copy(source,target,monitor);

      // Datum der Migration speichern
      Settings.SETTINGS.setAttribute("migration.mckoi-to-h2",new Date().toString());

      // Datenbank-Treiber umstellen
      Settings.SETTINGS.setAttribute("database.support.class",DBSupportH2Impl.class.getName());
    
    }
    catch (Exception e)
    {
      monitor.setStatusText(e.getMessage());
      monitor.setStatus(ProgressMonitor.STATUS_ERROR);
      throw new ApplicationException(e);
    }
    finally
    {
      Logger.info("re-activating foreign_key_checks");
      try
      {
        if (target != null)
          target.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE", null);
      }
      catch (Exception e)
      {
        Logger.error("error while re-activating foreign_key_checks",e);
      }
    }
    
    
    // User ueber Neustart benachrichtigen, ihm aber die Chance geben, den Neustart abzubrechen,
    // damit er die Logmeldungen kopieren kann
    try
    {
      if (Application.getCallback().askUser(i18n.tr("Datenmigration abgeschlossen. Anwendung wird jetzt beendet.")))
      {
        new FileClose().handleAction(null);
      }
      else
      {
        // Startseite neu laden, damit die Box verschwindet
        GUI.startView(GUI.getCurrentView(),null);
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to notify user about restart",e);
    }
  }

  /**
   * Wrapper des DB-Service, damit die Identifier gross geschrieben werden.
   */
  public static class H2DBServiceImpl extends DBServiceImpl
  {
    /**
     * ct.
     * @param monitor
     * @throws RemoteException
     */
    public H2DBServiceImpl(ProgressMonitor monitor) throws RemoteException
    {
      super(new DBSupportH2Impl(true,monitor));
      
      // Der Konstruktor von DBSupportH2Impl hat bereits Gross-Schreibung
      // fuer DBService aktiviert - nochmal fuer die Migration
      // deaktivieren
      System.setProperty(DBServiceImpl.class.getName() + ".uppercase","false");    

      // Fuer uns selbst aktivieren wir es jedoch
      System.setProperty(H2DBServiceImpl.class.getName() + ".uppercase","true");    
    }
  }

}
