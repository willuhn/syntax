/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/MandantImpl.java,v $
 * $Revision: 1.29 $
 * $Date: 2011/05/12 09:10:31 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
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
}


/*********************************************************************
 * $Log: MandantImpl.java,v $
 * Revision 1.29  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.28  2011-03-25 10:14:10  willuhn
 * @N Loeschen von Mandanten und Beruecksichtigen der zugeordneten Konten und Kontenrahmen
 * @C BUGZILLA 958
 *
 * Revision 1.27  2009-07-03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.26.2.1  2008/09/08 09:03:52  willuhn
 * @C aktiver Mandant/aktives Geschaeftsjahr kann nicht mehr geloescht werden
 *
 * Revision 1.26  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.25  2006/01/08 15:28:41  willuhn
 * @N Loeschen von Sonderabschreibungen
 *
 * Revision 1.24  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.23  2006/01/04 00:53:48  willuhn
 * @B bug 166 Ausserplanmaessige Abschreibungen
 *
 * Revision 1.22  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.21  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.20  2005/09/27 17:41:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.19  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.17  2005/08/25 21:58:58  willuhn
 * @N SKR04
 *
 * Revision 1.16  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.14  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.13  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.12  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.11  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.9  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.8  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.7  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.6  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.4  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 * Revision 1.3  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.2  2003/11/24 16:26:16  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/