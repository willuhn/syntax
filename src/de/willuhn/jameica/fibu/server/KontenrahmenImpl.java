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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public class KontenrahmenImpl extends AbstractUserObjectImpl implements Kontenrahmen
{

  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * Erzeugt einen neuen Kontorahmen.
   * @throws RemoteException
   */
  public KontenrahmenImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "kontenrahmen";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    super.insertCheck();
    try {
      String name = (String) getAttribute("name");
      if (name == null || "".equals(name))
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Namen f�r den Kontenrahmen ein."));
      
      // Checken, ob schon ein gleichnamiger Kontenrahmen existiert
      DBIterator list = getService().createList(Kontenrahmen.class);
      list.addFilter("name = ?",name);
      if (!this.isNewObject())
        list.addFilter("id != " + this.getID()); // Und wir sind es nicht selbst
      
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Ein Kontenrahmen mit diesem Namen existiert bereits."));
    }
    catch (RemoteException e)
    {
      throw new ApplicationException(i18n.tr("Fehler bei der Pr�fung des Kontenrahmens."),e);
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
   */
  protected void deleteCheck() throws ApplicationException
  {
    super.deleteCheck();
    try {

      DBIterator list = Settings.getDBService().createList(Konto.class);
      list.addFilter("kontenrahmen_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Der Kontenrahmen enth�lt Konten. Bitte l�schen zu Sie zun�chst die Konten."));

      list = Settings.getDBService().createList(Geschaeftsjahr.class);
      list.addFilter("kontenrahmen_id = " + this.getID());
      if (list.hasNext())
        throw new ApplicationException(i18n.tr("Es existieren bereits Gesch�ftsjahre mit diesem Kontenrahmen."));

    }
    catch (RemoteException e)
    {
      Logger.error("error while checking dependencies",e);
      throw new ApplicationException(i18n.tr("Fehler beim Pr�fen der Abh�ngigkeiten."));
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#getKonten()
   */
  public DBIterator getKonten() throws RemoteException
  {
    DBIterator list = getService().createList(Konto.class);
    list.addFilter("(kontenrahmen_id = " + this.getID() + ")");
    list.setOrder("order by kontonummer");
    return list;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name",name);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontenrahmen#findByKontonummer(java.lang.String)
   */
  public Konto findByKontonummer(String kto) throws RemoteException
  {
    DBIterator konten = getKonten();
    konten.addFilter("kontonummer = ?",kto);
    return konten.hasNext() ? (Konto) konten.next() : null;
  }

}


/*********************************************************************
 * $Log: KontenrahmenImpl.java,v $
 * Revision 1.22  2011/08/08 10:44:36  willuhn
 * @C compiler warnings
 *
 * Revision 1.21  2011-03-25 10:14:10  willuhn
 * @N Loeschen von Mandanten und Beruecksichtigen der zugeordneten Konten und Kontenrahmen
 * @C BUGZILLA 958
 *
 * Revision 1.20  2011-03-21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 * Revision 1.19  2009-07-03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.16  2007/11/05 01:04:49  willuhn
 * @N Beim Speichern testen, ob fuer den Mandanten schon ein gleichnamiger Kontenrahmen existiert
 * @N findByKontonummer
 *
 * Revision 1.15  2007/10/08 22:54:47  willuhn
 * @N Kopieren eines kompletten Kontenrahmen auf einen Mandanten
 *
 * Revision 1.14  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.13  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.12  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.11  2005/09/01 21:08:41  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.9  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.6  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.5  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.3  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.2  2003/11/24 16:26:16  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.1  2003/11/24 15:18:21  willuhn
 * *** empty log message ***
 *
 *********************************************************************/