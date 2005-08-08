/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.26 $
 * $Date: 2005/08/08 22:54:16 $
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
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.GeldKonto;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class BuchungImpl extends AbstractDBObject implements Buchung
{

  /**
   * Erzeugt eine neue Buchung.
   * @throws RemoteException
   */
  public BuchungImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "buchung";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "datum";
  }


  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getDatum()
   */
  public Date getDatum() throws RemoteException
  {
    Date d = (Date) getAttribute("datum");
    return (d == null ? new Date() : d);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getAttribute("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getGeldKonto()
   */
  public GeldKonto getGeldKonto() throws RemoteException
  {
    return (GeldKonto) getAttribute("geldkonto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getText()
   */
  public String getText() throws RemoteException
  {
    return (String) getAttribute("buchungstext");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getBelegnummer()
   */
  public int getBelegnummer() throws RemoteException
  {
    Integer i = (Integer) getAttribute("belegnummer");
    if (i != null)
      return i.intValue();
    
    return createBelegnummer();
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    Double d = (Double) getAttribute("betrag");
    if (d == null)
      return 0;

    double betrag = d.doubleValue();
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
   * @see de.willuhn.jameica.fibu.objects.Buchung#getSteuer()
   */
  public double getSteuer() throws RemoteException
  {
    Double d = (Double) getAttribute("steuer");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setDatum(java.util.Date)
   */
  public void setDatum(Date d) throws RemoteException
  {
    setAttribute("datum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setKonto(de.willuhn.jameica.fibu.objects.Konto)
   */
  public void setKonto(Konto k) throws RemoteException
  {
    setAttribute("konto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setGeldKonto(de.willuhn.jameica.fibu.objects.GeldKonto)
   */
  public void setGeldKonto(GeldKonto k) throws RemoteException
  {
    setAttribute("geldkonto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setMandant(de.willuhn.jameica.fibu.objects.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    setAttribute("mandant_id",m);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setText(java.lang.String)
   */
  public void setText(String text) throws RemoteException
  {
    setAttribute("buchungstext", text);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setBelegnummer(int)
   */
  public void setBelegnummer(int belegnummer) throws RemoteException
  {
    setAttribute("belegnummer",new Integer(belegnummer));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setAttribute("betrag", new Double(betrag));
  }

  public void setSteuer(double steuer) throws RemoteException
  {
    setAttribute("steuer", new Double(steuer));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#createBelegnummer()
   */
  public int createBelegnummer() throws RemoteException
  {
		// wir koennen hier keine Sequence oder aehnliches verwenden
		// weil sie die fachlichen Anforderungen entsprechend getListQuery()
		// nicht beachten wuerde. Ausserdem schneiden wir uns sonst die
		// Kompatibilitaet zu MySQL ab.
    DBIterator iterator = this.getList();
    iterator.setOrder("order by id desc");
    if (!iterator.hasNext())
      return 1;
    return ((Buchung) iterator.next()).getBelegnummer() + 1;
  }


  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("konto_id".equals(field))
      return Konto.class;

    if ("geldkonto_id".equals(field))
      return GeldKonto.class;

    if ("mandant_id".equals(field))
      return Mandant.class;

    return null;
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {

      if (getBetrag() == 0)
        throw new ApplicationException("Bitte geben Sie einen Buchungsbetrag ein.");

      // ich muss hier deshalb getAttribute() aufrufen, weil getBelegnummer automatisch eine erzeugt
      if (getAttribute("belegnummer") == null)
        throw new ApplicationException("Bitte geben Sie eine Belegnummer ein.");

      if (getMandant() == null)
        throw new ApplicationException("Bitte wählen Sie den Mandanten aus.");

      if (getKonto() == null)
        throw new ApplicationException("Bitte geben Sie ein Konto ein.");

      if (getGeldKonto() == null)
        throw new ApplicationException("Bitte geben Sie ein Geld-Konto ein.");

      // Checken, ob Jahr im gueltigen Bereich
      Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());

      Date d = getDatum();
      if (d == null)
        throw new ApplicationException("Bitte geben Sie ein Datum ein.");

      cal.setTime(d);
      int year = cal.get(Calendar.YEAR);
      if (year < Fibu.YEAR_MIN || year > Fibu.YEAR_MAX)
        throw new ApplicationException("Datum befindet sich nicht innerhalb des gültigen Bereiches.");

      int gj = Settings.getActiveMandant().getGeschaeftsjahr();
      if (year != gj)
        throw new ApplicationException("Datum befindet sich nicht innerhalb des aktuellen Geschäftsjahres.");

    }
    catch (RemoteException e)
    {
      Logger.error("error while checking buchung",e);
      throw new ApplicationException("Fehler bei der Prüfung der Buchung.",e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    // hier gilt erst mal das gleiche wie beim Insert-Check ;)
    insertCheck();
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

      if (m == null)
        throw new RemoteException("no active mandant defined");

      int year = m.getGeschaeftsjahr();

      String s = "select " + getIDField() + " from " + getTableName() +
        " where datum >= DATE '" + year + "-01-01'" +
        " and datum <= DATE '" + year + "-12-31'" + // nur aktuelles Geschaeftsjahr
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
 * Revision 1.26  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.25  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.24  2004/01/29 01:01:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.23  2004/01/29 00:45:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2004/01/29 00:06:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2004/01/27 22:47:30  willuhn
 * *** empty log message ***
 *
 * Revision 1.20  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.19  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.18  2003/12/19 19:45:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2003/12/16 02:27:33  willuhn
 * @N BuchungsEngine
 *
 * Revision 1.16  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.15  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.14  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2003/12/05 18:42:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.11  2003/12/01 23:01:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2003/12/01 21:23:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.8  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.5  2003/11/24 16:26:15  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.4  2003/11/24 14:21:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:10:56  willuhn
 * @N buchung dialog works now
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/