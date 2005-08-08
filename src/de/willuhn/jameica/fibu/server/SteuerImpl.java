/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/SteuerImpl.java,v $
 * $Revision: 1.8 $
 * $Date: 2005/08/08 21:35:46 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.SteuerKonto;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class SteuerImpl extends AbstractDBObject implements Steuer
{

  /**
   * Erzeugt einen neuen Steuersatz.
   * @throws RemoteException
   */
  public SteuerImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
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
   * @see de.willuhn.jameica.fibu.objects.Steuer#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#getSatz()
   */
  public double getSatz() throws RemoteException
  {
    Double d = (Double) getAttribute("satz");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name", name);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#setSatz(double)
   */
  public void setSatz(double satz) throws RemoteException
  {
    setAttribute("satz", new Double(satz));
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("steuerkonto_id".equals(field))
      return SteuerKonto.class;
    return null;
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    // wir checken ob vielleicht ein Konto diesen Steuersatz besitzt.
    try {
      DBIterator list = Settings.getDatabase().createList(Konto.class);
      list.addFilter("steuer_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException("Der Steuersatz ist einem Konto zugewiesen.\n" +
          "Bitte �ndern oder l�schen zu Sie zun�chst das Konto.");
    }
    catch (RemoteException e)
    {
			Logger.error("error while checking dependencies",e);
      throw new ApplicationException("Fehler beim Pr�fen der Abh�ngigkeiten.");
    }
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {

      if (getName() == null || "".equals(getName()))
        throw new ApplicationException("Bitte geben Sie eine Bezeichnung f�r den Steuersatz ein.");
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking dependencies",e);
      throw new ApplicationException("Fehler bei der Pr�fung der Pflichtfelder.",e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    insertCheck();
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#getSteuerKonto()
   */
  public SteuerKonto getSteuerKonto() throws RemoteException
  {
    return (SteuerKonto) getAttribute("steuerkonto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#setSteuerKonto(de.willuhn.jameica.fibu.objects.SteuerKonto)
   */
  public void setSteuerKonto(SteuerKonto k) throws RemoteException
  {
    setAttribute("steuerkonto_id",k);
  }
}

/*********************************************************************
 * $Log: SteuerImpl.java,v $
 * Revision 1.8  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.7  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/