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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 * 24.11.2003
 */
public class MandantImpl extends AbstractDBObject implements Mandant
{
  private transient I18N i18n = null;

  /**
   * Erzeugt einen neuen Mandanten.
   * @throws RemoteException
   */
  public MandantImpl() throws RemoteException
  {
    super();
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "mandant";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "firma";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getName1()
   */
  public String getName1() throws RemoteException
  {
    return (String) getAttribute("name1");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getName2()
   */
  public String getName2() throws RemoteException
  {
    return (String) getAttribute("name2");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getFirma()
   */
  public String getFirma() throws RemoteException
  {
    return (String) getAttribute("firma");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getStrasse()
   */
  public String getStrasse() throws RemoteException
  {
    return (String) getAttribute("strasse");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getPLZ()
   */
  public String getPLZ() throws RemoteException
  {
    return (String) getAttribute("plz");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getOrt()
   */
  public String getOrt() throws RemoteException
  {
    return (String) getAttribute("ort");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getSteuernummer()
   */
  public String getSteuernummer() throws RemoteException
  {
    return (String) getAttribute("steuernummer");
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    if ("finanzamt_id".equals(field))
      return Finanzamt.class;
    return null;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setName1(java.lang.String)
   */
  public void setName1(String name1) throws RemoteException
  {
    setAttribute("name1",name1);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setName2(java.lang.String)
   */
  public void setName2(String name2) throws RemoteException
  {
    setAttribute("name2",name2);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setFirma(java.lang.String)
   */
  public void setFirma(String firma) throws RemoteException
  {
    setAttribute("firma",firma);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setStrasse(java.lang.String)
   */
  public void setStrasse(String strasse) throws RemoteException
  {
    setAttribute("strasse",strasse);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setPLZ(java.lang.String)
   */
  public void setPLZ(String plz) throws RemoteException
  {
    setAttribute("plz",plz);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setOrt(java.lang.String)
   */
  public void setOrt(String ort) throws RemoteException
  {
    setAttribute("ort",ort);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setSteuernummer(java.lang.String)
   */
  public void setSteuernummer(String steuernummer) throws RemoteException
  {
    setAttribute("steuernummer",steuernummer);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getFinanzamt()
   */
  public Finanzamt getFinanzamt() throws RemoteException
  {
    return (Finanzamt) getAttribute("finanzamt_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setFinanzamt(de.willuhn.jameica.fibu.rmi.Finanzamt)
   */
  public void setFinanzamt(Finanzamt finanzamt) throws RemoteException
  {
    setAttribute("finanzamt_id",finanzamt);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    if (Settings.inUpdate())
      return;

    try {
      String firma = getFirma();
      if (firma == null || "".equals(firma))
        throw new ApplicationException(i18n.tr("Bitte geben Sie die Firma ein."));
  
      if (getFinanzamt() == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Finanzamt aus."));


    }
    catch (RemoteException e)
    {
      Logger.error("error while checking mandant",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Pflichtfelder."));
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    insertCheck();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getWaehrung()
   */
  public String getWaehrung() throws RemoteException
  {
    String s = (String) getAttribute("waehrung");
    if (s != null && s.length() > 0)
      return s;
    return Settings.WAEHRUNG;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setWaehrung(java.lang.String)
   */
  public void setWaehrung(String waehrung) throws RemoteException
  {
    setAttribute("waehrung",waehrung);
  }


  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getKuerzel()
   */
  public String getKuerzel() throws RemoteException
  {
    return (String) getAttribute("kuerzel");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setKuerzel(java.lang.String)
   */
  public void setKuerzel(String kuerzel) throws RemoteException
  {
    setAttribute("kuerzel",kuerzel);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getGeschaeftsjahre()
   */
  public DBIterator getGeschaeftsjahre() throws RemoteException
  {
    DBIterator list = getService().createList(Geschaeftsjahr.class);
    list.addFilter("mandant_id = " + this.getID());
    return list;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getAnlagevermoegen()
   */
  public DBIterator getAnlagevermoegen() throws RemoteException
  {
    DBIterator list = getService().createList(Anlagevermoegen.class);
    list.addFilter("mandant_id = " + this.getID());
    return list;
  }

  /**
   * Ueberschrieben, um alle Geschaeftsjahre inclusive aller Buchungen und Anfangsbestaende zu loeschen.
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    boolean sysChange = Settings.getSystemDataWritable();
    
    try
    {
      Logger.info("Lösche Mandant " + getAttribute(getPrimaryAttribute()));
      Cache.clear(Mandant.class);

      transactionBegin();
      
      if (!sysChange)
        Settings.setSystemDataWritable(true);

      DBIterator av = getAnlagevermoegen();
      while (av.hasNext())
      {
        Anlagevermoegen a = (Anlagevermoegen) av.next();
        a.delete();
      }
      
      DBIterator bt = getBuchungstemplates();
      while (bt.hasNext())
      {
        Buchungstemplate t = (Buchungstemplate) bt.next();
        t.delete();
      }

      DBIterator jahre = getGeschaeftsjahre();
      while (jahre.hasNext())
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) jahre.next();
        jahr.delete();
      }

      {
        DBIterator list = getService().createList(Kontenrahmen.class);
        list.addFilter("mandant_id = " + this.getID());
        while (list.hasNext())
        {
          Kontenrahmen kr = (Kontenrahmen) list.next();
          kr.setMandant(null);
          kr.store();
        }
      }
      {
        DBIterator list = getService().createList(Konto.class);
        list.addFilter("mandant_id = " + this.getID());
        while (list.hasNext())
        {
          Konto k = (Konto) list.next();
          k.setMandant(null);
          k.store();
        }
      }
      {
        DBIterator list = getService().createList(Steuer.class);
        list.addFilter("mandant_id = " + this.getID());
        while (list.hasNext())
        {
          Steuer s = (Steuer) list.next();
          s.setMandant(null);
          s.store();
        }
      }

      super.delete();

      transactionCommit();
    }
    catch (ApplicationException e)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      throw e;
    }
    catch (RemoteException e2)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      throw e2;
    }
    catch (Throwable t)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      Logger.error("unable to delete mandant",t);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Mandanten"));
    }
    finally
    {
      Settings.setSystemDataWritable(sysChange);
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    // Das Loeschen des Mandanten mit dem aktiven Geschaeftsjahr wuerde
    // anschliessend einen Fehler ausloesen.
    try
    {
      Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
      if (current != null)
      {
        Mandant cm = current.getMandant();
        if (cm.equals(this))
          throw new ApplicationException(i18n.tr("Mandant mit dem aktiven Geschäftsjahr kann nicht gelöscht werden. Bitte aktivieren Sie zuerst ein anderes Jahr."));
      }
    }
    catch (RemoteException re)
    {
      Logger.error("error while performing delete check",re);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Mandanten: {0}",re.getMessage()));
    }
    super.deleteCheck();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getBuchungstemplates()
   */
  public DBIterator getBuchungstemplates() throws RemoteException
  {
    DBIterator list = Settings.getDBService().createList(Buchungstemplate.class);
    list.addFilter("mandant_id = " + getID());
    return list;
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#store()
   */
  @Override
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    Cache.clear(Mandant.class);
  }

}
