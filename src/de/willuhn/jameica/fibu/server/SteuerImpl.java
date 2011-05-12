/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/SteuerImpl.java,v $
 * $Revision: 1.20 $
 * $Date: 2011/05/12 09:10:31 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class SteuerImpl extends AbstractUserObjectImpl implements Steuer
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Erzeugt einen neuen Steuersatz.
   * @throws RemoteException
   */
  public SteuerImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "steuer";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getSatz()
   */
  public double getSatz() throws RemoteException
  {
    Double d = (Double) getAttribute("satz");
    if (d != null)
      return d.doubleValue();

    return 0;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name", name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setSatz(double)
   */
  public void setSatz(double satz) throws RemoteException
  {
    setAttribute("satz", new Double(satz));
  }

  /**
   * @see de.willuhn.jameica.fibu.server.AbstractUserObjectImpl#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    if ("steuerkonto_id".equals(field))
      return Konto.class;
    return super.getForeignObject(field);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    super.deleteCheck();
    try {

      // wir checken ob vielleicht ein Konto diesen Steuersatz besitzt.
      DBIterator list = Settings.getDBService().createList(Konto.class);
      list.addFilter("steuer_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Der Steuersatz ist einem Konto zugewiesen.\n" +
          "Bitte ändern oder löschen zu Sie zunächst das Konto."));
    }
    catch (RemoteException e)
    {
			Logger.error("error while checking dependencies",e);
      throw new ApplicationException(i18n.tr("Fehler beim Prüfen der Abhängigkeiten."));
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try {
      if (getName() == null || "".equals(getName()))
        throw new ApplicationException(i18n.tr("Bitte geben Sie eine Bezeichnung für den Steuersatz ein."));
      
      if (getSatz() < 0)
        throw new ApplicationException(i18n.tr("Steuersatz darf nicht kleiner als 0 sein."));
      
      if (getSteuerKonto() == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Steuer-Sammelkonto aus"));
    }
    catch (RemoteException e)
    {
      Logger.error("error while checking dependencies",e);
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung der Pflichtfelder."),e);
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getSteuerKonto()
   */
  public Konto getSteuerKonto() throws RemoteException
  {
    return (Konto) getAttribute("steuerkonto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setSteuerKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setSteuerKonto(Konto k) throws RemoteException
  {
    setAttribute("steuerkonto_id",k);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getUstNrSteuer()
   */
  public String getUstNrSteuer() throws RemoteException
  {
    return (String) getAttribute("ust_nr_steuer");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setUstNrSteuer(java.lang.String)
   */
  public void setUstNrSteuer(String s) throws RemoteException
  {
    setAttribute("ust_nr_steuer",s);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#getUstNrBemessung()
   */
  public String getUstNrBemessung() throws RemoteException
  {
    return (String) getAttribute("ust_nr_bemessung");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Steuer#setUstNrBemessung(java.lang.String)
   */
  public void setUstNrBemessung(String s) throws RemoteException
  {
    setAttribute("ust_nr_bemessung",s);
  }
  
  
}

/*********************************************************************
 * $Log: SteuerImpl.java,v $
 * Revision 1.20  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.19  2010-06-04 13:49:48  willuhn
 * @N Kennzeichen fuer Steuer und Bemessungsgrundlage fuer UST-Voranmeldung
 *
 * Revision 1.18  2010/06/04 13:34:45  willuhn
 * @B Da fehlten ein paar Commits
 *
 * Revision 1.17  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.16  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.14  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.13  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.12  2005/10/05 17:52:33  willuhn
 * @N steuer behaviour
 *
 * Revision 1.11  2005/08/22 16:37:22  willuhn
 * @N Anfangsbestaende
 *
 * Revision 1.10  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.9  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.8  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.7  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/