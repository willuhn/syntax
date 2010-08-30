/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbschreibungsBuchungImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2010/08/30 16:41:01 $
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

import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class AbschreibungsBuchungImpl extends AbstractBaseBuchungImpl implements AbschreibungsBuchung
{

  /**
   * @throws RemoteException
   */
  public AbschreibungsBuchungImpl() throws RemoteException {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try
    {
      Kontoart kaSoll  = getSollKonto().getKontoArt();
      Kontoart kaHaben = getHabenKonto().getKontoArt();
      boolean isAbschreibung = kaSoll.getKontoArt() == Kontoart.KONTOART_AUFWAND && kaHaben.getKontoArt() == Kontoart.KONTOART_ANLAGE;
      
      if (!isAbschreibung)
        throw new ApplicationException(i18n.tr("Abschreibungsbuchungen müssen dem Schema \"Aufwand an Bestand\" erfolgen."));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking buchung",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Abschreibungs-Buchung."),e);
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    this.insertCheck();
  }
  
}


/*********************************************************************
 * $Log: AbschreibungsBuchungImpl.java,v $
 * Revision 1.4  2010/08/30 16:41:01  willuhn
 * @N Klartextbezeichnung bei Import/Export
 *
 * Revision 1.3  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.2  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/10/06 14:48:40  willuhn
 * @N Sonderregelung fuer Abschreibunsgbuchungen
 *
 *********************************************************************/