/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class KontenrahmenImpl extends AbstractUserObjectImpl implements Kontenrahmen
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * Erzeugt einen neuen Kontorahmen.
   * @throws RemoteException
   */
  public KontenrahmenImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "kontenrahmen";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try {
      String name = (String) getAttribute("name");
      if (name == null || "".equals(name))
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Namen für den Kontenrahmen ein."));
      
      // Checken, ob schon ein gleichnamiger Kontenrahmen existiert
      DBIterator list = getService().createList(Kontenrahmen.class);
      list.addFilter("name = ?",name);
      if (!this.isNewObject())
        list.addFilter("id != " + this.getID()); // Und wir sind es nicht selbst
      
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Ein Kontenrahmen mit diesem Namen existiert bereits."));
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung des Kontenrahmens."),e);
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    super.deleteCheck();
    try {

      DBIterator list = Settings.getDBService().createList(Konto.class);
      list.addFilter("kontenrahmen_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Der Kontenrahmen enthält Konten. Bitte löschen zu Sie zunächst die Konten."));

      list = Settings.getDBService().createList(Geschaeftsjahr.class);
      list.addFilter("kontenrahmen_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Es existieren bereits Geschäftsjahre mit diesem Kontenrahmen."));

    }
    catch (RemoteException e)
    {
      Logger.error("error while checking dependencies",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Abhängigkeiten."));
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#getKonten()
   */
  public DBIterator getKonten() throws RemoteException
  {
    DBIterator list = getService().createList(Konto.class);
    list.addFilter("(kontenrahmen_id = " + this.getID() + ")");
    list.setOrder(Settings.getAccountOrder());
    return list;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#findByKontonummer(java.lang.String)
   */
  public Konto findByKontonummer(String kto) throws RemoteException
  {
    DBIterator konten = getKonten();
    konten.addFilter("kontonummer = ?",kto);
    return konten.hasNext() ? (Konto) konten.next() : null;
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#delete()
   */
  @Override
  public void delete() throws RemoteException, ApplicationException
  {
    super.delete();
    Cache.clear(Kontenrahmen.class);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#store()
   */
  @Override
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    Cache.clear(Kontenrahmen.class);
  }

}
