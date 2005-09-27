/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/MandantImpl.java,v $
 * $Revision: 1.20 $
 * $Date: 2005/09/27 17:41:27 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
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
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
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
  public Class getForeignObject(String field) throws RemoteException
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
  public void insertCheck() throws ApplicationException
  {
    try {
      String firma = getFirma();
      if (firma == null || "".equals(firma))
        throw new ApplicationException(i18n.tr("Bitte geben Sie die Firma ein."));
  
      if (getFinanzamt() == null)
        throw new ApplicationException(i18n.tr("Bitte w�hlen Sie ein Finanzamt aus."));


    }
    catch (RemoteException e)
    {
      Logger.error("error while checking mandant",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Pr�fung der Pflichtfelder."));
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
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
    try
    {
      transactionBegin();

      DBIterator jahre = getGeschaeftsjahre();
      while (jahre.hasNext())
      {
        Geschaeftsjahr jahr = (Geschaeftsjahr) jahre.next();
        jahr.delete();
      }
      
      DBIterator av = getAnlagevermoegen();
      while (av.hasNext())
      {
        Anlagevermoegen a = (Anlagevermoegen) av.next();
        a.delete();
      }
      super.delete();
      
      transactionCommit();
    }
    catch (ApplicationException e)
    {
      transactionRollback();
      throw e;
    }
    catch (RemoteException e2)
    {
      transactionRollback();
      throw e2;
    }
    catch (Throwable t)
    {
      Logger.error("unable to delete mandant",t);
      throw new ApplicationException(i18n.tr("Fehler beim L�schen des Mandanten"));
    }
  }
}


/*********************************************************************
 * $Log: MandantImpl.java,v $
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