/**********************************************************************
 *
 * Copyright (c) 2024 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.version.Update;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Klasse der Datenbank-Updates.
 */
public abstract class AbstractUpdate implements Update
{
  protected final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Führt die Funktion auf dem DB-Service aus.
   * @param <T> der beliebige Rückgabetyp der Funktion. Kann Void sein.
   * @param f die Funktion.
   * @return der Rüeckgabewert. Kann NULL sein.
   * @throws ApplicationException
   */
  protected <T> T execute(DBServiceUpdate<T> f) throws ApplicationException
  {
    DBService db = null;
    
    try
    {
      // Wir erzeugen eine eigene Instanz, weil die "offizielle" zu dem Zeitpunkt noch nicht
      // verfügbar ist (wir sind ja gerade in deren Initialisierung)
      db = new DBServiceImpl(false);
      db.start();
      return f.execute(db);
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("unable to execute update",e);
      throw new ApplicationException(i18n.tr("Update \"{0}\" fehlgeschlagen: {1}",this.getName(),e.getMessage()),e);
    }
    finally
    {
      if (db != null)
      {
        try
        {
          db.stop(true);
        }
        catch (Exception e) {}
      }
    }
  }
  
  /**
   * Ein Update, das direkt auf dem DBService ausgefuehrt wird.
   * @param <T> der Rückgabetyp. Kann Void sein.
   */
  @FunctionalInterface
  public static interface DBServiceUpdate<T>
  {
    /**
     * Führt das Update aus.
     * @param db der Datenbank-Service.
     * @return der Rückgabetyp. Kann NULL sein.
     * @throws Exception
     */
    public T execute(DBService db) throws Exception;
    
  }

}


