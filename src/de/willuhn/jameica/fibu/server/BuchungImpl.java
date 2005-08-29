/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.34 $
 * $Date: 2005/08/29 12:17:29 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
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
    
    try {
      transactionBegin();

      HilfsBuchung[] hbs = BuchungsEngine.buche(this);

      super.store();

      if (hbs != null)
      {
        for (int i=0;i<hbs.length;++i)
        {
          hbs[i].setHauptBuchung(this); // das koennen wir erst nach dem Speichern der Hauptbuchung machen.
          hbs[i].store();
        }
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
   * Ueberschrieben, um Hilfsbuchungen auszublenden.
   * @see de.willuhn.datasource.db.AbstractDBObject#getListQuery()
   */
  protected String getListQuery()
  {
    return "select " + getIDField() + " from " + getTableName() + " where buchung_id is NULL";
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

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try
    {
      // Checken, ob in die Belegnummer eindeutig ist
      DBIterator list = getService().createList(Buchung.class);
      list.addFilter("belegnummer = " + getAttribute("belegnummer"));
      if (!this.isNewObject()) // wenn das Objekt existiert, klammern wir es aus
        list.addFilter("id != " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Im aktuellen Geschäftsjahr existiert bereits eine Buchung mit dieser Nummer"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking buchung",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Buchung."),e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("brutto".equals(arg0))
      return new Double(this.getBetrag());
    return super.getAttribute(arg0);
  }
}


/*********************************************************************
 * $Log: BuchungImpl.java,v $
 * Revision 1.34  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.33  2005/08/24 23:02:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.32  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.31  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.30  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.29  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 **********************************************************************/