/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/SaldenCache.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/15 23:38:27 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import de.willuhn.util.Session;

/**
 * Cache fuer die Salden.
 */
class SaldenCache
{
  private static Session session = new Session();
  
  /**
   * Liefert den gecachten Saldo oder null.
   * @param kontonummer Kontonummer.
   * @return Saldo.
   */
  protected static Double get(String kontonummer)
  {
    return (Double) session.get(kontonummer);
  }

  /**
   * Entfernt den Saldo aus dem Cache.
   * @param kontonummer
   */
  protected static void remove(String kontonummer)
  {
    session.remove(kontonummer);
  }
  
  /**
   * Speichert den Saldo.
   * @param kontonummer
   * @param d
   */
  protected static void put(String kontonummer, Double d)
  {
    session.put(kontonummer,d);
  }

}


/*********************************************************************
 * $Log: SaldenCache.java,v $
 * Revision 1.1  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 **********************************************************************/