/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Kontoart.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/05 17:11:58 $
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

import de.willuhn.jameica.rmi.DBObject;

/**
 * Diese Klasse bildet die verschiedenen Konto-Arten in Fibu ab.
 * @author willuhn
 */
public interface Kontoart extends DBObject
{

  public final static int KONTOART_EINNAHME   = 1; // E
  public final static int KONTOART_AUSGABE    = 2; // A
  public final static int KONTOART_GELD       = 3; // G
  public final static int KONTOART_ANLAGE     = 4; // V
  public final static int KONTOART_PRIVAT     = 5; // P // bedeutet, dass dessen Bestand zum Jahreswechsel nicht uebernommen wird

  /**
   * Liefert die Bezeichnung der Kontoart.
   * @return Name der Kontoart.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
}

/*********************************************************************
 * $Log: Kontoart.java,v $
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/