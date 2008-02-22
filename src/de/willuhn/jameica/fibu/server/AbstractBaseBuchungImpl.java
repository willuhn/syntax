/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AbstractBaseBuchungImpl.java,v $
 * $Revision: 1.24 $
 * $Date: 2008/02/22 10:41:41 $
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
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public abstract class AbstractBaseBuchungImpl extends AbstractTransferImpl implements BaseBuchung
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
    return (Date) getAttribute("datum");
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
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    setAttribute("geschaeftsjahr_id",jahr);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setBelegnummer(int)
   */
  public void setBelegnummer(int belegnummer) throws RemoteException
  {
    setAttribute("belegnummer",new Integer(belegnummer));
  }

  /**
   * Erzeugt eine neue Beleg-Nummer.
   * @return Neue Belegnummer.
   * @throws RemoteException
   */
  private int createBelegnummer() throws RemoteException
  {
    // TODO Create Belegnummer noch nicht schoen
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
    if ("geschaeftsjahr_id".equals(field))
      return Geschaeftsjahr.class;

    return super.getForeignObject(field);
  }
  

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {
      Geschaeftsjahr jahr = getGeschaeftsjahr();

      if (jahr == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Geschäftsjahr aus."));

      if (jahr.isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

      if (getBetrag() == 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Buchungsbetrag ungleich 0 ein."));

      Konto soll = getSollKonto();
      Konto haben = getHabenKonto();
      
      if (soll == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto für die Soll-Buchung ein."));

      if (haben == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto für die Haben-Buchung ein."));

      if (soll.equals(haben))
        throw new ApplicationException(i18n.tr("Soll- und Haben-Konto dürfen nicht identisch sein."));
      
      double steuer = getSteuer();
      if (steuer > 0.0d && soll.getSteuer() != null && haben.getSteuer() != null)
        throw new ApplicationException(i18n.tr("Es wurde ein Steuersatz eingegeben, obwohl keine zu versteuernden Konten ausgewählt wurden"));
      
      Date d = getDatum();
      if (d == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Datum ein."));

      if (!jahr.check(d))
        throw new ApplicationException(i18n.tr("Datum befindet sich nicht innerhalb des aktuellen Geschäftsjahres."));

      // ich muss hier deshalb getAttribute() aufrufen, weil getBelegnummer automatisch eine erzeugt
      Integer i = (Integer) getAttribute("belegnummer");
      if (i == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Belegnummer ein."));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking buchung",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Buchung."),e);
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
    this.insertCheck();
  }

  /**
   * Ueberschrieben, um den Salden-Cache fuer die Konten zu loeschen.
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    // Fuer's Logging:
    Logger.info(getAttribute("sollkonto") + " an " + getAttribute("habenkonto") + ": " + Fibu.DECIMALFORMAT.format(getBetrag()) + " (" + getText() + ")");
    SaldenCache.remove(getSollKonto().getKontonummer());
    SaldenCache.remove(getHabenKonto().getKontonummer());
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#isGeprueft()
   */
  public boolean isGeprueft() throws RemoteException
  {
    Integer i = (Integer) getAttribute("geprueft");
    return i != null && i.intValue() == 1;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setGeprueft(boolean)
   */
  public void setGeprueft(boolean b) throws RemoteException
  {
    setAttribute("geprueft",new Integer(b ? 1 : 0));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getHabenKonto()
   */
  public Konto getHabenKonto() throws RemoteException
  {
    Geschaeftsjahr jahr = getGeschaeftsjahr();
    return jahr == null ? null : jahr.getKontenrahmen().findByKontonummer((String)getAttribute("habenkonto"));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getSollKonto()
   */
  public Konto getSollKonto() throws RemoteException
  {
    Geschaeftsjahr jahr = getGeschaeftsjahr();
    return jahr == null ? null : jahr.getKontenrahmen().findByKontonummer((String)getAttribute("sollkonto"));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setHabenKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setHabenKonto(Konto k) throws RemoteException
  {
    setAttribute("habenkonto",k == null ? null : k.getKontonummer());
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setSollKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setSollKonto(Konto k) throws RemoteException
  {
    setAttribute("sollkonto",k == null ? null : k.getKontonummer());
  }
  
  
}

/*********************************************************************
 * $Log: AbstractBaseBuchungImpl.java,v $
 * Revision 1.24  2008/02/22 10:41:41  willuhn
 * @N Erweiterte Mandantenfaehigkeit (IN PROGRESS!)
 *
 * Revision 1.23  2006/01/09 01:52:40  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.21  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.20  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.19  2005/10/06 14:48:40  willuhn
 * @N Sonderregelung fuer Abschreibunsgbuchungen
 *
 * Revision 1.18  2005/10/06 14:32:22  willuhn
 * @B check auf abschreibung in insertCheck
 *
 * Revision 1.17  2005/10/06 13:19:22  willuhn
 * @B bug 133
 *
 * Revision 1.16  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2005/09/30 17:12:06  willuhn
 * @B bug 122
 *
 * Revision 1.14  2005/09/27 17:41:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/09/25 22:18:23  willuhn
 * @B bug 122
 *
 * Revision 1.12  2005/09/25 22:05:09  willuhn
 * @B bug 121
 *
 * Revision 1.11  2005/09/24 13:00:13  willuhn
 * @B bugfixes according to bugzilla
 *
 * Revision 1.10  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.9  2005/08/29 15:20:51  willuhn
 * @B bugfixing
 *
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