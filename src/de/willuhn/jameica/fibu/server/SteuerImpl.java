/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/SteuerImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/12/10 23:51:52 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.objects;

import java.rmi.RemoteException;
import java.sql.Connection;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.rmi.AbstractDBObject;
import de.willuhn.jameica.rmi.DBIterator;

/**
 * @author willuhn
 */
public class SteuerImpl extends AbstractDBObject implements Steuer
{

  /**
   * Laedt den Steuersatz oder erzeugt einen neuen.
   * @param conn Die Connection - reichen wir einfach durch ;).
   * @param id die optional zu ladende Buchung oder null.
   * @throws RemoteException
   */
  public SteuerImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
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
      DBIterator list = Application.getDefaultDatabase().createList(Konto.class);
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
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/