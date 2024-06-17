package de.willuhn.jameica.fibu.migration;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.server.DBSupportMcKoiImpl;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.SystemMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;


/**
 * Wird benachrichtigt, wenn die Anwendung gestartet wurde
 * und zeigt ggf. ein Dialog fuer die Datenmigration von
 * Mckoi zu H2 an.
 */
public class McKoiToH2MigrationListener implements MessageConsumer
{

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  public boolean autoRegister()
  {
    return Settings.SETTINGS.getBoolean("migration.h2",true);
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  public Class[] getExpectedMessageTypes()
  {
    return new Class[]{SystemMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  public void handleMessage(Message message) throws Exception
  {
    if (message == null || !(message instanceof SystemMessage))
      return;
    
    if (((SystemMessage) message).getStatusCode() != SystemMessage.SYSTEM_STARTED)
      return;
    
    // Checken, ob Migration schon lief
    if (Settings.SETTINGS.getString("migration.mckoi-to-h2",null) != null)
      return; // lief bereits
    
    // Checken, ob ueberhaupt die McKoi-Datenbank genutzt wird
    String driver = Settings.SETTINGS.getString("database.support.class",null);
    if (driver == null || !driver.equals(DBSupportMcKoiImpl.class.getName()))
      return;
    
    if (!Settings.SETTINGS.getBoolean("migration.h2",true))
      return;

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    String text = i18n.tr("Das Datenbank-Format von Syntax wird umgestellt.\n" +
        "Möchten Sie jetzt die Übernahme der Daten in das neue Format durchführen?");
    if (!Application.getCallback().askUser(text))
      return;
    
    Logger.warn("starting database migration from mckoi to h2");
    Application.getController().start(new McKoiToH2MigrationTask());
  }

}
