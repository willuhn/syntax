/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.15 $
 * $Date: 2003/12/11 21:00:34 $
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
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.*;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.server.AbstractDBObject;

/**
 * @author willuhn
 */
public class BuchungImpl extends AbstractDBObject implements Buchung
{

  /**
   * Erzeugt eine neue Buchung oder gibt laedt eine existierende.
   * @param conn Die Connection - reichen wir einfach durch ;).
   * @param id die optional zu ladende Buchung oder null.
   * @throws RemoteException
   */
  public BuchungImpl(Connection conn, String id) throws RemoteException
  {
    super(conn, id);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName() throws RemoteException
  {
    return "buchung";
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "datum";
  }


  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getDatum()
   */
  public Date getDatum() throws RemoteException
  {
    Date d = (Date) getField("datum");
    return (d == null ? new Date() : d);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getField("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getGeldKonto()
   */
  public GeldKonto getGeldKonto() throws RemoteException
  {
    return (GeldKonto) getField("geldkonto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getText()
   */
  public String getText() throws RemoteException
  {
    return (String) getField("text");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getBelegnummer()
   */
  public int getBelegnummer() throws RemoteException
  {
    Integer i = (Integer) getField("belegnummer");
    if (i != null)
      return i.intValue();
    
    return createBelegnummer();
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    Double d = (Double) getField("betrag");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getSteuer()
   */
  public double getSteuer() throws RemoteException
  {
    Double d = (Double) getField("steuer");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getField("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setDatum(java.util.Date)
   */
  public void setDatum(Date d) throws RemoteException
  {
    setField("datum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setKonto(de.willuhn.jameica.fibu.objects.Konto)
   */
  public void setKonto(Konto k) throws RemoteException
  {
    if (k == null) return;
    setField("konto_id",new Integer(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setGeldKonto(de.willuhn.jameica.fibu.objects.GeldKonto)
   */
  public void setGeldKonto(GeldKonto k) throws RemoteException
  {
    if (k == null) return;
    setField("geldkonto_id",new Integer(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setMandant(de.willuhn.jameica.fibu.objects.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    if (m == null) return;
    setField("mandant_id",new Integer(m.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setText(java.lang.String)
   */
  public void setText(String text) throws RemoteException
  {
    setField("text", text);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setBelegnummer(int)
   */
  public void setBelegnummer(int belegnummer) throws RemoteException
  {
    setField("belegnummer",new Integer(belegnummer));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setField("betrag", new Double(betrag));
  }

  public void setSteuer(double steuer) throws RemoteException
  {
    setField("steuer", new Double(steuer));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#createBelegnummer()
   */
  public int createBelegnummer() throws RemoteException
  {
    DBIterator iterator = this.getList();
    iterator.setOrder("order by id desc limit 1");
    if (!iterator.hasNext())
      return 1;
    return ((Buchung) iterator.next()).getBelegnummer() + 1;
  }


  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
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
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    // hier gilt erst mal das gleiche wie beim Update-Check ;)
    updateCheck();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    try {

      if (getBetrag() == 0)
        throw new ApplicationException("Bitte geben Sie einen Buchungsbetrag ein.");

      // ich muss hier deshalb getField() aufrufen, weil getBelegnummer automatisch eine erzeugt
      if (getField("belegnummer") == null)
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
      if (Application.DEBUG)
        e.printStackTrace();
      throw new ApplicationException("Fehler bei der Prüfung des Datums.",e);
    }
  }

  /**
   * Ueberschrieben von AbstractDBObject weil wir nur die Buchungen:
   *  - vom aktiven Mandanten
   *  - aus dem aktuellen Geschaeftsjahr haben wollen.
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    Mandant m = Settings.getActiveMandant();

    if (m == null)
    {
      throw new RemoteException("no active mandant defined");
    }
    int year = m.getGeschaeftsjahr();

    String s = "select id from " + getTableName() +
      " where YEAR(datum) = " + year + 
      " and mandant_id = " + m.getID() +
      " and buchung_id is NULL";
    return s;
  }

}

/*********************************************************************
 * $Log: BuchungImpl.java,v $
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