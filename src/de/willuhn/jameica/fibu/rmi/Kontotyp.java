/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/Kontotyp.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/09/02 17:35:07 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBObject;

/**
 * Unterscheidet Konten zwischen Einnahmen und Ausgaben.
 * Das Objekt existiert eigentlich nur fuer die Konten,
 * bei denen anhand der Konto-Art noch nicht erkennbar
 * ist, ob es sich um ein Einahme- oder Ausgabe-Konto
 * handelt. Daher besitzen nur wenige Konten zusaetzlich
 * zur Konto-Art noch den Konto-Typ.
 * Konkret sind das derzeit nur die Steuerkonten. Die haben
 * als Konto-Art naemlich alle das Flag "Steuerkonto". Da
 * man daran allein aber noch nicht erkennen kann, wie
 * es sich auf der Ueberschuss-Rechnung auswirkt, besitzen
 * Sie ausserdem noch den Konto-Typ.
 * @author willuhn
 */
public interface Kontotyp extends DBObject
{
  /**
   * Konto-Typ ungueltig.
   */
  public final static int KONTOTYP_UNGUELTIG  = 0;
  
  /**
   * Konto-Typ Einnahme.
   */
  public final static int KONTOTYP_EINNAHME   = 1;
  
  /**
   * Konto-Typ Ausgabe.
   */
  public final static int KONTOTYP_AUSGABE    = 2;

  /**
   * Liefert einen sprechenden Namen fuer den Konto-Typ.
   * @return Name.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;

  /**
   * Liefert einen Int-Wert der den Typ des Kontos definiert.
   * @return Kontotyp.
   * @throws RemoteException
   */
  public int getKontoTyp() throws RemoteException;
}


/*********************************************************************
 * $Log: Kontotyp.java,v $
 * Revision 1.1  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 *********************************************************************/