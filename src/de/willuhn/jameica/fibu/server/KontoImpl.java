/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/KontoImpl.java,v $
 * $Revision: 1.14 $
 * $Date: 2004/01/27 22:47:30 $
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
import de.willuhn.jameica.fibu.rmi.*;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class KontoImpl extends AbstractDBObject implements Konto
{

  /**
   * Erzeugt ein neues Konto.
   * @throws RemoteException
   */
  public KontoImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "konto";
  }
  
  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getKontonummer()
   */
  public String getKontonummer() throws RemoteException
  {
    return (String) getField("kontonummer");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getField("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "kontonummer";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getSaldo()
   */
  public double getSaldo() throws RemoteException
  {
    try {
      // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
      if ("".equals(getID()) || getID() == null)
        return 0;

      Statement stmt = getConnection().createStatement();
      Mandant m = Settings.getActiveMandant();
      if (m == null)
        throw new RemoteException("active mandant not set.");

      String sql = "select sum(betrag) as betrag from buchung where YEAR(datum) = " + m.getGeschaeftsjahr() + " and mandant_id = "+ m.getID() +" and konto_id = " + Integer.parseInt(getID());
      ResultSet rs = stmt.executeQuery(sql);
      rs.next();
      return rs.getDouble("betrag");
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to get saldo.",e);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getField("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getKontoArt()
   */
  public Kontoart getKontoArt() throws RemoteException
  {
    return (Kontoart) getField("kontoart_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#getSteuer()
   */
  public Steuer getSteuer() throws RemoteException
  {
    return (Steuer) getField("steuer_id");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
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
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  public void deleteCheck() throws ApplicationException
  {
    throw new ApplicationException("Konten dürfen nicht gelöscht werden.");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    updateCheck();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    try {
      if (getKontenrahmen() == null)
        throw new ApplicationException("Bitte wählen Sie einen Kontenrahmen aus.");

      String name = (String) getField("name");
      if (name == null || "".equals(name))
        throw new ApplicationException("Bitte geben Sie einen Namen für das Konto ein.");
      
      String kontonummer = (String) getField("kontonummer");
      if (kontonummer == null || "".equals(kontonummer))
        throw new ApplicationException("Bitte geben Sie eine Kontonummer ein.");
      
      Kontoart ka = (Kontoart) getField("kontoart");
      if (ka == null)
        throw new ApplicationException("Bitte wählen Sie eine Kontoart aus.");

      // Jetzt muessen wir noch pruefen, ob die Kontonummer schon bei einem anderen
      // Konto vergeben ist
      DBIterator konten = getList();
      while(konten.hasNext())
      {
        Konto k = (Konto) konten.next();
        if (k.getKontonummer().equals(kontonummer) && !k.getID().equals(getID()))
          throw new ApplicationException("Ein Konto mit dieser Kontonummer existiert bereits.");
      }
    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Überprüfung der Pflichtfelder",e);
    }
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getListQuery()
   */
  protected String getListQuery() throws RemoteException
  {
    // ueberschreiben wir, weil wir nur die Konten des Kontenrahmens des aktiven Mandanten haben wollen
    Mandant m = Settings.getActiveMandant();
    if (m == null)
      throw new RemoteException("no active mandant defined.");
    
    Kontenrahmen k = m.getKontenrahmen();
    if (k == null)
      throw new RemoteException("no kontenrahmen defined.");

    return "select " + getIDField() + " from " + getTableName() + " where kontenrahmen_id = " + k.getID();
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#setKontonummer(java.lang.String)
   */
  public void setKontonummer(String kontonummer) throws RemoteException
  {
    setField("kontonummer",kontonummer);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#setKontenrahmen(de.willuhn.jameica.fibu.objects.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen k) throws RemoteException
  {
    if (k == null) return;
    setField("kontenrahmen_id",new Integer(k.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setField("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#setKontoArt(Kontoart)
   */
  public void setKontoArt(Kontoart art) throws RemoteException
  {
    if (art == null) return;
    setField("kontoart_id",new Integer(art.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Konto#setSteuer(de.willuhn.jameica.fibu.objects.Steuer)
   */
  public void setSteuer(Steuer steuer) throws RemoteException
  {
    if (steuer == null) return;
    setField("steuer_id",new Integer(steuer.getID()));
  }

}

/*********************************************************************
 * $Log: KontoImpl.java,v $
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