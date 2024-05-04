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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.BuchungsEngine;
import de.willuhn.jameica.fibu.rmi.CustomSerializer;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Generische Buchung.
 */
public class BuchungImpl extends AbstractBaseBuchungImpl implements Buchung, CustomSerializer
{
  private double brutto = Double.NaN;

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
    if (!Double.isNaN(this.brutto))
      return this.brutto;
    
    double betrag = super.getBetrag();
    //Die Betraege der Splitbuchungen hinzurechnen
    DBIterator split = getSplitBuchungen();
    if(split.hasNext())betrag = 0d;
    while (split.hasNext())
    {
      Buchung s = (Buchung) split.next();
      betrag += s.getBruttoBetrag();
    }
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
  * @see de.willuhn.jameica.fibu.server.AbstractBaseBuchungImpl#getForeignObject(java.lang.String)
  */
 protected Class getForeignObject(String field) throws RemoteException
 {
   if ("split_id".equals(field))
     return Buchung.class;
   return super.getForeignObject(field);
 }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchung#setBruttoBetrag(double)
   */
  public void setBruttoBetrag(double d) throws RemoteException
  {
    this.brutto = d;
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
      
      Buchung hauptbuchung = getSplitHauptBuchung();
      //Wenn vorhanden, die SplitHauptbuchung speichern um die Steuer/Hilfsbuchungen zu entfernen
      if(hauptbuchung != null && hauptbuchung.getSteuer() > 0.01d) {
          hauptbuchung.setBetrag(hauptbuchung.getBruttoBetrag());
          hauptbuchung.setSteuer(0);
          hauptbuchung.store();
      }
      
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
      
      // Forcieren, dass der Brutto-Betrag wieder aus den Hilfsbuchungen berechnet wird
      this.brutto = Double.NaN;
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
   * @see de.willuhn.jameica.fibu.rmi.Buchung#getSplitHauptBuchung()
   */
  public Buchung getSplitHauptBuchung() throws RemoteException
  {
    return (Buchung) getAttribute("split_id");
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
   * @see de.willuhn.jameica.fibu.rmi.Buchung#getSplitBuchungen()
   */
  public DBIterator getSplitBuchungen() throws RemoteException
  {
    DBIterator i = Settings.getDBService().createList(Buchung.class);
    i.addFilter("split_id = " + this.getID());
    return i;
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("bruttoBetrag".equals(arg0) || "brutto".equals(arg0))
      return new Double(getBruttoBetrag());
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchung#setSplitBuchung()
   */
  public void setSplitBuchung(String id) throws RemoteException{
	    setAttribute("split_id",id);
	  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#setAttribute(java.lang.String, java.lang.Object)
   */
  public Object setAttribute(String arg0, Object arg1) throws RemoteException
  {
    if ("bruttoBetrag".equals(arg0))
    {
      double prev = this.brutto;
      this.setBruttoBetrag((Double)arg1);
      return prev;
    }
    else
    {
      return super.setAttribute(arg0, arg1);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.CustomSerializer#getCustomAttributeNames()
   */
  public String[] getCustomAttributeNames() throws RemoteException
  {
    // Wir haengen hier noch das Attribut "bruttoBetrag" an, weil
    // es mit serialisiert werden soll.
    String[] names = super.getAttributeNames();
    String[] newList = new String[names.length+1];
    System.arraycopy(names,0,newList,0,names.length);
    newList[names.length] = "bruttoBetrag";
    return newList;
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
      
      i = getSplitBuchungen();
      while (i.hasNext())
      {
        Buchung b = (Buchung) i.next();
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
  protected void insertCheck() throws ApplicationException
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
  protected void updateCheck() throws ApplicationException
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
    String s = (String) getAttribute("hb_umsatz_id");
    return s == null ? null : s.trim();
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
 * Revision 1.56  2011/05/12 09:10:32  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.55  2010-10-24 22:29:37  willuhn
 * @C Brutto-Betrag bei Buchungen mit exportieren
 *
 * Revision 1.54  2010-10-22 14:31:40  willuhn
 * *** empty log message ***
 *
 * Revision 1.53  2010-10-22 11:47:30  willuhn
 * @B Keine Doppelberechnung mehr in der Buchungserfassung (brutto->netto->brutto)
 *
 * Revision 1.52  2009-07-03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.51.2.1  2008/06/25 09:40:02  willuhn
 * @B Buchungsnummer wurde unter Umstaenden nicht korrekt angezeigt
 *
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