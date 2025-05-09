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
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.Transfer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public abstract class AbstractTransferImpl extends AbstractDBObject implements Transfer
{
  final static transient I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * Erzeugt einen neuen Transfer.
   * @throws RemoteException
   */
  public AbstractTransferImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getSollKonto()
   */
  public Konto getSollKonto() throws RemoteException
  {
    Integer i = (Integer) super.getAttribute("sollkonto_id");
    if (i == null)
      return null;
   
    Cache cache = Cache.get(Konto.class,true);
    return (Konto) cache.get(i);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getHabenKonto()
   */
  public Konto getHabenKonto() throws RemoteException
  {
    Integer i = (Integer) super.getAttribute("habenkonto_id");
    if (i == null)
      return null;
   
    Cache cache = Cache.get(Konto.class,true);
    return (Konto) cache.get(i);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getText()
   */
  public String getText() throws RemoteException
  {
    return (String) getAttribute("buchungstext");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    Number d = (Number) getAttribute("betrag");
    if (d == null)
      return 0;

    return d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getSteuer()
   */
  public double getSteuer() throws RemoteException
  {
    Number d = (Number) getAttribute("steuer");
    if (d != null)
      return d.doubleValue();

    return 0;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getSteuerObject()
   */
  public Steuer getSteuerObject() throws RemoteException
  {
  	// wenn eine steuer_id angegeben ist, diese verwenden
  	final Object o = super.getAttribute("steuer_id");
  	if (o != null)
  	{
  		Cache cache = Cache.get(Steuer.class,true);
  		return (Steuer) cache.get(o);
  	}

  	// Wenn in der Buchung kein Steuersatz direkt angegeben ist, dann verwenden wir die alte
  	// Logik - n�mlich den Steueuersatz von dem Soll-/oder Habenkonto verwenden, bei dem eines angegeben ist
    final Konto sk = this.getSollKonto();
    final Konto hk = this.getHabenKonto();

  	Steuer ss = sk == null ? null : sk.getSteuer();
    Steuer hs = hk == null ? null : hk.getSteuer();
    return (ss != null ? ss : hs);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setSollKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setSollKonto(Konto k) throws RemoteException
  {
    setAttribute("sollkonto_id",k == null || k.getID() == null ? null : Integer.valueOf(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setHabenKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setHabenKonto(Konto k) throws RemoteException
  {
    setAttribute("habenkonto_id",k == null || k.getID() == null ? null : Integer.valueOf(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setText(java.lang.String)
   */
  public void setText(String text) throws RemoteException
  {
    setAttribute("buchungstext", text);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setAttribute("betrag", Double.valueOf(betrag));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setSteuer(double)
   */
  public void setSteuer(double steuer) throws RemoteException
  {
    setAttribute("steuer", Double.valueOf(steuer));
  }


  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setSteuerObject(Steuer)
   */
  public void setSteuerObject(Steuer steuer) throws RemoteException
  {
    setAttribute("steuer_id",steuer == null || steuer.getID() == null ? null : Integer.valueOf(steuer.getID()));
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("habenKonto".equals(arg0))
      return this.getHabenKonto();
    
    if ("sollKonto".equals(arg0))
      return this.getSollKonto();
    
    return super.getAttribute(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    
    // Die NULL-Checks sind primaer nur fuer Buchungsvorlagen - dort duerfen sie NULL sein.
    Konto soll = getSollKonto();
    Konto haben = getHabenKonto();
    if (soll != null && haben != null)
      Logger.info(soll.getKontonummer() + " an " + haben.getKontonummer() + ": " + Settings.DECIMALFORMAT.format(getBetrag()) + " (" + getText() + ")");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#isGeprueft()
   */
  public boolean isGeprueft() throws RemoteException
  {
    Integer i = (Integer) getAttribute("geprueft");
    return i != null && i.intValue() == 1;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setGeprueft(boolean)
   */
  public void setGeprueft(boolean b) throws RemoteException
  {
    setAttribute("geprueft",Integer.valueOf(b ? 1 : 0));
  }


}
