/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2003/11/24 16:26:15 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.objects;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Date;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.rmi.AbstractDBObject;
import de.willuhn.jameica.rmi.DBIterator;

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
    Konto k = (Konto) getField("konto_id");
    if (k == null)
      return (Konto) Application.getDefaultDatabase().createObject(Konto.class,null);
    return k;
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#getGeldKonto()
   */
  public Konto getGeldKonto() throws RemoteException
  {
    Konto k = (Konto) getField("geldkonto_id");
    if (k == null)
      return (Konto) Application.getDefaultDatabase().createObject(Konto.class,null);
    return k;
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
    setField("konto_id",new Integer(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#setGeldKonto(de.willuhn.jameica.fibu.objects.Konto)
   */
  public void setGeldKonto(Konto k) throws RemoteException
  {
    setField("geldkonto_id",new Integer(k.getID()));
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

  /**
   * @see de.willuhn.jameica.fibu.objects.Buchung#createBelegnummer()
   */
  public int createBelegnummer() throws RemoteException
  {
    DBIterator iterator = this.getList();
    iterator.addFilter("belegnummer is not null order by belegnummer desc limit 1");
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
      return Konto.class;

    return null;
  }

}

/*********************************************************************
 * $Log: BuchungImpl.java,v $
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