/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/HilfsBuchungImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2004/01/29 01:05:09 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.util.ApplicationException;

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
			Application.getLog().error("error while reading hilfsbuchung",e);
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

    String s = "select " + getIDField() + " from " + getTableName() +
			" where datum >= DATE '" + year + "-01-01'" +
			" and datum <= DATE '" + year + "-12-31'" + // nur aktuelles Geschaeftsjahr
      " and mandant_id = " + m.getID() +
      " and buchung_id is not NULL";
    return s;
  }

}

/*********************************************************************
 * $Log: HilfsBuchungImpl.java,v $
 * Revision 1.5  2004/01/29 01:05:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.2  2003/12/19 19:45:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/