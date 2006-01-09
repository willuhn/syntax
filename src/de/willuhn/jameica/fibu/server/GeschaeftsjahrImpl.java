/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/GeschaeftsjahrImpl.java,v $
 * $Revision: 1.23 $
 * $Date: 2006/01/09 01:40:31 $
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung eines Geschaeftsjahres.
 */
public class GeschaeftsjahrImpl extends AbstractDBObject implements Geschaeftsjahr
{

  private transient I18N i18n  = null;
  
  /**
   * @throws java.rmi.RemoteException
   */
  public GeschaeftsjahrImpl() throws RemoteException
  {
    super();
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

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

    Calendar cal = Calendar.getInstance();

    if (d == null)
    {
      // Wir erstellen automatisch ein neues, wenn keins existiert.
      Logger.info("no geschaeftsjahr start given, using current year");
      cal.set(Calendar.MONTH,Calendar.JANUARY);
      cal.set(Calendar.DAY_OF_MONTH,1);
    }
    else
    {
      cal.setTime(d);
    }

    // Jetzt noch auf den Anfang des Tages setzen.
    cal.set(Calendar.HOUR_OF_DAY,0);
    cal.set(Calendar.MINUTE,0);
    cal.set(Calendar.SECOND,0);

    d = cal.getTime();
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

    Calendar cal = Calendar.getInstance();

    if (d == null)
    {
      // Wir erstellen automatisch ein neues, wenn keins existiert.
      Logger.info("no geschaeftsjahr start given, using current year");
      cal.set(Calendar.MONTH,Calendar.DECEMBER);
      cal.set(Calendar.DAY_OF_MONTH,31);
    }
    else
    {
      cal.setTime(d);
    }

    // Jetzt noch auf das Ende des Tages setzen
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);

    d = cal.getTime();
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
    if (d == null)
      return false;
    
    Date ende   = getEnde();
    Date beginn = getBeginn();
    
    return ((d.after(beginn) || d.equals(beginn)) && // Nach oder identisch mit Beginn
            (d.before(ende)  || d.equals(ende)));    //  Vor oder identisch mit Ende
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
      return Fibu.DATEFORMAT.format(getBeginn()) + " - " + Fibu.DATEFORMAT.format(getEnde());

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

      GenericIterator list = getBuchungen();

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
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getBuchungen()
   */
  public DBIterator getBuchungen() throws RemoteException
  {
    DBIterator list = getService().createList(Buchung.class);
    list.addFilter("geschaeftsjahr_id = " + this.getID());
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
      GenericIterator buchungen = getBuchungen();
      while (buchungen.hasNext())
      {
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
    Date start = getBeginn();
    Date end   = getEnde();
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(start);
    int count = 0;
    while (true)
    {
      if (++count > 13)
        break;
      cal.add(Calendar.MONTH,1);
      Date test = cal.getTime();
      if (test.after(end))
        break;
    }
    return count;
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
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    
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


/*********************************************************************
 * $Log: GeschaeftsjahrImpl.java,v $
 * Revision 1.23  2006/01/09 01:40:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2006/01/08 15:28:41  willuhn
 * @N Loeschen von Sonderabschreibungen
 *
 * Revision 1.21  2006/01/06 11:25:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.20  2006/01/04 17:05:32  willuhn
 * @B bug 170
 *
 * Revision 1.19  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.18  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.17  2006/01/03 11:29:03  willuhn
 * @N Erzeugen der Abschreibungs-Buchung in eine separate Funktion ausgelagert
 *
 * Revision 1.16  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2005/09/30 17:12:06  willuhn
 * @B bug 122
 *
 * Revision 1.14  2005/09/27 17:41:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2005/09/05 15:00:43  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2005/09/04 23:40:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.8  2005/09/01 23:28:15  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.6  2005/08/30 22:33:45  willuhn
 * @B bugfixing
 *
 * Revision 1.5  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.4  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.2  2005/08/29 16:43:14  willuhn
 * @B bugfixing
 *
 * Revision 1.1  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/