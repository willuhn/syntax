/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/AnfangsbestandImpl.java,v $
 * $Revision: 1.10 $
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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
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
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    
    try
    {
      if (Settings.getActiveGeschaeftsjahr().isClosed())
        throw new ApplicationException(i18n.tr("Geschäftsjahr ist bereits geschlossen"));

      Konto k = getKonto();
      Geschaeftsjahr jahr = getGeschaeftsjahr();
      
      if (jahr == null || jahr.isNewObject())
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Geschäftsjahr aus"));
      
      if (k == null || k.isNewObject())
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Konto aus"));
      
      if (getBetrag() == 0.0d)
          throw new ApplicationException(i18n.tr("Bitte geben Sie einen Anfangsbestand ein, der nicht 0 ist"));
        
      if (k.getAnfangsbestand(jahr) != null)
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
    Double d = (Double) getAttribute("betrag");
    return d == null ? 0.0d : d.doubleValue();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Anfangsbestand#setBetrag(double)
   */
  public void setBetrag(double betrag) throws RemoteException
  {
    setAttribute("betrag",new Double(betrag));
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


/*********************************************************************
 * $Log: AnfangsbestandImpl.java,v $
 * Revision 1.10  2005/09/26 23:51:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.8  2005/08/30 22:51:31  willuhn
 * @B bugfixing
 *
 * Revision 1.7  2005/08/29 17:46:14  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.6  2005/08/29 14:54:28  willuhn
 * @B bugfixing
 *
 * Revision 1.5  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.4  2005/08/28 01:08:03  willuhn
 * @N buchungsjournal
 *
 * Revision 1.3  2005/08/22 23:13:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/22 21:44:08  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.1  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 **********************************************************************/