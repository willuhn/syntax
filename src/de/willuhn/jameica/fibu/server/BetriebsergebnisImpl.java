/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BetriebsergebnisImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2006/05/08 22:44:18 $
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
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.ArrayList;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.Betriebsergebnis;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;

/**
 * Implementierung des Betriebsergebnisses des aktuellen Geschaeftsjahres.
 * @author willuhn
 */
public class BetriebsergebnisImpl extends UnicastRemoteObject implements Betriebsergebnis
{

  private final Geschaeftsjahr jahr;
  private final Date von;
  private final Date bis;

  public BetriebsergebnisImpl(Geschaeftsjahr jahr, Date von, Date bis)
      throws RemoteException
  {
    super();
    this.jahr = jahr;
    this.von = von;
    this.bis = bis;
  }

  /**
   * ct.
   * @param jahr Geschaeftsjahr.
   * @throws RemoteException
   */
  public BetriebsergebnisImpl(Geschaeftsjahr jahr) throws RemoteException
  {
    this(jahr, null, null);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Betriebsergebnis#getEinnahmen()
   */
  public Konto[] getEinnahmen() throws RemoteException
  {
    // Liste der Konten mit Einnahmen ermitteln
    ArrayList list = new ArrayList();
    DBIterator i = jahr.getKontenrahmen().getKonten();
    i.addFilter("kontoart_id = " + Kontoart.KONTOART_ERLOES + " OR (kontoart_id = " + Kontoart.KONTOART_STEUER + " AND kontotyp_id = " + Kontotyp.KONTOTYP_EINNAHME + ")");
    
    while (i.hasNext())
    {
      Konto k = (Konto) i.next();
      if (k.getUmsatz(jahr, von, bis) == 0.0d)
        continue; // hier gibts nichts anzuzeigen
      list.add(k);
    }
    return (Konto[]) list.toArray(new Konto[list.size()]);
  }

  public double getEinnahmenWert() throws RemoteException
  {
    return getWertAusKonten(getEinnahmen());
  }

  private double getWertAusKonten(Konto[] konten) throws RemoteException
  {
    double wert = 0;
    for (Konto konto : konten)
    {
      wert += konto.getUmsatz(jahr, von, bis);
    }
    return wert;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Betriebsergebnis#getAusgaben()
   */
  public Konto[] getAusgaben() throws RemoteException
  {
    ArrayList list = new ArrayList();
    DBIterator i = jahr.getKontenrahmen().getKonten();
    i.addFilter("kontoart_id = " + Kontoart.KONTOART_AUFWAND + " OR (kontoart_id = " + Kontoart.KONTOART_STEUER + " AND kontotyp_id = " + Kontotyp.KONTOTYP_AUSGABE + ")");
    while (i.hasNext())
    {
      Konto k = (Konto) i.next();
      if (k.getUmsatz(jahr, von, bis) == 0.0d)
        continue; // hier gibts nichts anzuzeigen
      list.add(k);
    }
    
    return (Konto[]) list.toArray(new Konto[list.size()]);
  }

  public double getAusgabenWert() throws RemoteException
  {
    return getWertAusKonten(getAusgaben());
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Betriebsergebnis#getBetriebsergebnis()
   */
  public double getBetriebsergebnis() throws RemoteException
  {
    double ergebnis = 0.0d;
    Konto[] einnamen = getEinnahmen();
    for (int i=0;i<einnamen.length;++i)
    {
      ergebnis += einnamen[i].getUmsatz(jahr, von, bis);
    }
    Konto[] ausgaben = getAusgaben();
    for (int i=0;i<ausgaben.length;++i)
    {
      ergebnis -= ausgaben[i].getUmsatz(jahr, von, bis);
    }
    return ergebnis;
  }

}


/*********************************************************************
 * $Log: BetriebsergebnisImpl.java,v $
 * Revision 1.5  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.4  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.3  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/09/04 23:10:14  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 *********************************************************************/