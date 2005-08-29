/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbschreibungImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/29 17:46:14 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung einer einzelnen Abschreibung.
 */
public class AbschreibungImpl extends AbstractDBObject implements Abschreibung
{

  private I18N i18n = null;
  
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
  public Buchung getBuchung() throws RemoteException
  {
    return (Buchung) getAttribute("buchung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#setAnlagevermoegen(de.willuhn.jameica.fibu.rmi.Anlagevermoegen)
   */
  public void setAnlagevermoegen(Anlagevermoegen av) throws RemoteException
  {
    setAttribute("av_id",av);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Abschreibung#setBuchung(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public void setBuchung(Buchung b) throws RemoteException
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
      return Buchung.class;
    
    return super.getForeignObject(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      if (Settings.getActiveGeschaeftsjahr().isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

      if (getAnlagevermoegen() == null)
        throw new ApplicationException(i18n.tr("Kein Anlage-Gegenstand zugeordnet"));
      if (getBuchung() == null)
        throw new ApplicationException(i18n.tr("Keine Buchung zugeordnet"));
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
}


/*********************************************************************
 * $Log: AbschreibungImpl.java,v $
 * Revision 1.2  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.1  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 **********************************************************************/