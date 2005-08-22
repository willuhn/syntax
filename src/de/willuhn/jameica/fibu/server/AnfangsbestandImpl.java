/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AnfangsbestandImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/22 16:37:22 $
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
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung des Anfangsbestandes eines Kontos.
 */
public class AnfangsbestandImpl extends AbstractDBObject implements
    Anfangsbestand
{

  /**
   * @throws java.rmi.RemoteException
   */
  public AnfangsbestandImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "konto_ab";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      Konto k = getKonto();
      Mandant m = getMandant();
      
      if (m == null || m.isNewObject())
        throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Mandanten aus"));
      
      if (k == null || k.isNewObject())
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Konto aus"));
        
      // HIER weiter

      DBIterator list = getService().createList(Anfangsbestand.class);
      list.addFilter("konto_id = " + k.getID());
      list.addFilter("mandant_id = " + m.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Für das Konto und Geschäftsjahr existiert bereits ein Anfangsbestand"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking anfangsbestand",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Anfangsbestandes"));
    }
    super.insertCheck();
  }
  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "konto_id";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    setAttribute("mandant_id",m);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getAttribute("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setKonto(Konto k) throws RemoteException
  {
    setAttribute("konto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    Double d = (Double) getAttribute("betrag");
    return d == null ? 0.0d : d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setAttribute("betrag",new Double(betrag));
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String arg0) throws RemoteException
  {
    if ("mandant_id".equals(arg0))
      return Mandant.class;
    if ("konto_id".equals(arg0))
      return Konto.class;
    return super.getForeignObject(arg0);
  }
}


/*********************************************************************
 * $Log: AnfangsbestandImpl.java,v $
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/