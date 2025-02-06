/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;

import de.willuhn.jameica.fibu.Settings;
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
    if (Settings.inUpdate())
      return;

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
