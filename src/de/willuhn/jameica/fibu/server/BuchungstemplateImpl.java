/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/BuchungstemplateImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2008/02/22 10:41:41 $
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

import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Buchungs-Templates.
 */
public class BuchungstemplateImpl extends AbstractTransferImpl implements Buchungstemplate
{
  /**
   * ct.
   * @throws RemoteException
   */
  public BuchungstemplateImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "buchungstemplate";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      if (getName() == null || getName().length() == 0)
        throw new ApplicationException(i18n.tr("Bitten geben Sie eine Bezeichnung an."));

      Kontenrahmen kr = getKontenrahmen();
      
      if (kr == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Kontenrahmen aus."));

      if (getBetrag() == 0.0d)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Buchungsbetrag ungleich 0 ein."));

      Konto soll = getSollKonto();
      Konto haben = getHabenKonto();
      
      if (soll == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto für die Soll-Buchung ein."));

      if (haben == null)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Konto für die Haben-Buchung ein."));

      if (soll.equals(haben))
        throw new ApplicationException(i18n.tr("Soll- und Haben-Konto dürfen nicht identisch sein."));
    
    }
    catch (RemoteException e)
    {
      Logger.error("unable to check template",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Vorlage"));
    }
    super.insertCheck();
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    insertCheck();
  }
  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.KontenrahmenObject#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getAttribute("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.KontenrahmenObject#setKontenrahmen(de.willuhn.jameica.fibu.rmi.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen kr) throws RemoteException
  {
    setAttribute("kontenrahmen_id",kr);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("kontenrahmen_id".equals(field))
      return Kontenrahmen.class;
    
    return super.getForeignObject(field);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getHabenKonto()
   */
  public Konto getHabenKonto() throws RemoteException
  {
    Kontenrahmen kr = getKontenrahmen();
    return kr == null ? null : kr.findByKontonummer((String)getAttribute("habenkonto"));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#getSollKonto()
   */
  public Konto getSollKonto() throws RemoteException
  {
    Kontenrahmen kr = getKontenrahmen();
    return kr == null ? null : kr.findByKontonummer((String)getAttribute("sollkonto"));
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setHabenKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setHabenKonto(Konto k) throws RemoteException
  {
    setAttribute("habenkonto",k == null ? null : k.getKontonummer());
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Transfer#setSollKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setSollKonto(Konto k) throws RemoteException
  {
    setAttribute("sollkonto",k == null ? null : k.getKontonummer());
  }
}


/*********************************************************************
 * $Log: BuchungstemplateImpl.java,v $
 * Revision 1.3  2008/02/22 10:41:41  willuhn
 * @N Erweiterte Mandantenfaehigkeit (IN PROGRESS!)
 *
 * Revision 1.2  2006/01/03 17:55:53  willuhn
 * @N a lot more checks
 * @B NPEs
 * @N BuchungsTemplates pro Mandant/Kontenrahmen
 * @N Default-Geschaeftsjahr in init.sql verschoben
 * @N Handling von Eingabe von Altbestaenden im AV
 *
 * Revision 1.1  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 **********************************************************************/