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
import java.util.ArrayList;
import java.util.Date;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung eines Anlagevermoegen-Postens.
 */
public class AnlagevermoegenImpl extends AbstractDBObject implements Anlagevermoegen
{

  private transient I18N i18n = null;
  private Boolean canChange = null;
  
  /**
   * @throws java.rmi.RemoteException
   */
  public AnlagevermoegenImpl() throws RemoteException
  {
    super();
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "anlagevermoegen";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAnschaffungskosten()
   */
  public double getAnschaffungskosten() throws RemoteException
  {
    Double d = (Double) getAttribute("anschaffungskosten");
    return d == null ? 0.0d : d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setAnschaffungskosten(double)
   */
  public void setAnschaffungskosten(double kosten) throws RemoteException
  {
    setAttribute("anschaffungskosten",new Double(kosten));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAnschaffungsdatum()
   */
  public Date getAnschaffungsdatum() throws RemoteException
  {
    return (Date) getAttribute("anschaffungsdatum");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setAnschaffungsDatum(java.util.Date)
   */
  public void setAnschaffungsDatum(Date d) throws RemoteException
  {
    setAttribute("anschaffungsdatum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getBuchung()
   */
  public Buchung getBuchung() throws RemoteException
  {
    return (Buchung) getAttribute("buchung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setBuchung(de.willuhn.jameica.fibu.rmi.Buchung)
   */
  public void setBuchung(Buchung buchung) throws RemoteException
  {
    setAttribute("buchung_id",buchung);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getNutzungsdauer()
   */
  public int getNutzungsdauer() throws RemoteException
  {
    Integer dauer = (Integer) getAttribute("nutzungsdauer");
    return dauer == null ? 0 : dauer.intValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setNutzungsdauer(int)
   */
  public void setNutzungsdauer(int dauer) throws RemoteException
  {
    setAttribute("nutzungsdauer",new Integer(dauer));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getRestwert(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getRestwert(Geschaeftsjahr jahr) throws RemoteException
  {
    GenericIterator abschreibungen = getAbschreibungen(jahr);

    // Falls ein Restwert existiert, nehmen wir den als Ausgangsbasis
    Double r = (Double) super.getAttribute("restwert");
    
    double restwert = r != null ? r.doubleValue() : getAnschaffungskosten();
    while (abschreibungen.hasNext())
    {
      Abschreibung a = (Abschreibung) abschreibungen.next();
      AbschreibungsBuchung b = a.getBuchung();
      restwert -= b.getBetrag();
    }
    return restwert;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant mandant) throws RemoteException
  {
    setAttribute("mandant_id",mandant);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String arg0) throws RemoteException
  {
    if ("mandant_id".equals(arg0))
      return Mandant.class;
    if ("buchung_id".equals(arg0))
      return Buchung.class;
    if ("konto_id".equals(arg0))
      return Konto.class;
    if ("k_abschreibung_id".equals(arg0))
      return Konto.class;
    
    return super.getForeignObject(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAbschreibungen(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public GenericIterator getAbschreibungen(Geschaeftsjahr jahr) throws RemoteException
  {
    DBIterator list = getService().createList(Abschreibung.class);
    list.addFilter("av_id = " + getID());
    list.setOrder("order by id");
    ArrayList l = new ArrayList();
    Date end = jahr.getEnde();
    while (list.hasNext())
    {
      Abschreibung a = (Abschreibung) list.next();
      AbschreibungsBuchung b = a.getBuchung();
      Date d = b.getDatum();
      if (d.before(end) || d.equals(end))
        l.add(a);
    }
    return PseudoIterator.fromArray((Abschreibung[]) l.toArray(new Abschreibung[l.size()]));
  }
  
  /**
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    try
    {
      transactionBegin();

      DBIterator abschreibungen = getService().createList(Abschreibung.class);
      abschreibungen.addFilter("av_id = " + getID());
      while (abschreibungen.hasNext())
      {
        Abschreibung a = (Abschreibung) abschreibungen.next();
        AbschreibungsBuchung b = a.getBuchung();
        a.delete();
        if (b != null)
          b.delete();
      }
      
      super.delete();
      
      transactionCommit();
    }
    catch (Exception e)
    {
      try
      {
        transactionRollback();
      }
      catch (Throwable tr)
      {
        Logger.error("unable to rollback transaction",tr);
      }
      
      if (e instanceof ApplicationException)
        throw (ApplicationException) e;
      
      Logger.error("unable to delete av",e);
      throw new ApplicationException(i18n.tr("Fehler beim L�schen des Anlage-Gegenstandes"),e);
    }
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      Date datum = getAnschaffungsdatum();
      
      Konto bestand = getKonto();
      
      if (bestand == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Bestandskonto an"));
      
      Kontoart ka = bestand.getKontoArt();
      if (ka.getKontoArt() != Kontoart.KONTOART_ANLAGE)
        throw new ApplicationException(i18n.tr("Das ausgew�hlte Bestandskonto ist kein Anlagekonto"));

      Konto k = getAbschreibungskonto();
      if (k == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Aufwandskonto an, auf dem die Abschreibungen gebucht werden"));

      ka = k.getKontoArt();
      if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
        throw new ApplicationException(i18n.tr("Das ausgew�hlte Abschreibungskonto ist kein Aufwandskonto"));

      if (datum == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Anschaffungsdatum an"));
      if (getAnschaffungskosten() <= 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen g�ltigen Betrag f�r die Anschaffungskosten an"));
      if (getNutzungsdauer() <= 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine g�ltige Nutzunsdauer (in Jahren) zur Abschreibung an"));
      if (getNutzungsdauer() > 99)
        throw new ApplicationException(i18n.tr("Nutzunsdauer zu gross (max. 99 Jahre)"));
      if (getMandant() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Mandanten an"));
      if (getName() == null || getName().length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Bezeichnung ein"));

      // Anlagevermoegen darf nicht in der Zukunft angeschafft werden.
      Geschaeftsjahr jahr = ((DBService) getService()).getActiveGeschaeftsjahr();
      if (datum.after(jahr.getEnde()))
        throw new ApplicationException(i18n.tr("Anschaffungsdatum darf sich nicht hinter dem aktuellen Gesch�fsjahr befinden"));

      if (super.getAttribute("restwert") != null && !datum.before(jahr.getBeginn()))
        throw new ApplicationException(i18n.tr("Restwert darf nur dann vorgegeben werden, wenn der Anlage-Gegenstand vor dem aktuellen Jahr angeschafft wurde."));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking av",e);
      throw new ApplicationException(i18n.tr("Fehler beim Pr�fen des Anlage-Gegenstandes"));
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    // Beim Status-Update ist das zulaessig
    if (statusUpdate)
      return;
    
    // Zuerst pruefen wir, ob ueberhaupt was geaendert werden darf
    try
    {
      if (!canChange())
        throw new ApplicationException(i18n.tr("Anlage-Gegenstand darf nicht mehr ge�ndert werden, wenn bereits Abschreibungen vorliegen"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking av",e);
      throw new ApplicationException(i18n.tr("Fehler beim Pr�fen des Anlage-Gegenstandes"));
    }

    // und wenn wir hier angekommen sind, machen wir noch die regulaeren Checks
    insertCheck();
  }
  
  private boolean statusUpdate = false;

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#updateStatus(int)
   */
  public void updateStatus(int status) throws RemoteException, ApplicationException
  {
    if (this.hasChanged()) // Wenn der Status geaendert wird, darf hier nichts anderes mehr geaendert werden
      throw new ApplicationException(i18n.tr("Status-�nderung nicht mehr m�glich"));
    try
    {
      this.statusUpdate = true;
      this.setStatus(status);
      this.store();
    }
    finally
    {
      this.statusUpdate = false;
    }
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("restwert".equals(arg0))
    {
      Geschaeftsjahr jahr = ((DBService)getService()).getActiveGeschaeftsjahr();
      return new Double(getRestwert(jahr));
    }
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#canChange()
   */
  public boolean canChange() throws RemoteException
  {
    if (canChange == null)
    {
      DBIterator list = getService().createList(Abschreibung.class);
      list.addFilter("av_id = " + getID());
      this.canChange = Boolean.valueOf(!list.hasNext());
    }
    return canChange.booleanValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getAttribute("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setKonto(Konto k) throws RemoteException
  {
    setAttribute("konto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAbschreibungskonto()
   */
  public Konto getAbschreibungskonto() throws RemoteException
  {
    return (Konto) getAttribute("k_abschreibung_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setAbschreibungskonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setAbschreibungskonto(Konto k) throws RemoteException
  {
    setAttribute("k_abschreibung_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getJahresAbschreibung(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getJahresAbschreibung(Geschaeftsjahr jahr) throws RemoteException
  {
    return getJahresAbschreibung(jahr,false);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAnfangsbestand(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getAnfangsbestand(Geschaeftsjahr jahr) throws RemoteException
  {
    return getRestwert(jahr) + getJahresAbschreibung(jahr);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setRestwert(double)
   */
  public void setRestwert(double restwert) throws RemoteException
  {
    if (canChange())
      setAttribute("restwert", new Double(restwert));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getJahresSonderAbschreibung(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public double getJahresSonderAbschreibung(Geschaeftsjahr jahr) throws RemoteException
  {
    return getJahresAbschreibung(jahr,true);
  }

  /**
   * Hilfsmethode zum Ausrechnen der Abschreibungen.
   * @param jahr
   * @param nurSonder true, wenn nur die Summe der Sonderabschreibungen ermittelt werden soll. 
   * @return die Summe der Abschreibungen.
   * @throws RemoteException
   */
  private double getJahresAbschreibung(Geschaeftsjahr jahr, boolean nurSonder) throws RemoteException
  {
    DBIterator list = getService().createList(Abschreibung.class);
    list.addFilter("av_id = " + getID());
    if (nurSonder) list.addFilter("sonderabschreibung = 1");
    list.setOrder("order by id");
    double sum = 0.0d;
    while (list.hasNext())
    {
      Abschreibung a = (Abschreibung) list.next();
      AbschreibungsBuchung b = a.getBuchung();
      Geschaeftsjahr j = b.getGeschaeftsjahr();
      if (j.equals(jahr))
        sum += b.getBetrag();
    }
    return sum;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getStatus()
   */
  public int getStatus() throws RemoteException
  {
    Integer status = (Integer) this.getAttribute("status");
    return status == null ? STATUS_BESTAND : status.intValue(); // wenn nichts angegeben ist, stehts noch im Bestand
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setStatus(int)
   */
  public void setStatus(int status) throws RemoteException
  {
    this.setAttribute("status",new Integer(status));
  }

}


/*********************************************************************
 * $Log: AnlagevermoegenImpl.java,v $
 * Revision 1.21  2012/01/27 23:14:55  willuhn
 * cleanup
 *
 * Revision 1.20  2010/12/20 12:58:22  willuhn
 * @N BUGZILLA 958
 *
 * Revision 1.19  2010-09-20 10:27:36  willuhn
 * @N Neuer Status fuer Anlagevermoegen - damit kann ein Anlagegut auch dann noch in der Auswertung erscheinen, wenn es zwar abgeschrieben ist aber sich noch im Bestand befindet. Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=69910#69910
 *
 * Revision 1.18  2006-05-29 13:02:30  willuhn
 * @N Behandlung von Sonderabschreibungen
 *
 * Revision 1.17  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 * Revision 1.16  2006/01/08 15:28:41  willuhn
 * @N Loeschen von Sonderabschreibungen
 *
 * Revision 1.15  2006/01/04 17:59:11  willuhn
 * @B bug 171
 *
 * Revision 1.14  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.13  2006/01/03 23:58:35  willuhn
 * @N Afa- und GWG-Handling
 *
 * Revision 1.12  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.11  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.10  2005/10/06 15:15:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2005/09/27 17:41:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/05 13:14:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/09/01 23:28:15  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.4  2005/08/29 21:37:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/29 14:26:56  willuhn
 * @N Anlagevermoegen, Abschreibungen
 *
 * Revision 1.2  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.1  2005/08/29 00:20:29  willuhn
 * @N anlagevermoegen
 *
 **********************************************************************/