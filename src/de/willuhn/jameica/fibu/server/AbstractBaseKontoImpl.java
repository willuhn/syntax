/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/AbstractBaseKontoImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/15 23:38:27 $
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
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.BaseKonto;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public abstract class AbstractBaseKontoImpl extends AbstractDBObject implements BaseKonto
{
  
  /**
   * Erzeugt ein neues Konto.
   * @throws RemoteException
   */
  public AbstractBaseKontoImpl() throws RemoteException
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
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getKontonummer()
   */
  public String getKontonummer() throws RemoteException
  {
    return (String) getAttribute("kontonummer");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getKontenrahmen()
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
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getSaldo()
   */
  public double getSaldo() throws RemoteException
  {
    // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
    if (getID() == null || getID().length() == 0)
      return 0;

    Double d = SaldenCache.get(this.getKontonummer());
    if (d != null)
      return d.doubleValue();
    
    Statement stmt = null;
    try
    {
      DBServiceImpl service = (DBServiceImpl) this.getService();
      stmt = service.getConnection().createStatement();

      Mandant m = Settings.getActiveMandant();

      Date start = m.getGeschaeftsjahrVon();
      Date end   = m.getGeschaeftsjahrBis();

      String sql = "select sum(betrag) as b from buchung " +
        " where TONUMBER(datum) >= " + start.getTime() +
        " and TONUMBER(datum) <= " + end.getTime() + // nur aktuelles Geschaeftsjahr
        " and mandant_id = "+ m.getID() + 
        " and konto_id = " + this.getID();
      ResultSet rs = stmt.executeQuery(sql);
      rs.next();
      double dd = rs.getDouble("b");
      SaldenCache.put(this.getKontonummer(),new Double(dd));
      return dd;
    }
    catch (Exception e)
    {
      Logger.error("error while closing sql statement",e);
      throw new RemoteException(e.toString());
    }
    finally
    {
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
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getKontoArt()
   */
  public Kontoart getKontoArt() throws RemoteException
  {
    return (Kontoart) getAttribute("kontoart_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getSteuer()
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
    if ("kontoart_id".equals(field))
      return Kontoart.class;
    if ("steuer_id".equals(field))
      return Steuer.class;
    if ("kontenrahmen_id".equals(field))
      return Kontenrahmen.class;
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
      if (getKontenrahmen() == null)
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
        BaseKonto k = (BaseKonto) konten.next();
        if (k.getKontonummer().equals(kontonummer) && !k.getID().equals(getID()))
          throw new ApplicationException("Ein Konto mit dieser Kontonummer existiert bereits.");
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
   * @see de.willuhn.datasource.db.AbstractDBObject#getListQuery()
   */
  protected String getListQuery()
  {
    try
    {
      // ueberschreiben wir, weil wir nur die Konten des Kontenrahmens des aktiven Mandanten haben wollen
      Mandant m = Settings.getActiveMandant();
      
      Kontenrahmen k = m.getKontenrahmen();
      if (k == null)
        throw new RemoteException("no kontenrahmen defined.");

      return "select " + getIDField() + " from " + getTableName() + " where kontenrahmen_id = " + k.getID();
    }
    catch (RemoteException e)
    {
      Logger.error("unable to load list query",e);
      return null;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#setKontonummer(java.lang.String)
   */
  public void setKontonummer(String kontonummer) throws RemoteException
  {
    setAttribute("kontonummer",kontonummer);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#setKontenrahmen(de.willuhn.jameica.fibu.rmi.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen k) throws RemoteException
  {
    setAttribute("kontenrahmen_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#setKontoArt(de.willuhn.jameica.fibu.rmi.Kontoart)
   */
  public void setKontoArt(Kontoart art) throws RemoteException
  {
    setAttribute("kontoart_id",art);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#setSteuer(de.willuhn.jameica.fibu.rmi.Steuer)
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
      return new Double(getSaldo());
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseKonto#getBuchungen()
   */
  public DBIterator getBuchungen() throws RemoteException
  {
    Kontoart ka = getKontoArt();
    int art = Kontoart.KONTOART_UNGUELTIG;
    if (ka != null)
      art = ka.getKontoArt();
    DBIterator list = Settings.getDBService().createList(art == Kontoart.KONTOART_STEUER ? HilfsBuchung.class : Buchung.class);
    list.addFilter(" (konto_id = " + this.getID() + " OR geldkonto_id = " + this.getID() + ")");
    list.setOrder("order by id desc");
    return list;
  }

}

/*********************************************************************
 * $Log: AbstractBaseKontoImpl.java,v $
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