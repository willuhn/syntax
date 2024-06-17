package de.willuhn.jameica.fibu.migration;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.server.DBServiceImpl;
import de.willuhn.jameica.fibu.server.DBSupportH2Impl;
import de.willuhn.jameica.gui.internal.action.FileClose;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;


/**
 * Migration von McKoi nach H2.
 */
public class McKoiToH2MigrationTask extends DatabaseMigrationTask
{
  /**
   * @see de.willuhn.jameica.hbci.migration.DatabaseMigrationTask#run(de.willuhn.util.ProgressMonitor)
   */
  public void run(ProgressMonitor monitor) throws ApplicationException
  {
    // Checken, ob die Migration schon lief
    if (Settings.SETTINGS.getString("migration.mckoi-to-h2",null) != null)
      throw new ApplicationException(i18n.tr("Datenmigration bereits durchgeführt"));
    
    try
    {
      setSource(Settings.getDBService());
      
      H2DBServiceImpl target = new H2DBServiceImpl(monitor);
      target.start();
      setTarget(target);
    }
    catch (RemoteException re)
    {
      monitor.setStatusText(re.getMessage());
      monitor.setStatus(ProgressMonitor.STATUS_ERROR);
      throw new ApplicationException(re);
    }
    super.run(monitor);

    // Datum der Migration speichern
    Settings.SETTINGS.setAttribute("migration.mckoi-to-h2",new Date().toString());

    // Datenbank-Treiber umstellen
    Settings.SETTINGS.setAttribute("database.support.class",DBSupportH2Impl.class.getName());
    
    // User ueber Neustart benachrichtigen
    String text = i18n.tr("Datenmigration erfolgreich beendet.\nSyntax wird nun beendet. Starten Sie die Anwendung anschließend bitte neu.");
    try
    {
      Application.getCallback().notifyUser(text);
    }
    catch (Exception e)
    {
      Logger.error("unable to notify user about restart",e);
    }
    
    // Syntax beenden
    new FileClose().handleAction(null);
  }

  /**
   * Wrapper des DB-Service, damit die Identifier gross geschrieben werden.
   */
  public static class H2DBServiceImpl extends DBServiceImpl
  {
    /**
     * ct.
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

