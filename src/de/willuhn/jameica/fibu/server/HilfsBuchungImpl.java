/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/HilfsBuchungImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/16 02:27:33 $
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
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;

/**
 * @author willuhn
 */
public class HilfsBuchungImpl extends BuchungImpl implements HilfsBuchung
{

  /**
   * @throws RemoteException
   */
  public HilfsBuchungImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.HilfsBuchung#getHauptBuchung()
   */
  public Buchung getHauptBuchung() throws RemoteException
  {
    return (Buchung) getField("buchung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.HilfsBuchung#setHauptBuchung(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public void setHauptBuchung(Buchung buchung) throws RemoteException
  {
    if (buchung == null) return;
    setField("buchung_id",new Integer(buchung.getID()));
  }

  /**
   * @see de.willuhn.jameica.server.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("buchung_id".equals(field))
      return Buchung.class;
    return super.getForeignObject(field);
  }

  /**
   * @see de.willuhn.jameica.server.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    try {
      if (getHauptBuchung() == null)
        throw new ApplicationException("Keine Haupt-Buchung zugewiesen.");
    }
    catch (RemoteException e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      throw new ApplicationException("Fehler bei der Prüfung der Hilfs-Buchung.",e);
    }
    super.updateCheck();
  }

  /**
   * Ueberschrieben von BuchungImpl weil die Funktion in BuchungImpl alle Buchungen
   * _ausser_ Hilfs-Buchungen findet. Wir wollen aber genau die ;).
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    Mandant m = Settings.getActiveMandant();

    if (m == null)
    {
      throw new RemoteException("no active mandant defined");
    }
    int year = m.getGeschaeftsjahr();

    String s = "select id from " + getTableName() +
      " where YEAR(datum) = " + year + 
      " and mandant_id = " + m.getID() +
      " and buchung_id is not NULL";
    return s;
  }

}

/*********************************************************************
 * $Log: HilfsBuchungImpl.java,v $
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/