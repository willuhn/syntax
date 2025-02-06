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
import java.util.Date;

import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public abstract class AbstractBaseBuchungImpl extends AbstractTransferImpl implements BaseBuchung
{
  /**
   * Erzeugt eine neue BaseBuchung.
   * @throws RemoteException
   */
  public AbstractBaseBuchungImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "buchung";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "toString";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("toString".equals(arg0))
      return i18n.tr("Buchung Nr. {0}",Integer.toString(getBelegnummer()));
    
    return super.getAttribute(arg0);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getDatum()
   */
  public Date getDatum() throws RemoteException
  {
    return (Date) getAttribute("datum");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getBelegnummer()
   */
  public int getBelegnummer() throws RemoteException
  {
    Integer i = (Integer) getAttribute("belegnummer");
    if (i != null)
      return i.intValue();
    
    return createBelegnummer();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getGeschaeftsjahr()
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException
  {
    Integer i = (Integer) super.getAttribute("geschaeftsjahr_id");
    if (i == null)
      return null; // Noch kein Geschaeftsjahr zugeordnet
   
    Cache cache = Cache.get(Geschaeftsjahr.class,true);
    return (Geschaeftsjahr) cache.get(i);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setDatum(java.util.Date)
   */
  public void setDatum(Date d) throws RemoteException
  {
    setAttribute("datum",d);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    setAttribute("geschaeftsjahr_id",jahr == null || jahr.getID() == null ? null : Integer.valueOf(jahr.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setBelegnummer(int)
   */
  public void setBelegnummer(int belegnummer) throws RemoteException
  {
    setAttribute("belegnummer",Integer.valueOf(belegnummer));
  }

  /**
   * Erzeugt eine neue Beleg-Nummer.
   * @return Neue Belegnummer.
   * @throws RemoteException
   */
  private int createBelegnummer() throws RemoteException
  {
    Geschaeftsjahr jahr = getGeschaeftsjahr();
    if (jahr == null)
      return 1; // Kein Geschaeftsjahr, keine Buchungsnummer
    
      Number n = (Number) getService().execute("select max(belegnummer)+1 from buchung where geschaeftsjahr_id = " + jahr.getID(),new Object[0],new ResultSetExtractor() {
      public Object extract(ResultSet rs) throws RemoteException, SQLException
      {
        if (rs.next())
          return rs.getObject(1);
        return Integer.valueOf(1);
      }
    
    });
    return n == null ? 1 : n.intValue();
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    if (Settings.inUpdate())
      return;
    
    try {
      Geschaeftsjahr jahr = getGeschaeftsjahr();

      if (jahr == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Geschäftsjahr aus."));

      if (jahr.isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

      if (getBetrag() == 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Buchungsbetrag ungleich 0 ein."));

      String text = getText();
      if (text == null || text.length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Text ein"));

      Konto soll = getSollKonto();
      Konto haben = getHabenKonto();
      
      if (soll == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto für die Soll-Buchung ein."));

      if (haben == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto für die Haben-Buchung ein."));

      if (soll.equals(haben))
        throw new ApplicationException(i18n.tr("Soll- und Haben-Konto dürfen nicht identisch sein."));
      
      double steuer = getSteuer();
      if (steuer > 0.0d && soll.getSteuer() != null && haben.getSteuer() != null)
        throw new ApplicationException(i18n.tr("Es wurde ein Steuersatz eingegeben, obwohl keine zu versteuernden Konten ausgewählt wurden"));
      
      Date d = getDatum();
      if (d == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Datum ein."));

      if (!jahr.check(d))
        throw new ApplicationException(i18n.tr("Datum befindet sich nicht innerhalb des aktuellen Geschäftsjahres."));

      // ich muss hier deshalb getAttribute() aufrufen, weil getBelegnummer automatisch eine erzeugt
      Integer i = (Integer) getAttribute("belegnummer");
      if (i == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Belegnummer ein."));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking buchung",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Buchung."),e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    super.delete();
    SaldenCache.remove(getSollKonto().getKontonummer());
    SaldenCache.remove(getHabenKonto().getKontonummer());
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    this.insertCheck();
  }

  /**
   * Ueberschrieben, um den Salden-Cache fuer die Konten zu loeschen.
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    SaldenCache.remove(getSollKonto().getKontonummer());
    SaldenCache.remove(getHabenKonto().getKontonummer());
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#getKommentar()
   */
  public String getKommentar() throws RemoteException
  {
   return (String) this.getAttribute("kommentar");
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.BaseBuchung#setKommentar(java.lang.String)
   */
  public void setKommentar(String kommentar) throws RemoteException
  {
    this.setAttribute("kommentar",kommentar);
  }
}
