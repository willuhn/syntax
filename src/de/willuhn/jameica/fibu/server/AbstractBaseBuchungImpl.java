/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractBaseBuchungImpl.java,v $
 * $Revision: 1.8 $
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
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public abstract class AbstractBaseBuchungImpl extends AbstractDBObject implements BaseBuchung
{
  I18N i18n = null;
  
  /**
   * Erzeugt eine neue BaseBuchung.
   * @throws RemoteException
   */
  public AbstractBaseBuchungImpl() throws RemoteException
  {
    super();
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
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
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getSollKonto()
   */
  public Konto getSollKonto() throws RemoteException
  {
    Konto k = (Konto) getAttribute("sollkonto_id");
    return k;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getHabenKonto()
   */
  public Konto getHabenKonto() throws RemoteException
  {
    Konto k = (Konto) getAttribute("habenkonto_id");
    return k;
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
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getGeschaeftsjahr()
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException
  {
    return (Geschaeftsjahr) getAttribute("geschaeftsjahr_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setDatum(java.util.Date)
   */
  public void setDatum(Date d) throws RemoteException
  {
    setAttribute("datum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setSollKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setSollKonto(Konto k) throws RemoteException
  {
    setAttribute("sollkonto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setHabenKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setHabenKonto(Konto k) throws RemoteException
  {
    setAttribute("habenkonto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    setAttribute("geschaeftsjahr_id",jahr);
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
    if ("sollkonto_id".equals(field))
      return Konto.class;

    if ("habenkonto_id".equals(field))
      return Konto.class;

    if ("geschaeftsjahr_id".equals(field))
      return Geschaeftsjahr.class;

    return null;
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {
      if (getBetrag() == 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Buchungsbetrag ein."));

      if (getSollKonto() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto f�r die Soll-Buchung ein."));

      if (getHabenKonto() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto f�r die Haben-Buchung ein."));

      Date d = getDatum();
      if (d == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Datum ein."));

      Geschaeftsjahr jahr = getGeschaeftsjahr();
      if (jahr == null)
        throw new ApplicationException(i18n.tr("Bitte w�hlen Sie ein Gesch�ftsjahr aus."));

      if (!jahr.check(d))
        throw new ApplicationException(i18n.tr("Datum befindet sich nicht innerhalb des aktuellen Gesch�ftsjahres."));

      // ich muss hier deshalb getAttribute() aufrufen, weil getBelegnummer automatisch eine erzeugt
      Integer i = (Integer) getAttribute("belegnummer");
      if (i == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Belegnummer ein."));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking buchung",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Pr�fung der Buchung."),e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    super.delete();
    SaldenCache.remove(getSollKonto().getKontonummer());
    SaldenCache.remove(getHabenKonto().getKontonummer());
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    insertCheck();
  }

  /**
   * Ueberschrieben, um den Salden-Cache fuer die Konten zu loeschen.
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    SaldenCache.remove(getSollKonto().getKontonummer());
    SaldenCache.remove(getHabenKonto().getKontonummer());
  }
}

/*********************************************************************
 * $Log: AbstractBaseBuchungImpl.java,v $
 * Revision 1.8  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.7  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 * Revision 1.6  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
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