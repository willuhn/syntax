/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontoImpl.java,v $
 * $Revision: 1.32 $
 * $Date: 2005/09/27 17:41:27 $
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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.Session;

/**
 * @author willuhn
 */
public class KontoImpl extends AbstractDBObject implements Konto
{
  // Cachen wir der Performance wegen
  private final transient static Session kontoArtCache     = new Session();
  private final transient static Session kontoTypCache     = new Session();
  private final transient static Session kontenRahmenCache = new Session();
  
  /**
   * Erzeugt ein neues Konto.
   * @throws RemoteException
   */
  public KontoImpl() throws RemoteException
  {
    super();
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
    Integer i = (Integer) super.getAttribute("kontenrahmen_id");
    if (i == null)
      return null;
    Kontenrahmen kr = (Kontenrahmen) kontenRahmenCache.get(i);
    if (kr == null)
    {
      kr = (Kontenrahmen) getService().createObject(Kontenrahmen.class,i.toString());
      kontenRahmenCache.put(i,kr);
    }
    return kr;
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

    Double d = SaldenCache.get(jahr.getID() + "." + this.getKontonummer());
    if (d != null)
      return d.doubleValue();

    double saldo = 0.0d;
    Anfangsbestand a = getAnfangsbestand(jahr);
    if (a != null)
      saldo = a.getBetrag();

    saldo += getUmsatz(jahr);
    SaldenCache.put(jahr.getID() + "." + this.getKontonummer(),new Double(saldo));
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
        " and sollkonto_id = " + this.getID();
      rs = stmt.executeQuery(sql);
      if (rs.next())
        saldo = rs.getDouble("b");

      sql = "select sum(betrag) as b from buchung " +
      " where geschaeftsjahr_id = "+ jahr.getID() + 
      " and habenkonto_id = " + this.getID();
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
    Integer i = (Integer) super.getAttribute("kontoart_id");
    if (i == null)
      return null;
    Kontoart ka = (Kontoart) kontoArtCache.get(i);
    if (ka == null)
    {
      ka = (Kontoart) getService().createObject(Kontoart.class,i.toString());
      kontoArtCache.put(i,ka);
    }
    return ka;
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
    return null;
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  public void deleteCheck() throws ApplicationException
  {
    throw new ApplicationException("Konten dürfen nicht gelöscht werden.");
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {
      Kontenrahmen kr = getKontenrahmen();
      if (kr == null)
        throw new ApplicationException("Bitte wählen Sie einen Kontenrahmen aus.");

      String name = (String) getAttribute("name");
      if (name == null || "".equals(name))
        throw new ApplicationException("Bitte geben Sie einen Namen für das Konto ein.");
      
      String kontonummer = (String) getAttribute("kontonummer");
      if (kontonummer == null || "".equals(kontonummer))
        throw new ApplicationException("Bitte geben Sie eine Kontonummer ein.");
      
      Kontoart ka = (Kontoart) getAttribute("kontoart_id");
      if (ka == null)
        throw new ApplicationException("Bitte wählen Sie eine Kontoart aus.");

      // Jetzt muessen wir noch pruefen, ob die Kontonummer schon bei einem anderen
      // Konto vergeben ist
      DBIterator konten = getList();
      while(konten.hasNext())
      {
        Konto k = (Konto) konten.next();
        Kontenrahmen kr2 = k.getKontenrahmen();
        if (k.getKontonummer().equals(kontonummer) && !k.getID().equals(getID()) && kr.getID().equals(kr2.getID()))
          throw new ApplicationException("Ein Konto mit dieser Kontonummer existiert bereits in diesem Kontenrahmen.");
      }
    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Überprüfung der Pflichtfelder",e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    insertCheck();
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
    setAttribute("kontenrahmen_id",k == null ? null : new Integer(k.getID()));
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
    setAttribute("kontoart_id",art == null ? null : new Integer(art.getID()));
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
      return new Double(getSaldo(Settings.getActiveGeschaeftsjahr()));
    if ("kontenrahmen_id".equals(arg0))
      return getKontenrahmen();
    if ("kontoart_id".equals(arg0))
      return getKontoArt();
    if ("kontotyp_id".equals(arg0))
      return getKontoTyp();
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
    Integer i = (Integer) super.getAttribute("kontotyp_id");
    if (i == null)
      return null;
    Kontotyp kt = (Kontotyp) kontoTypCache.get(i);
    if (kt == null)
    {
      kt = (Kontotyp) getService().createObject(Kontotyp.class,i.toString());
      kontoTypCache.put(i,kt);
    }
    return kt;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setKontoTyp(de.willuhn.jameica.fibu.rmi.Kontotyp)
   */
  public void setKontoTyp(Kontotyp typ) throws RemoteException
  {
    setAttribute("kontotyp_id",typ == null ? null : new Integer(typ.getID()));
  }
}

/*********************************************************************
 * $Log: KontoImpl.java,v $
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