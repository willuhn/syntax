/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/SaldenCache.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/10/03 14:22:11 $
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
import java.util.Enumeration;

import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.util.Session;

/**
 * Cache fuer die Salden.
 */
class SaldenCache
{
  private static Session session = new Session();
  
  /**
   * Liefert den gecachten Saldo oder null.
   * @param jahr Geschaeftsjahr.
   * @param kontonummer Kontonummer.
   * @return Saldo.
   * @throws RemoteException
   */
  protected static Double get(Geschaeftsjahr jahr, String kontonummer) throws RemoteException
  {
    if (jahr == null || kontonummer == null || kontonummer.length() == 0)
      return null;

    Session sj = (Session) session.get(jahr.getID());
    if (sj == null)
      return null;
    return (Double) sj.get(kontonummer);
  }

  /**
   * Entfernt den Saldo aus dem Cache.
   * @param kontonummer
   */
  protected static void remove(String kontonummer)
  {
    if (kontonummer == null || kontonummer.length() == 0)
      return;

    Enumeration e = session.keys();
    while (e.hasMoreElements())
    {
      Session sj = (Session) session.get(e.nextElement());
      sj.remove(kontonummer);
    }
  }
  
  /**
   * Entfernt den Saldo aus dem Cache.
   * @param jahr Geschaeftsjahr.
   * @param kontonummer
   * @throws RemoteException
   */
  protected static void remove(Geschaeftsjahr jahr, String kontonummer) throws RemoteException
  {
    if (jahr == null || kontonummer == null || kontonummer.length() == 0)
      return;

    Session sj = (Session) session.get(jahr.getID());
    sj.remove(kontonummer);
  }

  /**
   * Speichert den Saldo.
   * @param jahr Geschaeftsjahr.
   * @param kontonummer
   * @param d
   * @throws RemoteException
   */
  protected static void put(Geschaeftsjahr jahr, String kontonummer, Double d) throws RemoteException
  {
    if (jahr == null || kontonummer == null || kontonummer.length() == 0)
      return;
    
    Session sj = (Session) session.get(jahr.getID());
    if (sj == null)
    {
      sj = new Session();
      session.put(jahr.getID(),sj);
    }
    sj.put(kontonummer,d);
  }

}


/*********************************************************************
 * $Log: SaldenCache.java,v $
 * Revision 1.2  2005/10/03 14:22:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 **********************************************************************/