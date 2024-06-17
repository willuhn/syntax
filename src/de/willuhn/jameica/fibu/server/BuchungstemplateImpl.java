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
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Mandant;
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
      
      // Falls eine Umsatz-Kategorie zugeordnet ist, checken wir, ob
      // nicht schon eine andere Vorlage dieser zugeordnet ist. Andernfalls
      // koennten wir spaeter bei der Uebernahme von Buchungen nicht mehr
      // eindeutig anhand der Kategorie erkennen, welche Vorlage zu verwenden ist.
      if (this.getHibiscusUmsatzTypID() != null)
      {
        DBIterator list = this.getService().createList(Buchungstemplate.class);
        list.addFilter("hb_umsatztyp_id = ?",new Object[]{this.getHibiscusUmsatzTypID()});
        if (!this.isNewObject())
          list.addFilter("id != " + this.getID()); // Natuerlich duerfen wir uns selbst nicht finden ;)
        if (list.hasNext())
        {
          Buchungstemplate t = (Buchungstemplate) list.next();
          throw new ApplicationException(i18n.tr("Der Umsatz-Kategorie ist bereits die Vorlage \"{0}\" zugeordnet",t.getName()));
        }
      }
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
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant m) throws RemoteException
  {
    setAttribute("mandant_id",m);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getAttribute("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#setKontenrahmen(de.willuhn.jameica.fibu.rmi.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen kr) throws RemoteException
  {
    setAttribute("kontenrahmen_id",kr);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.server.AbstractTransferImpl#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    if ("mandant_id".equals(field))
      return Mandant.class;
    if ("kontenrahmen_id".equals(field))
      return Kontenrahmen.class;
    
    return super.getForeignObject(field);
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#getHibiscusUmsatzTypID()
   */
  public String getHibiscusUmsatzTypID() throws RemoteException
  {
    String s = (String) getAttribute("hb_umsatztyp_id");
    return s == null ? null : s.trim();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Buchungstemplate#setHibiscusUmsatzTypID(java.lang.String)
   */
  public void setHibiscusUmsatzTypID(String id) throws RemoteException
  {
    setAttribute("hb_umsatztyp_id",id);
  }

}


/*********************************************************************
 * $Log: BuchungstemplateImpl.java,v $
 * Revision 1.7  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.6  2010-06-03 17:07:14  willuhn
 * @N Erste Version der vollautomatischen Uebernahme von Umsatzen in Hibiscus!
 *
 * Revision 1.5  2010/06/03 14:26:16  willuhn
 * @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
 * @C Code-Cleanup
 *
 * Revision 1.4  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
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