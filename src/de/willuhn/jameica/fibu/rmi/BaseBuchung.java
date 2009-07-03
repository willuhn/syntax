/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/BaseBuchung.java,v $
 * $Revision: 1.9 $
 * $Date: 2009/07/03 10:52:19 $
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
import java.util.Date;

/**
 * Diese Klasse bildet die Buchungen in Fibu ab.
 * @author willuhn
 */
public interface BaseBuchung extends Transfer
{

  /**
   * Liefert das Datum der Buchung.
   * Wenn es eine neue Buchung ist, wird das aktuelle Datum geliefert.
   * @return Datum der Buchung.
   * @throws RemoteException
   */
  public Date getDatum() throws RemoteException;

  /**
   * Liefert das Geschaeftsjahr zu dieser Buchung.
   * @return Geschaeftsjahr der Buchung.
   * @throws RemoteException
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException;

  /**
   * Liefert die Belegnummer oder erzeugt eine neue, wenn sie null ist.
   * @return Belegnummer.
   * @throws RemoteException
   */
  public int getBelegnummer() throws RemoteException;

  /**
   * Setzt das Datum der Buchung.
   * @param d Datum.
   * @throws RemoteException
   */
  public void setDatum(Date d) throws RemoteException;

  /**
   * Setzt die Belegnummer.
   * @param belegnummer Belegnummer der Buchung.
   * @throws RemoteException
   */
  public void setBelegnummer(int belegnummer) throws RemoteException;

  /**
   * Setzt das Geschaeftsjahr der Buchung.
   * @param jahr Geschaeftsjahr der Buchung.
   * @throws RemoteException
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException;

}

/*********************************************************************
 * $Log: BaseBuchung.java,v $
 * Revision 1.9  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.7  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.6  2005/09/05 13:47:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/29 12:17:28  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.4  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.3  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.2  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.13  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.12  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2003/12/16 02:27:32  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.10  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.8  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.6  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.3  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:10:56  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/