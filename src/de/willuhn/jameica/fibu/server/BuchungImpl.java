/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.51 $
 * $Date: 2006/10/23 22:33:20 $
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
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.BuchungsEngine;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.system.Application;
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
   * @see de.willuhn.jameica.fibu.rmi.Buchung#getBruttoBetrag()
   */
  public double getBruttoBetrag() throws RemoteException
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

      BuchungsEngine engine = (BuchungsEngine) Application.getServiceFactory().lookup(Fibu.class,"engine");
      HilfsBuchung[] hbs = engine.buche(this);

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
    catch (Throwable t)
    {
      transactionBegin();
      Logger.error("error while saving buchung",t);
      throw new RemoteException(i18n.tr("Fehler beim Speichern der Buchung"));
    }
  }

  /**
   * Ueberschrieben, um Hilfsbuchungen auszublenden.
   * @see de.willuhn.datasource.db.AbstractDBObject#getListQuery()
   */
  protected String getListQuery()
  {
    return "select * from " + getTableName() + " where buchung_id is NULL";
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
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("brutto".equals(arg0))
      return new Double(getBruttoBetrag());
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    try
    {
      transactionBegin();
      DBIterator i = getHilfsBuchungen();
      while (i.hasNext())
      {
        HilfsBuchung b = (HilfsBuchung) i.next();
        b.delete();
      }
      
      // Falls mit der Buchung ein Anlagegenstand erzeugt wurde, muessen
      // wir den Link loeschen.
      Anlagevermoegen av = getAnlagevermoegen();
      if (av != null)
      {
        av.setBuchung(null);
        av.store();
      }

      super.delete();
      transactionCommit();
    }
    catch (RemoteException e)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      throw e;
    }
    catch (ApplicationException ae)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      throw ae;
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try
    {
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
      Logger.error("error while checking buchung",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Buchung."),e);
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    this.insertCheck();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchung#getAnlagevermoegen()
   */
  public Anlagevermoegen getAnlagevermoegen() throws RemoteException
  {
    DBIterator i = Settings.getDBService().createList(Anlagevermoegen.class);
    i.addFilter("buchung_id = " + this.getID());
    if (i.hasNext())
      return (Anlagevermoegen) i.next();
    return null;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchung#getHibiscusUmsatzID()
   */
  public String getHibiscusUmsatzID() throws RemoteException
  {
    return(String) getAttribute("hb_umsatz_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchung#setHibiscusUmsatzID(java.lang.String)
   */
  public void setHibiscusUmsatzID(String id) throws RemoteException
  {
    setAttribute("hb_umsatz_id",id);
  }
}


/*********************************************************************
 * $Log: BuchungImpl.java,v $
 * Revision 1.51  2006/10/23 22:33:20  willuhn
 * @N Experimentell: Laden der Objekte direkt beim Erzeugen der Liste
 *
 * Revision 1.50  2006/10/09 23:48:41  willuhn
 * @B bug 140
 *
 * Revision 1.49  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.48  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.47  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 * Revision 1.46  2006/01/08 15:28:40  willuhn
 * @N Loeschen von Sonderabschreibungen
 *
 * Revision 1.45  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.44  2005/10/06 14:48:40  willuhn
 * @N Sonderregelung fuer Abschreibunsgbuchungen
 *
 * Revision 1.43  2005/09/25 22:05:09  willuhn
 * @B bug 121
 *
 * Revision 1.42  2005/09/24 13:00:13  willuhn
 * @B bugfixes according to bugzilla
 *
 * Revision 1.41  2005/09/05 15:00:43  willuhn
 * *** empty log message ***
 *
 * Revision 1.40  2005/09/05 13:47:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.39  2005/09/02 13:27:35  willuhn
 * @C transaction behavior
 *
 * Revision 1.38  2005/09/02 11:26:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.37  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.36  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.35  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
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