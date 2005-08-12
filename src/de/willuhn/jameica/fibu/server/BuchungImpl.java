/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.29 $
 * $Date: 2005/08/12 00:10:59 $
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
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Generische Buchung.
 */
public class BuchungImpl extends AbstractBaseBuchungImpl implements Buchung
{

  /**
   * @throws RemoteException
   */
  public BuchungImpl() throws RemoteException
  {
    super();
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    double betrag = super.getBetrag();
    // jetzt muessen wir aber noch die Betraege der Hilfs-Buchungen drauf rechnen
    DBIterator hbs = getHilfsBuchungen();
    while (hbs.hasNext())
    {
      HilfsBuchung hb = (HilfsBuchung) hbs.next();
      betrag += hb.getBetrag();
    }
    return betrag;
  }

  /**
   * Diese Methode haben wir hier deshalb ueberschrieben, weil nicht nur
   * das Objekt speichern wollen sondern auch noch damit zusammenhaengende
   * Hilfe-Buchungen (z.Bsp. fuer die Steuern).
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    HilfsBuchung[] hbs = BuchungsEngine.buche(this);
    
    if (hbs == null)
    {
      // keine Hilfs-Buchungen noetig.
      super.store();
      return;
    }

    try {
      // Hilfs-Buchungen noetig.
      transactionBegin();

      super.store();

      for (int i=0;i<hbs.length;++i)
      {
        hbs[i].setHauptBuchung(this); // das koennen wir erst nach dem Speichern der Hauptbuchung machen.
        hbs[i].store();
      }

      transactionCommit();
    }
    catch (RemoteException e)
    {
      transactionRollback();
      throw e;
    }
    catch (ApplicationException ae)
    {
      transactionRollback();
      throw ae;
    }
  }

  /**
   * Ueberschrieben von AbstractDBObject weil wir nur die Buchungen:
   *  - vom aktiven Mandanten
   *  - aus dem aktuellen Geschaeftsjahr haben wollen.
   * @see de.willuhn.datasource.db.AbstractDBObject#getListQuery()
   */
  protected String getListQuery()
  {
    try
    {
      Mandant m = Settings.getActiveMandant();

      Date start = m.getGeschaeftsjahrVon();
      Date end   = m.getGeschaeftsjahrBis();
      
      String s = "select " + getIDField() + " from " + getTableName() +
        " where datum >= DATE '" + DF.format(start) + "'" +
        " and datum <= DATE '" + DF.format(end) + "'" + // nur aktuelles Geschaeftsjahr
        " and mandant_id = " + m.getID() +  // nur aktueller Mandant
        " and buchung_id is NULL";          // keine Hilfs-Buchungen
      return s;
    }
    catch (RemoteException e)
    {
      Logger.error("unable to create list query",e);
      return null;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchung#getHilfsBuchungen()
   */
  public DBIterator getHilfsBuchungen() throws RemoteException
  {
    DBIterator i = Settings.getDBService().createList(HilfsBuchung.class);
    i.addFilter("buchung_id = " + this.getID());
    return i;
  }


}


/*********************************************************************
 * $Log: BuchungImpl.java,v $
 * Revision 1.29  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 **********************************************************************/