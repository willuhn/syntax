/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/FinanzamtImpl.java,v $
 * $Revision: 1.10 $
 * $Date: 2005/08/08 22:54:16 $
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
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class FinanzamtImpl extends AbstractDBObject implements Finanzamt
{

  /**
   * Erzeugt ein neues Finanzamt.
   * @throws RemoteException
   */
  public FinanzamtImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "finanzamt";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getOrt()
   */
  public String getOrt() throws RemoteException
  {
    return (String) getAttribute("ort");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getPLZ()
   */
  public String getPLZ() throws RemoteException
  {
    return (String) getAttribute("plz");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getPostfach()
   */
  public String getPostfach() throws RemoteException
  {
    return (String) getAttribute("postfach");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#getStrasse()
   */
  public String getStrasse() throws RemoteException
  {
    return (String) getAttribute("strasse");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setOrt(java.lang.String)
   */
  public void setOrt(String ort) throws RemoteException
  {
    setAttribute("ort",ort);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setPLZ(java.lang.String)
   */
  public void setPLZ(String plz) throws RemoteException
  {
    setAttribute("plz",plz);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setPostfach(java.lang.String)
   */
  public void setPostfach(String postfach) throws RemoteException
  {
    setAttribute("postfach",postfach);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Finanzamt#setStrasse(java.lang.String)
   */
  public void setStrasse(String strasse) throws RemoteException
  {
    setAttribute("strasse",strasse);
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  public void deleteCheck() throws ApplicationException
  {
    // Wir checken ob das Finanzamt einem Mandanten zugewiesen ist.
    try {
      DBIterator list = Settings.getDBService().createList(Mandant.class);
      list.addFilter("finanzamt_id='" + getID() + "'");
      if (list.hasNext())
        throw new ApplicationException("Das Finanzamt ist einem Mandanten zugewiesen.\n" +
          "Bitte ändern oder löschen zu Sie zunächst den Mandanten.");
    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Prüfung auf eventuell vorhandene \n" +        "Abhängigkeiten zu existierenden Mandanten.");
    }
    
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    // erst mal das gleiche wie beim updateCheck() ;)
    insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {
      String name = getName();
      if (name == null || "".equals(name)) {
        throw new ApplicationException("Bitte geben Sie den Namen des Finanzamtes ein.");
      }
  
      String plz = getPLZ();
      if (plz == null || "".equals(plz)) {
        throw new ApplicationException("Bitte geben Sie die Postleitzahl des Finanzamtes ein.");
      }
  
      String ort = getOrt();
      if (ort == null || "".equals(ort)) {
        throw new ApplicationException("Bitte geben Sie den Ort des Finanzamtes ein.");
      }
  
      String strasse  = getStrasse();
      String postfach = getPostfach();
      if ((strasse == null || "".equals(strasse)) && (postfach == null || "".equals(postfach))) {
        throw new ApplicationException("Bitte geben Sie entweder Postfach oder die Strasse des Finanzamtes ein.");
      }
    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Prüfung der Pflichtfelder.",e);
    }
    super.insertCheck();
  }

}

/*********************************************************************
 * $Log: FinanzamtImpl.java,v $
 * Revision 1.10  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.9  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.5  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.4  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.3  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.1  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/