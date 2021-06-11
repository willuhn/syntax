/**********************************************************************
 *
 * Copyright (c) 2021 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
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
public class SaldenCache
{
  private static Session session = new Session();
  
  /**
   * Liefert den gecachten Saldo oder null.
   * @param jahr Geschaeftsjahr.
   * @param kontonummer Kontonummer.
   * @return Saldo.
   * @throws RemoteException
   */
  public static Double get(Geschaeftsjahr jahr, String kontonummer) throws RemoteException
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
  public static void remove(String kontonummer)
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
  public static void remove(Geschaeftsjahr jahr, String kontonummer) throws RemoteException
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
  public static void put(Geschaeftsjahr jahr, String kontonummer, Double d) throws RemoteException
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

