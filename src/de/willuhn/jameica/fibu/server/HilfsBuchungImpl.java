/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/HilfsBuchungImpl.java,v $
 * $Revision: 1.16 $
 * $Date: 2011/05/12 09:10:32 $
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

import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class HilfsBuchungImpl extends AbstractBaseBuchungImpl implements HilfsBuchung
{

  /**
   * @throws RemoteException
   */
  public HilfsBuchungImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.HilfsBuchung#getHauptBuchung()
   */
  public Buchung getHauptBuchung() throws RemoteException
  {
    return (Buchung) getAttribute("buchung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.HilfsBuchung#setHauptBuchung(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public void setHauptBuchung(Buchung buchung) throws RemoteException
  {
    setAttribute("buchung_id",buchung);
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractBaseBuchungImpl#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    if ("buchung_id".equals(field))
      return Buchung.class;
    return super.getForeignObject(field);
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractBaseBuchungImpl#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try {

      if (getHauptBuchung() == null)
        throw new ApplicationException("Keine Haupt-Buchung zugewiesen.");

      // BUGZILLA 131
      Kontoart kaSoll  = getSollKonto().getKontoArt();
      Kontoart kaHaben = getHabenKonto().getKontoArt();
      boolean gpSoll   = kaSoll.getKontoArt() == Kontoart.KONTOART_GELD || kaSoll.getKontoArt() == Kontoart.KONTOART_PRIVAT;
      boolean gpHaben  = kaHaben.getKontoArt() == Kontoart.KONTOART_GELD || kaHaben.getKontoArt() == Kontoart.KONTOART_PRIVAT;
      boolean isAbschreibung = kaSoll.getKontoArt() == Kontoart.KONTOART_AUFWAND && kaHaben.getKontoArt() == Kontoart.KONTOART_ANLAGE;
      
      if (!gpSoll && !gpHaben && !isAbschreibung)
        throw new ApplicationException(i18n.tr("Mindestens eines der beiden Konten muss ein Geld- oder Privat-Konto sein"));
    }
    catch (RemoteException e)
    {
			Logger.error("error while reading hilfsbuchung",e);
      throw new ApplicationException("Fehler bei der Prüfung der Hilfs-Buchung.",e);
    }
  }
  
  /**
   * @see de.willuhn.jameica.fibu.server.AbstractBaseBuchungImpl#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    this.insertCheck();
  }

  /**
   * Ueberschrieben, um nur Hilfsbuchungen zu laden.
   * @see de.willuhn.datasource.db.AbstractDBObject#getListQuery()
   */
  protected String getListQuery()
  {
    return "select * from " + getTableName() + " where buchung_id is NOT NULL";
  }

}

/*********************************************************************
 * $Log: HilfsBuchungImpl.java,v $
 * Revision 1.16  2011/05/12 09:10:32  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.15  2006-10-23 22:33:20  willuhn
 * @N Experimentell: Laden der Objekte direkt beim Erzeugen der Liste
 *
 * Revision 1.14  2005/10/06 14:48:40  willuhn
 * @N Sonderregelung fuer Abschreibunsgbuchungen
 *
 * Revision 1.13  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.12  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.8  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.7  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.6  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.5  2004/01/29 01:05:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.2  2003/12/19 19:45:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 **********************************************************************/