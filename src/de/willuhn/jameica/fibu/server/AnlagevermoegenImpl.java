/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AnlagevermoegenImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/08/29 14:26:56 $
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
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Abschreibung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
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

  private I18N i18n = null;
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
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAnschaffungsDatum()
   */
  public Date getAnschaffungsDatum() throws RemoteException
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
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getLaufzeit()
   */
  public int getLaufzeit() throws RemoteException
  {
    Integer laufzeit = (Integer) getAttribute("laufzeit");
    return laufzeit == null ? 0 : laufzeit.intValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#setLaufzeit(int)
   */
  public void setLaufzeit(int laufzeit) throws RemoteException
  {
    setAttribute("laufzeit",new Integer(laufzeit));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getRestwert()
   */
  public double getRestwert() throws RemoteException
  {
    double restwert = getAnschaffungskosten();
    DBIterator abschreibungen = getAbschreibungen();
    while (abschreibungen.hasNext())
    {
      Abschreibung a = (Abschreibung) abschreibungen.next();
      Buchung b = a.getBuchung();
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
    
    return super.getForeignObject(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#getAbschreibungen()
   */
  public DBIterator getAbschreibungen() throws RemoteException
  {
    DBIterator list = getService().createList(Abschreibung.class);
    list.addFilter("av_id = " + getID());
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

      DBIterator abschreibungen = getAbschreibungen();
      while (abschreibungen.hasNext())
      {
        Abschreibung a = (Abschreibung) abschreibungen.next();
        Buchung b = a.getBuchung();
        if (b != null)
          b.delete();
        a.delete();
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
      if (getAnschaffungsDatum() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Anschaffungsdatum an"));
      if (getAnschaffungskosten() == 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Betrag für die Anschaffungskosten an"));
      if (getLaufzeit() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Laufzeit (in Jahren) zur Abschreibung an"));
      if (getMandant() == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Mandanten an"));
      if (getName() == null || getName().length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Bezeichnung ein"));
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
    
    // Jetzt muessen wir noch pruefen, ob abschreibungsrelevante Daten geaendert wurden
    try
    {
      if (!canChange())
      {
        if (hasChanged("anschaffungsdatum"))
          throw new ApplicationException(i18n.tr("Anschaffungsdatum darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));
        
        if (hasChanged("anschaffungskosten"))
          throw new ApplicationException(i18n.tr("Anschaffungskosten dürfen nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

        if (hasChanged("laufzeit"))
          throw new ApplicationException(i18n.tr("Laufzeit darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

        if (hasChanged("mandant_id"))
          throw new ApplicationException(i18n.tr("Mandant darf nicht mehr geändert werden, wenn bereits Abschreibungen vorliegen"));

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
    if ("restwert".equals(arg0))
      return new Double(getRestwert());
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anlagevermoegen#canChange()
   */
  public boolean canChange() throws RemoteException
  {
    if (canChange == null)
    {
      DBIterator list = getAbschreibungen();
      this.canChange = Boolean.valueOf(!list.hasNext());
    }
    return canChange.booleanValue();
  }
}


/*********************************************************************
 * $Log: AnlagevermoegenImpl.java,v $
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