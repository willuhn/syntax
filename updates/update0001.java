/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/updates/Attic/update0001.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/06/01 17:42:03 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

import java.io.StringReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.server.DBSupportMcKoiImpl;
import de.willuhn.jameica.fibu.server.DBSupportMySqlImpl;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.ScriptExecutor;
import de.willuhn.sql.version.Update;
import de.willuhn.sql.version.UpdateProvider;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;


/**
 * Datenbank-Update zum Erstellen der Version-Tabelle.
 */
public class update0001 implements Update
{
  private Map statements = new HashMap();
  
  /**
   * ct
   */
  public update0001()
  {
    // Update fuer McKoi
    statements.put(DBSupportMcKoiImpl.class.getName(),
        "CREATE TABLE version (" +
        "    id NUMERIC default UNIQUEKEY('version')," +
        "    name varchar(255) NOT NULL," +
        "    version int(5) NOT NULL," +
        "    UNIQUE (id)," +
        "    PRIMARY KEY (id)" +
        ");\n");
    
    // Update fuer MySQL
    statements.put(DBSupportMySqlImpl.class.getName(),
        "CREATE TABLE version (" +
        "    id int(10) AUTO_INCREMENT," +
        "    name varchar(255) NOT NULL," +
        "    version int(5) NOT NULL," +
        "    UNIQUE (id)," +
        "    PRIMARY KEY (id)" +
        ") TYPE = InnoDB;\n");
  }

  /**
   * @see de.willuhn.sql.version.Update#execute(de.willuhn.sql.version.UpdateProvider)
   */
  public void execute(UpdateProvider provider) throws ApplicationException
  {
    // Wenn wir eine Tabelle erstellen wollen, muessen wir wissen, welche
    // SQL-Dialekt wir sprechen
    String driver = Settings.SETTINGS.getString("database.support.class",null);
    String sql = (String) statements.get(driver);
    if (sql == null)
      throw new ApplicationException(Application.getI18n().tr("Datenbank {0} wird nicht unterstützt",driver));
    
    try
    {
      Connection conn   = provider.getConnection();
      ProgressMonitor m = provider.getProgressMonitor();

      ScriptExecutor.execute(new StringReader(sql),conn,m);
      ScriptExecutor.execute(new StringReader("INSERT INTO version (name,version) values ('db',0)"),conn,m);
      m.log(Application.getI18n().tr("Update ausgeführt"));
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("unable to execute update",e);
      throw new ApplicationException(Application.getI18n().tr("Fehler beim Ausführen des Updates"),e);
    }
  }

  /**
   * @see de.willuhn.sql.version.Update#getName()
   */
  public String getName()
  {
    return "Datenbank-Update für zur Erstellung der Versions-Tabelle";
  }

}


/*********************************************************************
 * $Log: update0001.java,v $
 * Revision 1.1  2010/06/01 17:42:03  willuhn
 * @N Neues Update-Verfahren via UpdateProvider
 *
 * Revision 1.6  2008/10/12 22:10:20  willuhn
 * @B Typo in den Updates
 * @B Spalten-Sortierung und -breite fuer in den Positionen von Sammelauftraegen nicht gespeichert
 *
 * Revision 1.5  2008/06/15 21:55:51  willuhn
 * @N update007 - Spalte "content" vergroessert
 * @B Fix in update002 - verursachte Fehler auf alten MySQL-Versionen
 *
 * Revision 1.4  2007/12/12 10:02:44  willuhn
 * @N Datenbank-Updates auch in Create-Scripts nachziehen
 *
 * Revision 1.3  2007/12/11 16:10:11  willuhn
 * @N Erster Code fuer "Offene Posten-Verwaltung"
 *
 * Revision 1.2  2007/12/11 15:25:18  willuhn
 * @N Class-Update fuer neue Tabellen "op" und "op_buchung"
 *
 * Revision 1.1  2007/12/11 15:23:53  willuhn
 * @N Class-Update fuer neue Tabellen "op" und "op_buchung"
 *
 **********************************************************************/