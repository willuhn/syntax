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
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class SteuerImpl extends AbstractUserObjectImpl implements Steuer
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Erzeugt einen neuen Steuersatz.
   * @throws RemoteException
   */
  public SteuerImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "steuer";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getSatz()
   */
  public double getSatz() throws RemoteException
  {
    Double d = (Double) getAttribute("satz");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name", name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setSatz(double)
   */
  public void setSatz(double satz) throws RemoteException
  {
    setAttribute("satz", new Double(satz));
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    super.deleteCheck();
    try {

      // wir checken ob vielleicht ein Konto diesen Steuersatz besitzt.
      DBIterator list = Settings.getDBService().createList(Konto.class);
      list.addFilter("steuer_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Der Steuersatz ist einem Konto zugewiesen.\n" +
          "Bitte ändern oder löschen zu Sie zunächst das Konto."));
    }
    catch (RemoteException e)
    {
			Logger.error("error while checking dependencies",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Abhängigkeiten."));
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try {
      if (getName() == null || "".equals(getName()))
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Bezeichnung für den Steuersatz ein."));
      
      if (getSatz() < 0)
        throw new ApplicationException(i18n.tr("Steuersatz darf nicht kleiner als 0 sein."));
      
      if (getSteuerKonto() == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Steuer-Sammelkonto aus"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking dependencies",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Pflichtfelder."),e);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getSteuerKonto()
   */
  public Konto getSteuerKonto() throws RemoteException
  {
    Integer i = (Integer) super.getAttribute("steuerkonto_id");
    if (i == null)
      return null;
   
    Cache cache = Cache.get(Konto.class,true);
    return (Konto) cache.get(i);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setSteuerKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setSteuerKonto(Konto k) throws RemoteException
  {
    setAttribute("steuerkonto_id",k == null || k.getID() == null ? null : new Integer(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getUstNrSteuer()
   */
  public String getUstNrSteuer() throws RemoteException
  {
    return (String) getAttribute("ust_nr_steuer");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setUstNrSteuer(java.lang.String)
   */
  public void setUstNrSteuer(String s) throws RemoteException
  {
    setAttribute("ust_nr_steuer",s);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getUstNrBemessung()
   */
  public String getUstNrBemessung() throws RemoteException
  {
    return (String) getAttribute("ust_nr_bemessung");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setUstNrBemessung(java.lang.String)
   */
  public void setUstNrBemessung(String s) throws RemoteException
  {
    setAttribute("ust_nr_bemessung",s);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#delete()
   */
  @Override
  public void delete() throws RemoteException, ApplicationException
  {
    super.delete();
    Cache.clear(Steuer.class);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#store()
   */
  @Override
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    Cache.clear(Steuer.class);
  }

}
