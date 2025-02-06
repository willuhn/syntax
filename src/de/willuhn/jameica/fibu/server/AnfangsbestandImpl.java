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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung des Anfangsbestandes eines Kontos.
 */
public class AnfangsbestandImpl extends AbstractDBObject implements
    Anfangsbestand
{

  /**
   * @throws java.rmi.RemoteException
   */
  public AnfangsbestandImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#delete()
   */
  public void delete() throws RemoteException, ApplicationException
  {
    Konto k = getKonto();
    super.delete();
    if (k != null)
      SaldenCache.remove(k.getKontonummer());
  }
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "konto_ab";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    if (Settings.inUpdate())
      return;

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    updateCheck();
    try
    {
      Konto k = getKonto();
      if (k.getAnfangsbestand(getGeschaeftsjahr()) != null)
        throw new ApplicationException(i18n.tr("Für das Konto {0} existiert bereits ein Anfangsbestand",k.getKontonummer()));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking anfangsbestand",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Anfangsbestandes"));
    }
    super.insertCheck();
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  protected void updateCheck() throws ApplicationException
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      Geschaeftsjahr jahr = getGeschaeftsjahr();

      if (jahr == null || jahr.isNewObject())
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Geschäftsjahr aus"));

      if (jahr.isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

      Konto k = getKonto();
      
      if (k == null || k.isNewObject())
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Konto aus"));

      int ka = k.getKontoArt().getKontoArt();
      if (ka != Kontoart.KONTOART_ANLAGE && ka != Kontoart.KONTOART_GELD)
        throw new ApplicationException(i18n.tr("Nur Anlage- und Geldkonten dürfen einen Anfangsbestand haben"));

      // BUGZILLA 1153 - Bei Geldkonten tolerieren wir negative Anfangsbestaende
      if (ka == Kontoart.KONTOART_ANLAGE && this.getBetrag() < 0d)
        throw new ApplicationException(i18n.tr("Bei Anlagekonten muss der Anfangsbestand größer als 0 sein"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking anfangsbestand",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen des Anfangsbestandes"));
    }
    super.updateCheck();
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "konto_id";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#getGeschaeftsjahr()
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException
  {
    return (Geschaeftsjahr) getAttribute("geschaeftsjahr_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    setAttribute("geschaeftsjahr_id",jahr);
  }

  /**
   * @see de.willuhn.datasource.rmi.Changeable#store()
   */
  public void store() throws RemoteException, ApplicationException
  {
    super.store();
    SaldenCache.remove(getKonto().getKontonummer());
  }
  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getAttribute("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setKonto(Konto k) throws RemoteException
  {
    setAttribute("konto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#getBetrag()
   */
  public double getBetrag() throws RemoteException
  {
    Number d = (Number) getAttribute("betrag");
    return d == null ? 0.0d : d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setAttribute("betrag",Double.valueOf(betrag));
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String arg0) throws RemoteException
  {
    if ("geschaeftsjahr_id".equals(arg0))
      return Geschaeftsjahr.class;
    if ("konto_id".equals(arg0))
      return Konto.class;
    return super.getForeignObject(arg0);
  }
}
