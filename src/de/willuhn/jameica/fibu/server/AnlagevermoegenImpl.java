/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AnlagevermoegenImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/29 00:20:29 $
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
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.logging.Logger;

/**
 * Implementierung eines Anlagevermoegen-Postens.
 */
public class AnlagevermoegenImpl extends AbstractDBObject implements Anlagevermoegen
{

  /**
   * @throws java.rmi.RemoteException
   */
  public AnlagevermoegenImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "anlagevermoegen";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAnschaffungskosten()
   */
  public double getAnschaffungskosten() throws RemoteException
  {
    Double d = (Double) getAttribute("anschaffungskosten");
    return d == null ? 0.0d : d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setAnschaffungskosten(double)
   */
  public void setAnschaffungskosten(double kosten) throws RemoteException
  {
    setAttribute("anschaffungskosten",new Double(kosten));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAnschaffungsDatum()
   */
  public Date getAnschaffungsDatum() throws RemoteException
  {
    return (Date) getAttribute("anschaffungsdatum");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setAnschaffungsDatum(java.util.Date)
   */
  public void setAnschaffungsDatum(Date d) throws RemoteException
  {
    setAttribute("anschaffungsdatum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getBuchung()
   */
  public Buchung getBuchung() throws RemoteException
  {
    return (Buchung) getAttribute("buchung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setBuchung(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public void setBuchung(Buchung buchung) throws RemoteException
  {
    setAttribute("buchung_id",buchung);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getLaufzeit()
   */
  public int getLaufzeit() throws RemoteException
  {
    Integer laufzeit = (Integer) getAttribute("laufzeit");
    return laufzeit == null ? 0 : laufzeit.intValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setLaufzeit(int)
   */
  public void setLaufzeit(int laufzeit) throws RemoteException
  {
    setAttribute("laufzeit",new Integer(laufzeit));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getRestwert()
   */
  public double getRestwert() throws RemoteException
  {
    Double restwert = (Double) getAttribute("restwert");
    return restwert == null ? 0.0d : restwert.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setRestwert(double)
   */
  public void setRestwert(double restwert) throws RemoteException
  {
    setAttribute("restwert",new Double(restwert));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant mandant) throws RemoteException
  {
    setAttribute("mandant_id",mandant);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String arg0) throws RemoteException
  {
    if ("mandant_id".equals(arg0))
      return Mandant.class;
    if ("buchung_id".equals(arg0))
      return Buchung.class;
    
    return super.getForeignObject(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getListQuery()
   */
  protected String getListQuery()
  {
    try
    {
      return "select " + getIDField() + " from " + getTableName() +
             " where mandant_id = " + Settings.getActiveMandant().getID();
    }
    catch (RemoteException e)
    {
      Logger.error("unable to load av by mandant",e);
      return super.getListQuery();
    }
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenImpl.java,v $
 * Revision 1.1  2005/08/29 00:20:29  willuhn
 * @N anlagevermoegen
 *
 **********************************************************************/