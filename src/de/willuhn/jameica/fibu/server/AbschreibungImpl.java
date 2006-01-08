/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbschreibungImpl.java,v $
 * $Revision: 1.8 $
 * $Date: 2006/01/08 15:28:41 $
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
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung einer einzelnen Abschreibung.
 */
public class AbschreibungImpl extends AbstractDBObject implements Abschreibung
{

  private transient I18N i18n = null;
  
  /**
   * @throws java.rmi.RemoteException
   */
  public AbschreibungImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "abschreibung";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "buchung_id";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#getAnlagevermoegen()
   */
  public Anlagevermoegen getAnlagevermoegen() throws RemoteException
  {
    return (Anlagevermoegen) getAttribute("av_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#getBuchung()
   */
  public AbschreibungsBuchung getBuchung() throws RemoteException
  {
    return (AbschreibungsBuchung) getAttribute("buchung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#setAnlagevermoegen(de.willuhn.jameica.fibu.rmi.Anlagevermoegen)
   */
  public void setAnlagevermoegen(Anlagevermoegen av) throws RemoteException
  {
    setAttribute("av_id",av);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#setBuchung(de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung)
   */
  public void setBuchung(AbschreibungsBuchung b) throws RemoteException
  {
    setAttribute("buchung_id",b);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String arg0) throws RemoteException
  {
    if ("av_id".equals(arg0))
      return Anlagevermoegen.class;
    if ("buchung_id".equals(arg0))
      return AbschreibungsBuchung.class;
    
    return super.getForeignObject(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      Geschaeftsjahr jahr = ((DBService)getService()).getActiveGeschaeftsjahr();
      if (jahr.isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

      if (getAnlagevermoegen() == null)
        throw new ApplicationException(i18n.tr("Kein Anlage-Gegenstand zugeordnet"));
      if (getBuchung() == null)
        throw new ApplicationException(i18n.tr("Keine Abschreibungs-Buchung zugeordnet"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking abschreibung",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Abschreibung"));
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
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    // Wir muessen die Buchung mit loeschen
    try
    {
      transactionBegin();

      Logger.info("Lösche zugehörige Abschreibungsbuchung");
      AbschreibungsBuchung b = getBuchung();
      if (b != null)
        b.delete();

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
      Logger.error("unable to delete abschreibung",t);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen der Abschreibung"));
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#isSonderabschreibung()
   */
  public boolean isSonderabschreibung() throws RemoteException
  {
    Integer i = (Integer) getAttribute("sonderabschreibung");
    return i != null && i.intValue() == 1;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#setSonderabschreibung(boolean)
   */
  public void setSonderabschreibung(boolean b) throws RemoteException
  {
    setAttribute("sonderabschreibung",b ? new Integer(1) : null);
  }
}


/*********************************************************************
 * $Log: AbschreibungImpl.java,v $
 * Revision 1.8  2006/01/08 15:28:41  willuhn
 * @N Loeschen von Sonderabschreibungen
 *
 * Revision 1.7  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.6  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/27 17:41:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.2  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/