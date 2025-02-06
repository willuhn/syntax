package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontozuordnung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Konto-Zuordnungen.
 */
public class KontozuordnungImpl extends AbstractTransferImpl implements Kontozuordnung
{

  /**
   * Erzeugt eine neue Kontozuordnung
   * @throws RemoteException
   */
  public KontozuordnungImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "kontozuordnung";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setName(java.lang.String)
   */
  public void setName(String name) throws RemoteException
  {
    setAttribute("name", name);

  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#getKonto()
   */
  public Konto getKonto() throws RemoteException
  {
    return (Konto) getAttribute("konto_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setKonto(Konto konto) throws RemoteException
  {
    setAttribute("konto_id", konto);

  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#getMandant()
   */
  public Mandant getMandant() throws RemoteException
  {
    return (Mandant) getAttribute("mandant_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setMandant(de.willuhn.jameica.fibu.rmi.Mandant)
   */
  public void setMandant(Mandant mandant) throws RemoteException
  {
    setAttribute("mandant_id", mandant);

  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#getHibiscusKontoId()
   */
  @Override
  public String getHibiscusKontoId() throws RemoteException
  {
    Integer id = (Integer) getAttribute("hb_konto_id");
    return id == null ? null : id.toString();
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setHibiscusKontoId(java.lang.String)
   */
  @Override
  public void setHibiscusKontoId(String id) throws RemoteException
  {
    try
    {
      setAttribute("hb_konto_id",id != null ? Integer.parseInt(id) : null);
    }
    catch (Exception e)
    {
      Logger.error("invalid hb_konto_id",e);
    }
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  protected void insertCheck() throws ApplicationException
  {
    if (Settings.inUpdate())
      return;

    try
    {
      if (getName() == null || getName().length() == 0)
        throw new ApplicationException(i18n.tr("Bitten geben Sie eine Bezeichnung an."));

      // Falls eine Konto-Kategorie zugeordnet ist, checken wir, ob
      // nicht schon eine andere Vorlage dieser zugeordnet ist.
      final String hid = this.getHibiscusKontoId();
      if (hid != null)
      {
        DBIterator list = this.getService().createList(Kontozuordnung.class);
        list.addFilter("hb_konto_id = ?",hid);
        list.addFilter("mandant_id = ?",this.getMandant().getID());
        if (!this.isNewObject())
          list.addFilter("id != ?,",this.getID()); // Natuerlich duerfen wir uns selbst nicht finden ;)
        
        if (list.hasNext())
        {
          final Kontozuordnung t = (Kontozuordnung) list.next();
          throw new ApplicationException(i18n.tr("Dem Hibiscus-Konto ist bereits \"{0}\" zugeordnet", t.getName()));
        }
      }
    } catch (RemoteException e)
    {
      Logger.error("unable to check template", e);
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
   * @see de.willuhn.jameica.fibu.server.AbstractUserObjectImpl#getForeignObject(java.lang.String)
   */
  protected Class getForeignObject(String field) throws RemoteException
  {
    if ("mandant_id".equals(field))
      return Mandant.class;
    if ("konto_id".equals(field))
      return Konto.class;
    return super.getForeignObject(field);
  }

}
