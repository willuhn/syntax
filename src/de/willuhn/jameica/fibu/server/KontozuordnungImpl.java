package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontozuordnung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class KontozuordnungImpl extends AbstractTransferImpl implements Kontozuordnung
{

  /**
   * Erzeugt eine neue Kontozuordnung
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
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setName()
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
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setKonto()
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
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setMandant()
   */
  public void setMandant(Mandant mandant) throws RemoteException
  {
    setAttribute("mandant_id", mandant);

  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#getHbKonto()
   */
  public de.willuhn.jameica.hbci.rmi.Konto getHbKonto() throws RemoteException
  {
    String s = null;
    if (getAttribute("hb_konto_id") != null)
      s = getAttribute("hb_konto_id").toString();
    return (de.willuhn.jameica.hbci.rmi.Konto) Settings.getDBService().createObject(de.willuhn.jameica.hbci.rmi.Konto.class, s);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontozuordnung#setHbKonto(de.willuhn.jameica.hbci.rmi.Konto)
   */
  public void setHbKonto(de.willuhn.jameica.hbci.rmi.Konto hbKonto_id) throws RemoteException
  {
    setAttribute("hb_konto_id", hbKonto_id.getID());
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

      // Falls eine Konto-Kategorie zugeordnet ist, checken wir, ob
      // nicht schon eine andere Vorlage dieser zugeordnet ist.
      if (this.getHbKonto() != null)
      {
        DBIterator list = this.getService().createList(Kontozuordnung.class);
        list.addFilter("hb_konto_id = ?", this.getHbKonto().getID());
        list.addFilter("mandant_id = ?", this.getMandant().getID());
        if (!this.isNewObject())
          list.addFilter("id != " + this.getID()); // Natuerlich duerfen wir uns selbst nicht finden ;)
        if (list.hasNext())
        {
          Kontozuordnung t = (Kontozuordnung) list.next();
          throw new ApplicationException(i18n.tr("Dem Hibiscus-Konto ist bereits die Vorlage \"{0}\" zugeordnet", t.getName()));
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
