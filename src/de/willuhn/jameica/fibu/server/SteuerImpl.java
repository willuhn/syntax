/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/SteuerImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2003/12/15 19:08:04 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.*;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.server.AbstractDBObject;

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
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getField("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#getSatz()
   */
  public double getSatz() throws RemoteException
  {
    Double d = (Double) getField("satz");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setField("name", name);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#setSatz(double)
   */
  public void setSatz(double satz) throws RemoteException
  {
    setField("satz", new Double(satz));
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
          "Bitte ändern oder löschen zu Sie zunächst das Konto.");
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      throw new ApplicationException("Fehler beim Prüfen der Abhängigkeiten.");
    }
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    // hier gilt erst mal das gleiche wie beim Update-Check ;)
    updateCheck();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    try {

      if (getName() == null || "".equals(getName()))
        throw new ApplicationException("Bitte geben Sie eine Bezeichnung für den Steuersatz ein.");
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      throw new ApplicationException("Fehler bei der Prüfung der Pflichtfelder.",e);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#getSteuerKonto()
   */
  public SteuerKonto getSteuerKonto() throws RemoteException
  {
    return (SteuerKonto) getField("steuerkonto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Steuer#setSteuerKonto(de.willuhn.jameica.fibu.objects.SteuerKonto)
   */
  public void setSteuerKonto(SteuerKonto k) throws RemoteException
  {
    if (k == null) return;
    setField("steuerkonto_id",new Integer(k.getID()));
  }
}

/*********************************************************************
 * $Log: SteuerImpl.java,v $
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