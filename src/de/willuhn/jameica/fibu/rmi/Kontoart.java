/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Kontoart.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/12/11 21:00:35 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse bildet die verschiedenen Konto-Arten in Fibu ab.
 * @author willuhn
 */
public interface Kontoart extends DBObject
{

  public final static int KONTOART_UNGUELTIG  = 0;
  public final static int KONTOART_EINNAHME   = 1; // E
  public final static int KONTOART_AUSGABE    = 2; // A
  public final static int KONTOART_GELD       = 3; // G
  public final static int KONTOART_ANLAGE     = 4; // V
  public final static int KONTOART_PRIVAT     = 5; // P // bedeutet, dass dessen Bestand zum Jahreswechsel nicht uebernommen wird
  public final static int KONTOART_STEUER     = 6; // S

  /**
   * Liefert die Bezeichnung der Kontoart.
   * @return Name der Kontoart.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;

  /**
   * Liefert einen Int-Wert der die Art des Kontos definiert.
   * @return Kontoart.
   * @throws RemoteException
   */
  public int getKontoArt() throws RemoteException;
}

/*********************************************************************
 * $Log: Kontoart.java,v $
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/