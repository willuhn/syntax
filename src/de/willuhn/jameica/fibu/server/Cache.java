/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.logging.Logger;

/**
 * Cache fuer oft geladene Fachobjekte.
 * Uebernommen aus Hibiscus.
 */
class Cache
{
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(Cache.class);
  private static int timeout = 0;
  
  // Enthaelt alle Caches.
  private final static Map<Class,Cache> caches = new HashMap<Class,Cache>();
  
  // Der konkrete Cache
  private Map<String,DBObject> data = new HashMap<String,DBObject>(); // Als Map fuer schnellen Zugriff auf einzelne Werte
  private List<DBObject> values = new LinkedList<DBObject>();         // Als Liste fuer die Sicherstellung der Reihenfolge
  private Class<? extends DBObject> type = null;
  private long validTo = 0;
  
  static
  {
    settings.setStoreWhenRead(false);
    
    // Das Timeout betraegt nur 10 Sekunden. Mehr brauchen wir nicht.
    // Es geht ja nur um das haeufige Laden immer wieder gleicher Daten in kurzen Abstaenden
    timeout = settings.getInt("timeout.seconds",10);
  }

  /**
   * ct.
   */
  private Cache()
  {
    touch();
  }
  
  /**
   * Aktualisiert das Verfallsdatum des Caches.
   */
  private void touch()
  {
    this.validTo = System.currentTimeMillis() + (timeout * 1000);
  }

  /**
   * Loescht den genannten Cache.
   * @param type der Cache.
   */
  static void clear(Class<? extends DBObject> type)
  {
    Logger.info("clear cache of type " + type.getSimpleName());
    caches.remove(type);
  }
  
  /**
   * Liefert den Cache fuer den genannten Typ.
   * @param type der Typ.
   * @param init true, wenn der Cache bei der Erzeugung automatisch befuellt werden soll.
   * @return der Cache.
   * @throws RemoteException
   */
  static Cache get(final Class<? extends DBObject> type, boolean init) throws RemoteException
  {
    return get(type,new ObjectFactory() {
      public DBIterator load() throws RemoteException
      {
        Logger.info("loading objects of type " + type.getSimpleName() + " into cache");
        return Settings.getDBService().createList(type);
      }
    },init);
  }
  
  /**
   * Liefert den Cache fuer den genannten Typ.
   * @param type der Typ.
   * @param factory die Object-Factory.
   * @param init true, wenn der Cache bei der Erzeugung automatisch befuellt werden soll.
   * @return der Cache.
   * @throws RemoteException
   */
  static Cache get(Class<? extends DBObject> type, ObjectFactory factory, boolean init) throws RemoteException
  {
    Cache cache = caches.get(type);
    
    if (cache != null)
    {
      if (cache.validTo < System.currentTimeMillis())
      {
        Logger.debug("cache of type " + type.getSimpleName() + " expired");
        caches.remove(type);
        cache = null; // Cache wegwerfen
      }
      else
      {
        cache.touch(); // Verfallsdatum aktualisieren
      }
    }
    
    // Cache erzeugen und mit Daten fuellen
    if (cache == null)
    {
      cache = new Cache();
      cache.type = type;
      
      if (init)
      {
        // Daten in den Cache laden
        DBIterator list = factory.load();
        while (list.hasNext())
        {
          DBObject o = (DBObject) list.next();
          cache.data.put(o.getID(),o);
          cache.values.add(o);
        }
      }
      caches.put(type,cache);
    }
    return cache;
  }

  /**
   * Liefert ein Objekt aus dem Cache.
   * @param id die ID des Objektes.
   * @return das Objekt oder NULL, wenn es nicht existiert.
   * @throws RemoteException
   */
  DBObject get(Object id) throws RemoteException
  {
    if (id == null)
      return null;
    
    String s = id.toString();
    
    DBObject value = data.get(s);
    
    if (value == null)
    {
      // Noch nicht im Cache. Vielleicht koennen wir es noch laden
      try
      {
        value = Settings.getDBService().createObject(type,s);
        if (value == null)
          return null;
        data.put(value.getID(),value);
        values.add(value);
      }
      catch (ObjectNotFoundException one)
      {
        // Objekt existiert nicht mehr
      }
    }
    return value;
  }
  
  /**
   * Liefert alle Werte aus dem Cache.
   * @return Liste der Werte aus dem Cache.
   */
  Collection<DBObject> values()
  {
    return values;
  }
  
  /**
   * Interface fuer eine Faktory, die die Objekte laedt.
   */
  public static interface ObjectFactory
  {
    /**
     * Laedt die Objekte.
     * @return die Objekte.
     * @throws RemoteException
     */
    public DBIterator load() throws RemoteException;
  }
}
