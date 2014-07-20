/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Betriebsergebnis.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/05/08 22:44:18 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Liefert Informationen zum Betriebsergebnis des aktuellen Geschaeftsjahres.
 * @author willuhn
 */
public interface Betriebsergebnis extends Remote
{
  /**
   * Liefert die bereinigte Liste der Konten mit Einnahmen im Geschaeftsjahr.
   * Bereinigt, weil die Liste nur Konten enthaelt, auf denen tatsaechlich
   * Umsaetze gebucht wurden.
   * @return Liste der Einnahmen.
   * @throws RemoteException
   */
  public Konto[] getEinnahmen() throws RemoteException;

  /**
   * @return die Summe aller Buchungen auf Konten mit Einnahmen im
   *         Geschäftsjahr.
   * @throws RemoteException
   */
  public double getEinnahmenWert() throws RemoteException;

  /**
   * Liefert die bereinigte Liste der Konten mit Ausgaben im Geschaeftsjahr.
   * Bereinigt, weil die Liste nur Konten enthaelt, auf denen tatsaechlich
   * Umsaetze gebucht wurden.
   * @return Liste der Ausgaben.
   * @throws RemoteException
   */
  public Konto[] getAusgaben() throws RemoteException;

  /**
   * @return die Summe aller Buchungen auf Konten mit Ausgaben im Geschäftsjahr.
   * @throws RemoteException
   */
  public double getAusgabenWert() throws RemoteException;

  /**
   * Liefert das Betriebsergebnis.
   * Bei Gewinn ein positiver Wert, bei Verlust ein negativer.
   * @return das Betriebsergebnis.
   * @throws RemoteException
   */
  public double getBetriebsergebnis() throws RemoteException;

}


/*********************************************************************
 * $Log: Betriebsergebnis.java,v $
 * Revision 1.2  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.1  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 *********************************************************************/