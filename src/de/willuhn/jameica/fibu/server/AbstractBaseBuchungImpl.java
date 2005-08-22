/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractBaseBuchungImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2005/08/22 16:37:22 $
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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public abstract class AbstractBaseBuchungImpl extends AbstractDBObject implements BaseBuchung
{
  
  /**
   * Erzeugt eine neue BaseBuchung.
   * @throws RemoteException
   */
  public AbstractBaseBuchungImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
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
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getDatum()
   */
  public Date getDatum() throws RemoteException
  {
    Date d = (Date) getAttribute("datum");
    return (d == null ? new Date() : d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getAttribute("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getGeldKonto()
   */
  public Konto getGeldKonto() throws RemoteException
  {
    return (Konto) getAttribute("geldkonto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getText()
   */
  public String getText() throws RemoteException
  {
    return (String) getAttribute("buchungstext");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getBelegnummer()
   */
  public int getBelegnummer() throws RemoteException
  {
    Integer i = (Integer) getAttribute("belegnummer");
    if (i != null)
      return i.intValue();
    
    return createBelegnummer();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    Double d = (Double) getAttribute("betrag");
    if (d == null)
      return 0;

    return d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getSteuer()
   */
  public double getSteuer() throws RemoteException
  {
    Double d = (Double) getAttribute("steuer");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setDatum(java.util.Date)
   */
  public void setDatum(Date d) throws RemoteException
  {
    setAttribute("datum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setKonto(Konto k) throws RemoteException
  {
    setAttribute("konto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setGeldKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setGeldKonto(Konto k) throws RemoteException
  {
    setAttribute("geldkonto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    setAttribute("mandant_id",m);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setText(java.lang.String)
   */
  public void setText(String text) throws RemoteException
  {
    setAttribute("buchungstext", text);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setBelegnummer(int)
   */
  public void setBelegnummer(int belegnummer) throws RemoteException
  {
    setAttribute("belegnummer",new Integer(belegnummer));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setAttribute("betrag", new Double(betrag));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setSteuer(double)
   */
  public void setSteuer(double steuer) throws RemoteException
  {
    setAttribute("steuer", new Double(steuer));
  }

  /**
   * Erzeugt eine neue Beleg-Nummer.
   * @return Neue Belegnummer.
   * @throws RemoteException
   */
  private int createBelegnummer() throws RemoteException
  {
		// wir koennen hier keine Sequence oder aehnliches verwenden
		// weil sie die fachlichen Anforderungen entsprechend getListQuery()
		// nicht beachten wuerde. Ausserdem schneiden wir uns sonst die
		// Kompatibilitaet zu MySQL ab.
    DBIterator iterator = this.getList();
    iterator.setOrder("order by id desc");
    if (!iterator.hasNext())
      return 1;
    return ((BaseBuchung) iterator.next()).getBelegnummer() + 1;
  }


  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("konto_id".equals(field))
      return Konto.class;

    if ("geldkonto_id".equals(field))
      return Konto.class;

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
      
      Mandant m = getMandant();

      if (getBetrag() == 0.0d)
        throw new ApplicationException("Bitte geben Sie einen Buchungsbetrag ein.");

      // ich muss hier deshalb getAttribute() aufrufen, weil getBelegnummer automatisch eine erzeugt
      if (getAttribute("belegnummer") == null)
        throw new ApplicationException("Bitte geben Sie eine Belegnummer ein.");

      if (m == null)
        throw new ApplicationException("Bitte wählen Sie den Mandanten aus.");

      if (getKonto() == null)
        throw new ApplicationException("Bitte geben Sie ein Konto ein.");

      if (getGeldKonto() == null)
        throw new ApplicationException("Bitte geben Sie ein Geld-Konto ein.");

      Date d = getDatum();
      if (d == null)
        throw new ApplicationException("Bitte geben Sie ein Datum ein.");

      if (!m.checkGeschaeftsJahr(d))
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
   * Ueberschrieben, um den Salden-Cache fuer die Konten zu loeschen.
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    SaldenCache.remove(getKonto().getKontonummer());
    SaldenCache.remove(getGeldKonto().getKontonummer());
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
        " where TONUMBER(datum) >= " + start.getTime() + 
        " and TONUMBER(datum) <= " + end.getTime() + // nur aktuelles Geschaeftsjahr
        " and mandant_id = " + m.getID();
      return s;
    }
    catch (RemoteException e)
    {
      Logger.error("unable to create list query",e);
      return null;
    }
  }
}

/*********************************************************************
 * $Log: AbstractBaseBuchungImpl.java,v $
 * Revision 1.4  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.3  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/15 13:18:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.28  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.27  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
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