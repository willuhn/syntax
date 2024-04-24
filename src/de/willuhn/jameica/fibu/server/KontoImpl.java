/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.rmi.Transfer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.DateUtil;
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
    Integer i = (Integer) super.getAttribute("kontenrahmen_id");
    if (i == null)
      return null;
   
    Cache cache = Cache.get(Kontenrahmen.class,true);
    return (Kontenrahmen) cache.get(i);

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
   * @see de.willuhn.jameica.fibu.rmi.Konto#getSaldo(java.util.Date)
   */
  @Override
  public double getSaldo(Date date) throws RemoteException
  {
    // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
    if (getID() == null || getID().length() == 0 || date == null)
      return 0;

    Geschaeftsjahr jahr = findGeschaeftsjahr(date);
    if (jahr == null)
    {
      Logger.warn("no geschaeftsjahr found for date " + date);
      return 0.0d;
    }

    double saldo = 0.0d;
    Anfangsbestand a = getAnfangsbestand(jahr);
    if (a != null)
      saldo = a.getBetrag();

    saldo += getUmsatz(jahr,null,date);
    return saldo;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getUmsatzAfter(java.util.Date)
   */
  public double getUmsatzAfter(Date date) throws RemoteException
  {
    // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
    if (getID() == null || getID().length() == 0 || date == null)
      return 0;

    Geschaeftsjahr jahr = findGeschaeftsjahr(date);
    if (jahr == null)
    {
      Logger.warn("no geschaeftsjahr found for date " + date);
      return 0.0d;
    }

    // Wir setzen noch die Uhrzeit auf das Ende des Tages, um sicherzustellen,
    // dass die Buchungen des Tages dabei sind
    return getUmsatz(getHauptBuchungen(jahr,jahr.getBeginn(),DateUtil.endOfDay(date)),
                     getHilfsBuchungen(jahr,jahr.getBeginn(),DateUtil.endOfDay(date)));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getUmsatzBefore(java.util.Date)
   */
  public double getUmsatzBefore(Date date) throws RemoteException
  {
    // Da der aktuelle Tag nicht mitzaehlen soll, ziehen wir einen ab
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE,-1);
    
    // Wir pruefen noch, ob wir mit dem neuen Datum aus dem Geschaeftsjahr fliegen
    // wuerden:
    Date dayBefore = cal.getTime();
    Geschaeftsjahr j = findGeschaeftsjahr(date);
    if (!j.check(dayBefore))
      return 0.0d; // Jepp, also liefern wir 0.0, denn wir sind ganz am Anfang des Geschaeftsjahres angekommen
    return getUmsatzAfter(dayBefore);
  }
  
  /**
   * Sucht das zum Datum gehoerende Geschaeftsjahr beim aktuellen Mandanten.
   * @param date das Datum.
   * @return das Geschaeftsjahr oder null.
   * @throws RemoteException
   */
  private Geschaeftsjahr findGeschaeftsjahr(Date date) throws RemoteException
  {
    // Wir ermitteln das Geschaeftsjahr, in dem sich das Datum befindet
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    if (jahr.check(date))
      return jahr;
    
    // Ist nicht das aktuielle, dann muessen wir es suchen
    DBIterator list = jahr.getMandant().getGeschaeftsjahre();
    while (list.hasNext())
    {
      jahr = (Geschaeftsjahr) list.next();
      if (jahr.check(date))
        return jahr;
    }
    return null;
  }
  
  /**
   * Liefert den Umsatz aus der Liste der uebergebenen Buchungen.
   * @param buchungen Liste der Buchungen.
   * @param hilfsbuchungen Liste der Hilfsbuchungen.
   * @return Umsatz.
   * @throws RemoteException
   */
  private double getUmsatz(DBIterator buchungen, DBIterator hilfsbuchungen) throws RemoteException
  {
    // das ist ein neues Konto. Von daher wissen wir den Saldo natuerlich noch nicht ;)
    if (getID() == null || getID().length() == 0)
      return 0;

    double soll = 0.0d;
    double haben = 0.0d;

    int kontoArt = getKontoArt().getKontoArt();
    Kontotyp typ = getKontoTyp();
    //Split-Hauptbuchungen rausfiltern
    buchungen.addFilter("NOT EXISTS(SELECT 1 FROM buchung b WHERE b.split_id = buchung.id)");
    
    // Erst die Hauptbuchungen
    while (buchungen.hasNext())
    {
      Transfer t = (Transfer) buchungen.next();
      if (t.getSollKonto().equals(this))
        soll += t.getBetrag();
      else
        haben += t.getBetrag();
    }

    // Und jetzt noch die Hilfs-Buchungen
    while (hilfsbuchungen.hasNext())
    {
      Transfer t = (Transfer) hilfsbuchungen.next();
      if (t.getSollKonto().equals(this))
        soll += t.getBetrag();
      else
        haben += t.getBetrag();
    }
    
    // Siehe auch BetriebsergebnisImpl#getEinnahmen
    if (kontoArt == Kontoart.KONTOART_ERLOES || (kontoArt == Kontoart.KONTOART_STEUER && typ != null && typ.getKontoTyp() == Kontotyp.KONTOTYP_EINNAHME))
      return haben - soll;
    return soll - haben;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getUmsatz(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getUmsatz(Geschaeftsjahr jahr) throws RemoteException
  {
    return getUmsatz(getHauptBuchungen(jahr), getHilfsBuchungen(jahr));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getUmsatz(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr, java.util.Date, java.util.Date)
   */
  public double getUmsatz(Geschaeftsjahr jahr, Date von, Date bis)
      throws RemoteException
  {
    return getUmsatz(getHauptBuchungen(jahr, von, bis),
        getHilfsBuchungen(jahr, von, bis));
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
   
    Cache cache = Cache.get(Kontoart.class,true);
    return (Kontoart) cache.get(i);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getSteuer()
   */
  public Steuer getSteuer() throws RemoteException
  {
    Integer i = (Integer) super.getAttribute("steuer_id");
    if (i == null)
      return null;
   
    Cache cache = Cache.get(Steuer.class,true);
    return (Steuer) cache.get(i);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#delete()
   */
  @Override
  public void delete() throws RemoteException, ApplicationException
  {
    super.delete();
    Cache.clear(Konto.class);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#store()
   */
  @Override
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    Cache.clear(Konto.class);
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
        throw new ApplicationException(i18n.tr("Das Konto besitzt bereits einen Anfangsbestand. Löschen Sie zuerst diesen."));

      list = getService().createList(Buchung.class);
      list.addFilter("habenkonto_id = " + this.getID() + " OR sollkonto_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Es existieren bereits Buchungen auf diesem Konto."));

      list = getService().createList(Buchungstemplate.class);
      list.addFilter("habenkonto_id = " + this.getID() + " OR sollkonto_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Es existieren bereits Buchungsvorlagen auf diesem Konto."));

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
      
      Kontoart ka = this.getKontoArt();
      if (ka == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Kontoart aus."));

      // Jetzt muessen wir noch pruefen, ob die Kontonummer schon bei einem anderen
      // Konto vergeben ist
      Konto other = kr.findByKontonummer(kontonummer);
      if (other != null && !other.equals(this))
        throw new ApplicationException(i18n.tr("Ein Konto mit dieser Kontonummer existiert bereits in diesem Kontenrahmen."));
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
    setAttribute("kontenrahmen_id",k == null || k.getID() == null ? null : new Integer(k.getID()));
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
    setAttribute("kontoart_id",art == null || art.getID() == null ? null : new Integer(art.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setSteuer(de.willuhn.jameica.fibu.rmi.Steuer)
   */
  public void setSteuer(Steuer steuer) throws RemoteException
  {
    setAttribute("steuer_id",steuer == null || steuer.getID() == null ? null : new Integer(steuer.getID()));
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
      if (jahr == null)
        return new Double(0.0d);
      
      // Checken, ob das ueberhaupt der selbe Kontenrahmen ist
      if (!BeanUtil.equals(this.getKontenrahmen(),jahr.getKontenrahmen()))
        return new Double(0.0d);
      
      return new Double(getSaldo(jahr));
    }
    
    if ("kontoArt".equals(arg0))
      return this.getKontoArt();

    if ("steuer".equals(arg0))
      return this.getSteuer();
    
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getHauptBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public DBIterator getHauptBuchungen(Geschaeftsjahr jahr) throws RemoteException
  {
    return getBuchungen(jahr, null, null, Buchung.class);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getHilfsBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public DBIterator getHilfsBuchungen(Geschaeftsjahr jahr) throws RemoteException
  {
    return getBuchungen(jahr, null, null, HilfsBuchung.class);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getHauptBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr, java.util.Date, java.util.Date)
   */
  public DBIterator getHauptBuchungen(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException
  {
    return getBuchungen(jahr, von, bis, Buchung.class);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getHilfsBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr, java.util.Date, java.util.Date)
   */
  public DBIterator getHilfsBuchungen(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException
  {
    return getBuchungen(jahr, von, bis, HilfsBuchung.class);
  }

  /**
   * Interne Hilfs-Funktion zum Laden der entsprechenden Buchungen.
   * @param jahr das Jahr.
   * @param von Start-Datum.
   * @param bis End-Datum.
   * @param type Art der Buchungen.
   * @return Liste der Buchungen.
   * @throws RemoteException
   */
  private DBIterator getBuchungen(Geschaeftsjahr jahr, Date von, Date bis, Class type) throws RemoteException
  {
    
    if (von != null && !jahr.check(von))
      throw new RemoteException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(von)));

    if (bis != null && !jahr.check(bis))
      throw new RemoteException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(bis)));

    Date start = null;
    if (von != null)
      start = DateUtil.startOfDay(von);

    Date end = null;
    if (bis != null)
      end = DateUtil.endOfDay(bis);
    
    DBService db = ((DBService)getService());

    DBIterator list = getService().createList(type);
    list.addFilter("(sollkonto_id = " + this.getID() + " OR habenkonto_id = " + this.getID() + ")");
    list.addFilter("geschaeftsjahr_id = " + jahr.getID());
    if (start != null)
      list.addFilter(db.getSQLTimestamp("datum") + " >= " + start.getTime());
    if (end != null)
      list.addFilter(db.getSQLTimestamp("datum") + " <=" + end.getTime());
    list.setOrder("order by datum,belegnummer");

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
   
    Cache cache = Cache.get(Kontotyp.class,true);
    return (Kontotyp) cache.get(i);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#setKontoTyp(de.willuhn.jameica.fibu.rmi.Kontotyp)
   */
  public void setKontoTyp(Kontotyp typ) throws RemoteException
  {
    setAttribute("kontotyp_id",typ == null || typ.getID() == null ? null : new Integer(typ.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Konto#getNumBuchungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr, java.util.Date, java.util.Date)
   */
  public int getNumBuchungen(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException
  {
    if (this.isNewObject())
      return 0;
    
    if (von != null && !jahr.check(von))
      throw new RemoteException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(von)));

    if (bis != null && !jahr.check(bis))
      throw new RemoteException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(bis)));

    Date start = null;
    if (von != null)
      start = DateUtil.startOfDay(von);

    Date end = null;
    if (bis != null)
      end = DateUtil.endOfDay(bis);

    String sql = "select count(id) from buchung where geschaeftsjahr_id = ? and (sollkonto_id = ? or habenkonto_id = ?)";

    DBService db = (DBService) this.getService();

    if (start != null) sql += " and " + db.getSQLTimestamp("datum") + " >= " + start.getTime();
    if (end != null)   sql += " and " + db.getSQLTimestamp("datum") + " <=" + end.getTime();

    ResultSetExtractor rs = new ResultSetExtractor()
    {
      public Object extract(ResultSet rs) throws RemoteException, SQLException
      {
        if (!rs.next())
          return new Integer(0);
        return new Integer(rs.getInt(1));
      }
    };

    Integer id = new Integer(this.getID());
    Integer i = (Integer) db.execute(sql, new Object[] {new Integer(jahr.getID()),id,id},rs);
    return i == null ? 0 : i.intValue();
  }
}
