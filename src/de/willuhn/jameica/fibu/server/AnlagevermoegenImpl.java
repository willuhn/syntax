/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AnlagevermoegenImpl.java,v $
 * $Revision: 1.12 $
 * $Date: 2006/01/03 17:55:53 $
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

    // Wenn keine Abschreibungen existieren, tolerieren wir den Wert
    // des Attributes "restwert". Der darf vom Benutzer eingegeben werden,
    // wenn es sich um migriertes Anlagegut handelt.
    Double r = (Double) getAttribute("myrestwert");
    
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
      Logger.error("unable to delete av",t);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Anlage-Gegenstandes"));
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
      
      if (getKonto() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Bestandskonto an"));
      if (getAbschreibungskonto() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Aufwandskonto an, auf dem die Abschreibungen gebucht werden"));
      if (datum == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Anschaffungsdatum an"));
      if (getAnschaffungskosten() == 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Betrag für die Anschaffungskosten an"));
      if (getNutzungsdauer() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Nutzunsdauer (in Jahren) zur Abschreibung an"));
      if (getNutzungsdauer() > 99)
        throw new ApplicationException(i18n.tr("Nutzunsdauer zu gross (max. 99 Jahre)"));
      if (getMandant() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Mandanten an"));
      if (getName() == null || getName().length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Bezeichnung ein"));

      // Anlagevermoegen darf nicht in der Zukunft angeschafft werden.
      Geschaeftsjahr jahr = ((DBService) getService()).getActiveGeschaeftsjahr();
      Date end = jahr.getEnde();
      if (datum.after(end))
        throw new ApplicationException(i18n.tr("Anschaffungsdatum darf sich nicht hinter dem aktuellen Geschäfsjahr befinden"));

      // In der Vergangenheit jedoch schon. Und zwar dann, wenn bereits
      // existierendes Anlagevermoegen aus einer anderen Fibu-Anwendung
      // uebernommen wird, fuer das bereits Abschreibungen existieren.
      Date start = jahr.getBeginn();

      // Anschaffungsdatum liegt nicht vorm aktuellen Jahr oder
      // das Anlagevermoegen darf nicht mehr geaendert werden.
      // den manuell eingegebenen Restwert.
      if (!datum.before(start) || !canChange())
        setAttribute("restwert",null);
    
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking av",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Anlage-Gegenstandes"));
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    insertCheck();
    
    // TODO GWGs muessen via Checkbox manuell auswaehlbar sein, da nicht
    // alles, was weniger als 400 EUR gekostet hat, automatisch ein GWG ist!!
    
    // Jetzt muessen wir noch pruefen, ob abschreibungsrelevante Daten geaendert wurden
    try
    {
      if (!canChange())
      {
        if (hasChanged("anschaffungsdatum"))
          throw new ApplicationException(i18n.tr("Anschaffungsdatum darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));
        
        if (hasChanged("anschaffungskosten"))
          throw new ApplicationException(i18n.tr("Anschaffungskosten dürfen nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

        if (hasChanged("nutzungsdauer"))
          throw new ApplicationException(i18n.tr("Nutzungsdauer darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

        if (hasChanged("mandant_id"))
          throw new ApplicationException(i18n.tr("Mandant darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

        if (hasChanged("k_abschreibung_id"))
          throw new ApplicationException(i18n.tr("Abschreibungskonto darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

        if (hasChanged("konto_id"))
          throw new ApplicationException(i18n.tr("Bestandskonto darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

      }
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking av",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Anlage-Gegenstandes"));
    }
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    // Erzeugt sonst eine Endlosscleife
    if ("myrestwert".equals(arg0))
      return super.getAttribute("restwert");
    
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
    DBIterator list = getService().createList(Abschreibung.class);
    list.addFilter("av_id = " + getID());
    list.setOrder("order by id");
    while (list.hasNext())
    {
      Abschreibung a = (Abschreibung) list.next();
      AbschreibungsBuchung b = a.getBuchung();
      Geschaeftsjahr j = b.getGeschaeftsjahr();
      if (j.equals(jahr))
        return b.getBetrag();
    }
    return 0.0d;
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
}


/*********************************************************************
 * $Log: AnlagevermoegenImpl.java,v $
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