/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/Attic/AbstractKontenrahmenObjectImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/02/26 19:13:23 $
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
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.KontenrahmenObject;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public abstract class AbstractKontenrahmenObjectImpl extends AbstractDBObject implements KontenrahmenObject
{
  transient I18N i18n = null;
  
  /**
   * Erzeugt ein neues User-Objekt.
   * @throws RemoteException
   */
  public AbstractKontenrahmenObjectImpl() throws RemoteException
  {
    super();
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    if ("kontenrahmen_id".equals(field))
      return Kontenrahmen.class;
    return null;
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
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException
  {
    setAttribute("kontenrahmen_id",kontenrahmen);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    try {
      Kontenrahmen kr = getKontenrahmen();
      if (kr == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Kontenrahmen aus."));
      
      if (kr.isSystemKontenrahmen())
        throw new ApplicationException(i18n.tr("System-Kontenrahmen darf nicht ausgewählt werden."));
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(i18n.tr("Fehler bei der Prüfung des Datensatzes."),e);
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
   * Hilfsmethode zum Laden des Kontos anhand der Kontonummer im Kontenrahmen.
   * @param kontonummer die Kontonummer
   * @return das Konto oder null.
   * @throws RemoteException
   */
  protected Konto findKonto(String kontonummer) throws RemoteException
  {
    if (kontonummer == null || kontonummer.length() == 0)
      return null;
    
    Kontenrahmen kr = getKontenrahmen();
    return kr == null ? null : kr.findByKontonummer(kontonummer);
  }

  
  
}
