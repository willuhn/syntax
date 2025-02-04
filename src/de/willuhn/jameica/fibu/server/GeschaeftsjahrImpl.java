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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Betriebsergebnis;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.util.GeschaeftsjahrUtil;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung eines Geschaeftsjahres.
 */
public class GeschaeftsjahrImpl extends AbstractDBObject implements Geschaeftsjahr
{

  private final static transient I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * @throws java.rmi.RemoteException
   */
  public GeschaeftsjahrImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "geschaeftsjahr";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getBeginn()
   */
  public Date getBeginn() throws RemoteException
  {
    Date d = (Date) getAttribute("beginn");

    if (d == null)
    {
      // Wir erstellen automatisch ein neues, wenn keins existiert.
      Logger.info("no geschaeftsjahr start given, using current year");
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MONTH,Calendar.JANUARY);
      cal.set(Calendar.DAY_OF_MONTH,1);
      d = cal.getTime();
    }

    // Jetzt noch auf den Anfang des Tages setzen.
    d = DateUtil.startOfDay(d);
    setBeginn(d);
    return d;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setBeginn(java.util.Date)
   */
  public void setBeginn(Date beginn) throws RemoteException
  {
    setAttribute("beginn",beginn);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getEnde()
   */
  public Date getEnde() throws RemoteException
  {
    Date d = (Date) getAttribute("ende");

    if (d == null)
    {
      // Wir erstellen automatisch ein neues, wenn keins existiert.
      Logger.info("no geschaeftsjahr start given, using current year");
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MONTH,Calendar.DECEMBER);
      cal.set(Calendar.DAY_OF_MONTH,31);
      d = cal.getTime();
    }

    // Jetzt noch auf das Ende des Tages setzen
    d = DateUtil.endOfDay(d);
    setEnde(d);
    return d;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setEnde(java.util.Date)
   */
  public void setEnde(Date ende) throws RemoteException
  {
    setAttribute("ende",ende);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#check(java.util.Date)
   */
  public boolean check(Date d) throws RemoteException
  {
    return GeschaeftsjahrUtil.within(this.getBeginn(),this.getEnde(),d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) this.getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    setAttribute("mandant_id",m);
  }
  

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String arg0) throws RemoteException
  {
    if ("mandant_id".equals(arg0))
      return Mandant.class;
    if ("kontenrahmen_id".equals(arg0))
      return Kontenrahmen.class;
    if ("vorjahr_id".equals(arg0))
      return Geschaeftsjahr.class;
    
    return super.getForeignObject(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("this".equals(arg0))
      return this;
    if ("name".equals(arg0))
      return Settings.DATEFORMAT.format(getBeginn()) + " - " + Settings.DATEFORMAT.format(getEnde());

    return super.getAttribute(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      if (getMandant() == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Mandanten aus"));

      if (getKontenrahmen() == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Kontenrahmen aus."));

      Date beginn = getBeginn();
      Date ende = getEnde();
      
      if (!beginn.before(ende))
        throw new ApplicationException(i18n.tr("Ende des Geschäftsjahres muss sich nach dessen Beginn befinden"));
      
      int monate = getMonate();
      if (monate < 1 || monate > 12)
        throw new ApplicationException(i18n.tr("Geschäftsjahr darf nicht weniger als 1 und nicht mehr als 12 Monaten lang sein"));
      
      DBIterator existing = getService().createList(Geschaeftsjahr.class);
      existing.addFilter("mandant_id = " + this.getMandant().getID());
      while (existing.hasNext())
      {
        Geschaeftsjahr other = (Geschaeftsjahr) existing.next();
        if ((other.check(beginn) ||  other.check(ende)) && !other.equals(this))
          throw new ApplicationException(i18n.tr("Geschäftsjahr überschneidet sich mit dem existierenden Jahr: {0}",other.getAttribute(other.getPrimaryAttribute()).toString()));
      }
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking geschaeftsjahr",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Geschäftsjahres"));
    }
    super.insertCheck();
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    insertCheck();
    try
    {
      Geschaeftsjahr vorjahr = getVorjahr();
      if (vorjahr != null && vorjahr.equals(this))
        throw new ApplicationException(i18n.tr("Geschäftsjahr darf nicht auf sich selbst verweisen"));

      // Das Hilfsbuchungen nicht ohne Hauptbuchungen existieren koennen, brauchen
      // wir nur schauen, ob Hauptbuchungen existieren.
      GenericIterator list = getHauptBuchungen();

      if (list.size() > 0)
      {
        if (hasChanged("kontenrahmen_id"))
          throw new ApplicationException(i18n.tr("Wechsel des Kontenrahmens nicht mehr möglich, da bereits Buchungen vorliegen"));
        
        if (hasChanged("mandant_id"))
          throw new ApplicationException(i18n.tr("Wechsel des Mandanten nicht mehr möglich, da bereits Buchungen vorliegen"));

// Das schlaegt ggf. fehl, da die beiden Getter getBeginn und getEnde die Uhrzeit aendern
//        if (hasChanged("beginn"))
//          throw new ApplicationException(i18n.tr("Beginn des Geschäftsjahres kann nicht mehr geändert werden, da bereits Buchungen vorliegen"));
//
//        if (hasChanged("ende"))
//          throw new ApplicationException(i18n.tr("Ende des Geschäftsjahres kann nicht mehr geändert werden, da bereits Buchungen vorliegen"));
      }
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking gj",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Pflichtfelder."));
    }
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setKontenrahmen(de.willuhn.jameica.fibu.rmi.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException
  {
    setAttribute("kontenrahmen_id",kontenrahmen);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getAttribute("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getHauptBuchungen()
   */
  public DBIterator getHauptBuchungen() throws RemoteException
  {
    return this.getHauptBuchungen(null,null,false);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getHauptBuchungen(boolean)
   */
  public DBIterator getHauptBuchungen(boolean splitHauptbuchungen) throws RemoteException
  {
    return this.getHauptBuchungen(null,null,splitHauptbuchungen);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getHauptBuchungen(java.util.Date, java.util.Date)
   */
  @Override
  public DBIterator getHauptBuchungen(Date von, Date bis) throws RemoteException
  {
	  return this.getHauptBuchungen(von,bis,false);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getHauptBuchungen(java.util.Date, java.util.Date, boolean)
   */
  @Override
  public DBIterator getHauptBuchungen(Date von, Date bis, boolean splitHauptbuchungen) throws RemoteException
  {
    if (von != null && !this.check(von))
      throw new RemoteException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(von)));

    if (bis != null && !this.check(bis))
      throw new RemoteException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(bis)));

    Date start = null;
    if (von != null)
      start = DateUtil.startOfDay(von);

    Date end = null;
    if (bis != null)
      end = DateUtil.endOfDay(bis);

    DBService db = ((DBService)getService());
    DBIterator list = db.createList(Buchung.class);
    if (start != null)
      list.addFilter(db.getSQLTimestamp("datum") + " >= " + start.getTime());
    if (end != null)
      list.addFilter(db.getSQLTimestamp("datum") + " <=" + end.getTime());
    list.addFilter("geschaeftsjahr_id = " + this.getID());
    //Split-Hauptbuchungen rausfiltern
    if(!splitHauptbuchungen)
    	list.addFilter("id NOT IN (SELECT split_id FROM buchung WHERE split_id IS NOT NULL)");
    list.setOrder("order by datum,belegnummer");
    return list;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getAnfangsbestaende()
   */
  public DBIterator getAnfangsbestaende() throws RemoteException
  {
    DBIterator list = getService().createList(Anfangsbestand.class);
    list.addFilter("geschaeftsjahr_id = " + this.getID());
    return list;
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    try
    {
      
      Cache.clear(Geschaeftsjahr.class);
      
      Logger.info("Lösche Geschaeftsjahr " + getAttribute(getPrimaryAttribute()));

      transactionBegin();

      Logger.info("Lösche Abschreibungen");
      GenericIterator afa = getAbschreibungen();
      while (afa.hasNext())
      {
        Abschreibung a = (Abschreibung) afa.next();
        a.delete();
      }
      
      Logger.info("Lösche Buchungen");
      GenericIterator buchungen = getHauptBuchungen(true);
      //Erstmal die SplitBuchungen löschen
      while (buchungen.hasNext()) {
    	  Buchung b = (Buchung) buchungen.next();
    	  if(b.getSplitHauptBuchung() != null)
    		  b.delete();
      }
      //Jetzt die anderen buchungen inkl. der (ehemaligen) SplitHauptbuchungen
      buchungen = getHauptBuchungen(true);
      while (buchungen.hasNext())
      {
        // Wir muessen nur die Haupt-Buchungen loeschen.
        // Die wiederrum loeschen ihre Hilfsbuchungen dann
        // selbst
        Buchung b = (Buchung) buchungen.next();
        b.delete();
      }

      Logger.info("Lösche Anfangsbestände");
      GenericIterator ab = getAnfangsbestaende();
      while (ab.hasNext())
      {
        Anfangsbestand a = (Anfangsbestand) ab.next();
        a.delete();
      }
      
      // Die Abschreibungen werden beim Schliessen eines Geschaeftsjahres und
      // damit bei der automatischen Erstellung eines neuen Jahres erstellt.
      // Die Buchungen landen jedoch noch im alten Jahr. Wird nun diese Jahr
      // geloescht, muessen auch die die zugehoerigen Abschreibungen wieder
      // entfernt werden, damit das System wieder in dem Zustand ist wie
      // unmittelbar vorm Erstellen des zu loeschenden Geschaeftsjahres.
      Geschaeftsjahr vorjahr = getVorjahr();
      if (vorjahr != null)
      {
        Logger.info("Lösche Abschreibungen des Vorjahres");
        afa = vorjahr.getAbschreibungen();
        while (afa.hasNext())
        {
          Abschreibung a = (Abschreibung) afa.next();
          if (a.isSonderabschreibung())
            continue; // Sonderabschreibungen werden uebersprungen
          AbschreibungsBuchung b = a.getBuchung();
          a.delete();
          b.delete();
        }
        vorjahr.setClosed(false);
        vorjahr.store();
      }

      Geschaeftsjahr j = ((DBService) getService()).getActiveGeschaeftsjahr();
      boolean active = j != null && j.equals(this);
      super.delete();
      transactionCommit();
      if (active)
      {
        Logger.info("Entferne aktives Geschäftsjahr");
        Settings.setActiveGeschaeftsjahr(null);
      }
    }
    catch (ApplicationException e)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      throw e;
    }
    catch (RemoteException e2)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      throw e2;
    }
    catch (Throwable t)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      Logger.error("unable to delete jahr",t);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Geschäftsjahres"));
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#isClosed()
   */
  public boolean isClosed() throws RemoteException
  {
    Integer i = (Integer) getAttribute("closed");
    return i != null && i.intValue() == 1;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setClosed(boolean)
   */
  public void setClosed(boolean b) throws RemoteException
  {
    setAttribute("closed", new Integer(b ? 1 : 0));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getMonate()
   */
  public int getMonate() throws RemoteException
  {
    return GeschaeftsjahrUtil.getMonths(this.getBeginn(),this.getEnde());
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getAbschreibungen()
   */
  public GenericIterator getAbschreibungen() throws RemoteException
  {
    ArrayList l = new ArrayList();
    DBIterator list = getService().createList(Abschreibung.class);
    while (list.hasNext())
    {
      Abschreibung a = (Abschreibung) list.next();
      AbschreibungsBuchung b = a.getBuchung();
      if (b == null)
        continue;
      if (this.equals(b.getGeschaeftsjahr()))
        l.add(a);
    }
    return PseudoIterator.fromArray((Abschreibung[]) l.toArray(new Abschreibung[l.size()]));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getVorjahr()
   */
  public Geschaeftsjahr getVorjahr() throws RemoteException
  {
    return (Geschaeftsjahr) getAttribute("vorjahr_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setVorjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setVorjahr(Geschaeftsjahr vorjahr) throws RemoteException
  {
    setAttribute("vorjahr_id",vorjahr);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getBetriebsergebnis()
   */
  public Betriebsergebnis getBetriebsergebnis() throws RemoteException
  {
    return new BetriebsergebnisImpl(this);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getBetriebsergebnis(java.util.Date, java.util.Date)
   */
  @Override
  public Betriebsergebnis getBetriebsergebnis(Date start, Date end) throws RemoteException
  {
    return new BetriebsergebnisImpl(this,start,end);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getBetriebsergebnisseMonatlich()
   */
  public Map<String, Betriebsergebnis> getBetriebsergebnisseMonatlich() throws RemoteException
  {
    final DateFormat df = new SimpleDateFormat("MMMM");
    final Calendar cal  = Calendar.getInstance();

    Map<String, Betriebsergebnis> result = new LinkedHashMap<String, Betriebsergebnis>();
    Date start = this.getBeginn();
    Date end   = this.getEnde();
    
    // Wir iterieren monatsweise ueber das Geschaeftsjahr und erzeugen jeweils
    // ein Betriebsergebnis fuer diesen Zeitraum
    while (start != null && start.before(end))
    {
      // Monatsletzten ermitteln
      cal.setTime(start);
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
      
      result.put(df.format(start),new BetriebsergebnisImpl(this, start, DateUtil.endOfDay(cal.getTime())));
      
      // Naechsten Monatsersten ermitteln
      cal.setTime(start);
      cal.add(Calendar.MONTH, 1);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      
      // Fuer die naechste Iteration
      start = DateUtil.startOfDay(cal.getTime());
    }
    return result;
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    
    Cache.clear(Geschaeftsjahr.class);
    
    // Falls wir das aktuelle Geschaeftsjahr sind, laden wir es neu
    Settings.reloadActiveGeschaeftsjahr();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    // Geschaeftsjahre duerfen nur sequentiell geloescht werden.
    // Wir pruefen daher, ob es ein Folge-Jahre gibt.
    try
    {
      Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
      if (current != null)
      {
        if (current.equals(this))
          throw new ApplicationException(i18n.tr("Aktives Geschäftsjahr kann nicht gelöscht werden. Bitte aktivieren Sie zuerst ein anderes Jahr."));
      }

      DBIterator list = getService().createList(Geschaeftsjahr.class);
      while (list.hasNext())
      {
        Geschaeftsjahr j = (Geschaeftsjahr) list.next();
        Geschaeftsjahr vorjahr = j.getVorjahr();
        if (vorjahr == null)
          continue;
        if (vorjahr.equals(this))
          throw new ApplicationException(i18n.tr("Geschäftsjahr besitzt bereits ein Folgejahr. Löschen Sie zunächst dieses"));
      }
    }
    catch (RemoteException e)
    {
      Logger.error("unable to check jahr",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Geschäftsjahres"));
    }
    
    super.deleteCheck();
  }

}
