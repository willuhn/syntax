/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

/**
 * Diese Klasse bildet die verschiedenen Steuersaetze in Fibu ab.
 * Es werden zwei verschiedene Arten von Steuersaetzen unterschieden.
 * Initiale Steuersaetze gelten fuer alle Mandanten und koennen nicht
 * geaendert werden. Zusaetzlich existieren noch benutzerspezifische
 * Steuersaetze, die jeweils nur fuer den Mandanten gelten, der sie
 * angelegt hat. Wird eine Liste von Steuersaetzen geladen, so werden
 * immer auch nur die initialen sowie die des aktuellen Mandanten geladen.
 * @author willuhn
 */
public interface Steuer extends UserObject
{

  /**
   * Liefert die Bezeichnung des Steuersatzes.
   * @return Name des Steuersatzes.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;

  /**
   * Liefert den Steuersatz dieser Steuer.
   * @return Steuersatz dieser Steuer.
   * @throws RemoteException
   */
  public double getSatz() throws RemoteException;

  /**
   * Liefert das Steuersammelkonto, welches fuer Konten verwendet wird, die
   * diese Steuer als Vorschlag registriert haben.
   * @return das Steuersammel-Konto
   * @throws RemoteException
   */
  public Konto getSteuerKonto() throws RemoteException;
  
  /**
   * Setzt den Namen des Steuersatzes.
   * @param name Name des Steuersatzes.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;

  /**
   * Setzt den Steuersatz dieser Steuer.
   * @param satz Steuersatz.
   * @throws RemoteException
   */
  public void setSatz(double satz) throws RemoteException;

  /**
   * Setzt das Steuer-Sammelkonto.
   * @param k das zu verwendende Steuersammel-Konto.
   * @throws RemoteException
   */
  public void setSteuerKonto(Konto k) throws RemoteException;
  
  /**
   * Liefert das Kennzeichen fuer die Steuer im UST-Voranmelde-Formular.
   * @return das Kennzeichen fuer die Steuer im UST-Voranmelde-Formular.
   * @throws RemoteException
   */
  public String getUstNrSteuer() throws RemoteException;
  
  /**
   * Speichert das Kennzeichen fuer die Steuer im UST-Voranmelde-Formular.
   * @param s das Kennzeichen fuer die Steuer im UST-Voranmelde-Formular.
   * @throws RemoteException
   */
  public void setUstNrSteuer(String s) throws RemoteException;
  
  /**
   * Liefert das Kennzeichen fuer die Bemessungsgrundlage im UST-Voranmelde-Formular.
   * @return das Kennzeichen fuer die Bemessungsgrundlage im UST-Voranmelde-Formular.
   * @throws RemoteException
   */
  public String getUstNrBemessung() throws RemoteException;
  
  /**
   * Speichert das Kennzeichen fuer die Bemessungsgrundlage im UST-Voranmelde-Formular.
   * @param s das Kennzeichen fuer die Bemessungsgrundlage im UST-Voranmelde-Formular.
   * @throws RemoteException
   */
  public void setUstNrBemessung(String s) throws RemoteException;

}

/*********************************************************************
 * $Log: Steuer.java,v $
 * Revision 1.12  2010/06/04 13:49:48  willuhn
 * @N Kennzeichen fuer Steuer und Bemessungsgrundlage fuer UST-Voranmeldung
 *
 * Revision 1.11  2010/06/04 13:34:45  willuhn
 * @B Da fehlten ein paar Commits
 *
 * Revision 1.10  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.8  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.7  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.6  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.5  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.4  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/