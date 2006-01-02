/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontoImpl.java,v $
 * $Revision: 1.39 $
 * $Date: 2006/01/02 15:18:29 $
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class KontoImpl extends AbstractUserObjectImpl implements Konto
{
  private I18N i18n = null;
  
  /**
   * Erzeugt ein neues Konto.
   * @throws RemoteException
   */
  public KontoImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "konto";
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getKontonummer()
   */
  public String getKontonummer() throws RemoteException
  {
    return (String) getAttribute("kontonummer");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getAttribute("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "kontonummer";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getSaldo(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getSaldo(Geschaeftsjahr jahr) throws RemoteException
  {
    // das ist ein neues Konto. Da gibts noch keinen Umsatz.
    if (getID() == null || getID().length() == 0)
      return 0;

    Double d = SaldenCache.get(jahr,this.getKontonummer());
    if (d != null)
      return d.doubleValue();

    double saldo = 0.0d;
    Anfangsbestand a = getAnfangsbestand(jahr);
    if (a != null)
      saldo = a.getBetrag();

    saldo += getUmsatz(jahr);
    SaldenCache.put(jahr,this.getKontonummer(),new Double(saldo));
    return saldo;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getUmsatz(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getUmsatz(Geschaeftsjahr jahr) throws RemoteException
  {
    // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
    if (getID() == null || getID().length() == 0)
      return 0;

    
    Statement stmt = null;
    ResultSet rs   = null;
    try
    {
      DBServiceImpl service = (DBServiceImpl) this.getService();
      stmt = service.getConnection().createStatement();

      double saldo = 0.0d;

      String sql = "select sum(betrag) as b from buchung " +
        " where geschaeftsjahr_id = "+ jahr.getID() + 
        " and habenkonto_id = " + this.getID();
      rs = stmt.executeQuery(sql);
      if (rs.next())
        saldo = rs.getDouble("b");

      sql = "select sum(betrag) as b from buchung " +
      " where geschaeftsjahr_id = "+ jahr.getID() + 
      " and sollkonto_id = " + this.getID();
      rs = stmt.executeQuery(sql);
      if (rs.next())
        saldo -= rs.getDouble("b");

      return saldo;
    }
    catch (Exception e)
    {
      Logger.error("error while closing sql statement",e);
      throw new RemoteException(e.toString());
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (Throwable t)
        {
          Logger.error("error while closing resultset",t);
        }
      }
      if (stmt != null)
      {
        try
        {
          stmt.close();
        }
        catch (Throwable t)
        {
          Logger.error("error while closing sql statement",t);
        }
      }
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getKontoArt()
   */
  public Kontoart getKontoArt() throws RemoteException
  {
    return (Kontoart) getAttribute("kontoart_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getSteuer()
   */
  public Steuer getSteuer() throws RemoteException
  {
    return (Steuer) getAttribute("steuer_id");
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("steuer_id".equals(field))
      return Steuer.class;
    if ("kontoart_id".equals(field))
      return Kontoart.class;
    if ("kontotyp_id".equals(field))
      return Kontotyp.class;
    if ("kontenrahmen_id".equals(field))
      return Kontenrahmen.class;
    return super.getForeignObject(field);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    super.deleteCheck();
    try
    {
      DBIterator list = getService().createList(Steuer.class);
      list.addFilter("steuerkonto_id = " + this.getID());
      
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Das Konto ist als Sammelkonto einem Steuersatz zugeordnet."));

      list = getService().createList(Anfangsbestand.class);
      list.addFilter("konto_id = " + this.getID());
      
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Das Konto besitzt bereits einen Anfangsbestand. Löschen SIe zuerst diesen."));

      list = getService().createList(Buchung.class);
      list.addFilter("habenkonto_id = " + this.getID() + " OR sollkonto_id = " + this.getID());
      
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Es existieren bereits Buchungen auf diesem Konto."));

      list = getService().createList(Anlagevermoegen.class);
      list.addFilter("konto_id = " + this.getID());
      
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Das Konto ist einem Anlage-Gegenstand zugeordnet."));
    }
    catch (RemoteException e)
    {
      Logger.error("unable to check konto",e);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Kontos"));
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try {
      Kontenrahmen kr = getKontenrahmen();
      if (kr == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Kontenrahmen aus."));

      String name = (String) getAttribute("name");
      if (name == null || "".equals(name))
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Namen für das Konto ein."));
      
      String kontonummer = (String) getAttribute("kontonummer");
      if (kontonummer == null || "".equals(kontonummer))
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Kontonummer ein."));
      
      Kontoart ka = (Kontoart) getAttribute("kontoart_id");
      if (ka == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Kontoart aus."));

      // Jetzt muessen wir noch pruefen, ob die Kontonummer schon bei einem anderen
      // Konto vergeben ist
      DBIterator konten = kr.getKonten();
      while(konten.hasNext())
      {
        Konto k = (Konto) konten.next();
        
        // 1. Kontonummer stimmt ueberein
        // 2. ID des Konto verschieden
        if (k.getKontonummer().equals(kontonummer) &&
            !k.getID().equals(getID())
        )
        {
          throw new ApplicationException(i18n.tr("Ein Konto mit dieser Kontonummer existiert bereits in diesem Kontenrahmen."));
        }
      }
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(i18n.tr("Fehler bei der Überprüfung der Pflichtfelder"),e);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setKontonummer(java.lang.String)
   */
  public void setKontonummer(String kontonummer) throws RemoteException
  {
    setAttribute("kontonummer",kontonummer);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setKontenrahmen(de.willuhn.jameica.fibu.rmi.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen k) throws RemoteException
  {
    setAttribute("kontenrahmen_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setKontoArt(de.willuhn.jameica.fibu.rmi.Kontoart)
   */
  public void setKontoArt(Kontoart art) throws RemoteException
  {
    setAttribute("kontoart_id",art);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setSteuer(de.willuhn.jameica.fibu.rmi.Steuer)
   */
  public void setSteuer(Steuer steuer) throws RemoteException
  {
    setAttribute("steuer_id",steuer);
  }

  /**
   * Ueberschrieben, um ein synthetisches Attribut "saldo" zu erzeugen
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("saldo".equals(arg0))
    {
      Geschaeftsjahr jahr = ((DBService)getService()).getActiveGeschaeftsjahr();
      return new Double(getSaldo(jahr));
    }
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public DBIterator getBuchungen(Geschaeftsjahr jahr) throws RemoteException
  {
    Kontoart ka = getKontoArt();
    int art = Kontoart.KONTOART_UNGUELTIG;
    if (ka != null)
      art = ka.getKontoArt();
    // TODO Solange Steuer-Buchungen nur automatisch als Hilfsbuchungen erzeugt werden, ist
    // das ok. Falls Steuer-Buchungen aber manuell als Hauptbuchungen angelegt werden,
    // muessen bei KONTOART_STEUER zwei Listen erzeugt werden. Eine, mit den Hilfsbuchungen,
    // die andere mit den Hauptbuchungen.
    DBIterator list = Settings.getDBService().createList(art == Kontoart.KONTOART_STEUER ? HilfsBuchung.class : Buchung.class);
    list.addFilter(" (sollkonto_id = " + this.getID() + " OR habenkonto_id = " + this.getID() + ")");
    list.addFilter("geschaeftsjahr_id = " + jahr.getID());
    list.setOrder("order by tonumber(datum)");
    return list;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr, java.util.Date, java.util.Date)
   */
  public DBIterator getBuchungen(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException, ApplicationException
  {
    if (!jahr.check(von))
      throw new ApplicationException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Fibu.DATEFORMAT.format(von)));

    if (!jahr.check(bis))
      throw new ApplicationException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Fibu.DATEFORMAT.format(bis)));

    // Uhrzeiten noch zurueckdrehen
    Calendar cal = Calendar.getInstance();
    cal.setTime(von);
    cal.set(Calendar.HOUR_OF_DAY,0);
    cal.set(Calendar.MINUTE,0);
    cal.set(Calendar.SECOND,0);

    Date start = cal.getTime();

    cal.setTime(bis);
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);

    Date end = cal.getTime();
    
    DBIterator list = this.getBuchungen(jahr);
    list.addFilter("tonumber(datum) >= " + start.getTime() + " AND tonumber(datum) <=" + end.getTime());
    return list;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getAnfangsbestand(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public Anfangsbestand getAnfangsbestand(Geschaeftsjahr jahr) throws RemoteException
  {
    DBIterator ab = jahr.getAnfangsbestaende();
    ab.addFilter("konto_id = " + this.getID());
    if (!ab.hasNext())
      return null;
    return (Anfangsbestand) ab.next();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getKontoTyp()
   */
  public Kontotyp getKontoTyp() throws RemoteException
  {
    return (Kontotyp) getAttribute("kontotyp_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setKontoTyp(de.willuhn.jameica.fibu.rmi.Kontotyp)
   */
  public void setKontoTyp(Kontotyp typ) throws RemoteException
  {
    setAttribute("kontotyp_id",typ);
  }
}

/*********************************************************************
 * $Log: KontoImpl.java,v $
 * Revision 1.39  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.38  2006/01/02 01:54:07  willuhn
 * @N Benutzerdefinierte Konten
 *
 * Revision 1.37  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.36  2005/10/18 09:25:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.35  2005/10/06 16:00:37  willuhn
 * @B bug 135
 *
 * Revision 1.34  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.33  2005/10/03 14:22:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.32  2005/09/27 17:41:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.31  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.30  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.29  2005/09/01 21:08:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.28  2005/08/30 23:15:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.27  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.26  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.25  2005/08/25 23:00:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.24  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.23  2005/08/22 21:44:09  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.22  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.20  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.19  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.18  2004/02/09 13:05:13  willuhn
 * @C misc
 *
 * Revision 1.17  2004/01/29 01:05:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.16  2004/01/29 00:19:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2004/01/29 00:13:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.14  2004/01/27 22:47:30  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.11  2003/12/19 19:45:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2003/12/12 21:11:26  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.9  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.8  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.7  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.6  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.4  2003/11/24 16:26:15  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.3  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/