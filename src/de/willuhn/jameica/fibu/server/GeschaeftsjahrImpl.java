/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/GeschaeftsjahrImpl.java,v $
 * $Revision: 1.13 $
 * $Date: 2005/09/26 23:51:59 $
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
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Betriebsergebnis;
import de.willuhn.jameica.fibu.rmi.Buchung;
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

  private I18N i18n       = null;
  private Mandant mandant = null;
  
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
    cal.set(Calendar.SECOND,1);

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
    return getBeginn().before(d) && getEnde().after(d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    if (this.mandant == null)
      this.mandant = (Mandant) getAttribute("mandant_id");
    return this.mandant;

  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Geschaeftsjahr#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    this.mandant = m;
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
    // Wir muessen pruefen, ob sich der Kontenrahmen geaendert hat und dies
    // verbieten, wenn wir schon Buchungen haben.
    try
    {
      Geschaeftsjahr vorjahr = getVorjahr();
      if (vorjahr != null && vorjahr.getID().equals(this.getID()))
        throw new ApplicationException(i18n.tr("Geschäftsjahr darf nicht auf sich selbst verweisen"));

      GenericIterator list = getBuchungen();

      if (list.size() > 0)
      {
        if (hasChanged("kontenrahmen_id"))
          throw new ApplicationException(i18n.tr("Wechsel des Kontenrahmens nicht mehr möglich, da bereits Buchungen vorliegen"));
        
        if (hasChanged("mandant_id"))
          throw new ApplicationException(i18n.tr("Wechsel des Mandanten nicht mehr möglich, da bereits Buchungen vorliegen"));

        if (hasChanged("beginn"))
          throw new ApplicationException(i18n.tr("Beginn des Geschäftsjahres kann nicht mehr geändert werden, da bereits Buchungen vorliegen"));

        if (hasChanged("ende"))
          throw new ApplicationException(i18n.tr("Ende des Geschäftsjahres kann nicht mehr geändert werden, da bereits Buchungen vorliegen"));
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
      
      Geschaeftsjahr vorjahr = getVorjahr();
      if (vorjahr != null)
      {
        Logger.info("Lösche Abschreibungen des Vorjahres");
        afa = vorjahr.getAbschreibungen();
        while (afa.hasNext())
        {
          Abschreibung a = (Abschreibung) afa.next();
          Buchung b = a.getBuchung();
          a.delete();
          b.delete();
        }
        vorjahr.setClosed(false);
        vorjahr.store();
      }
        
      super.delete();
      transactionCommit();
      Settings.setActiveGeschaeftsjahr(null);
    }
    catch (ApplicationException e)
    {
      transactionRollback();
      throw e;
    }
    catch (RemoteException e2)
    {
      transactionRollback();
      throw e2;
    }
    catch (Throwable t)
    {
      Logger.error("unable to delete mandant",t);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Mandanten"));
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
      Buchung b = a.getBuchung();
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
    if (!Application.inServerMode() && this.equals(Settings.getActiveGeschaeftsjahr()))
    {
      // Member aktualisieren
      Settings.setActiveGeschaeftsjahr(this);
    }
  }

}


/*********************************************************************
 * $Log: GeschaeftsjahrImpl.java,v $
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