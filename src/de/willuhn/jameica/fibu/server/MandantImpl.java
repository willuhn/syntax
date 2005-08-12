/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/MandantImpl.java,v $
 * $Revision: 1.15 $
 * $Date: 2005/08/12 00:10:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by  bbv AG
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 * 24.11.2003
 */
public class MandantImpl extends AbstractDBObject implements Mandant
{

  /**
   * Erzeugt einen neuen Mandanten.
   * @throws RemoteException
   */
  public MandantImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "mandant";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "firma";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getAttribute("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getName1()
   */
  public String getName1() throws RemoteException
  {
    return (String) getAttribute("name1");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getName2()
   */
  public String getName2() throws RemoteException
  {
    return (String) getAttribute("name2");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getFirma()
   */
  public String getFirma() throws RemoteException
  {
    return (String) getAttribute("firma");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getStrasse()
   */
  public String getStrasse() throws RemoteException
  {
    return (String) getAttribute("strasse");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getPLZ()
   */
  public String getPLZ() throws RemoteException
  {
    return (String) getAttribute("plz");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getOrt()
   */
  public String getOrt() throws RemoteException
  {
    return (String) getAttribute("ort");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getSteuernummer()
   */
  public String getSteuernummer() throws RemoteException
  {
    return (String) getAttribute("steuernummer");
  }

  /**
   * Ueberschrieben, um ein synthetisches Attribut "geschaeftsjahr" zu erzeugen.
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0) throws RemoteException
  {
    if ("geschaeftsjahr".equals(arg0))
      return Fibu.DATEFORMAT.format(getGeschaeftsjahrVon()) + " - " + Fibu.DATEFORMAT.format(getGeschaeftsjahrBis());

    return super.getAttribute(arg0);
  }
  
  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getForeignObject(java.lang.String)
   */
  public Class getForeignObject(String field) throws RemoteException
  {
    if ("kontenrahmen_id".equals(field))
      return Kontenrahmen.class;
    if ("finanzamt_id".equals(field))
      return Finanzamt.class;
    return null;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setName1(java.lang.String)
   */
  public void setName1(String name1) throws RemoteException
  {
    setAttribute("name1",name1);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setName2(java.lang.String)
   */
  public void setName2(String name2) throws RemoteException
  {
    setAttribute("name2",name2);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setFirma(java.lang.String)
   */
  public void setFirma(String firma) throws RemoteException
  {
    setAttribute("firma",firma);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setStrasse(java.lang.String)
   */
  public void setStrasse(String strasse) throws RemoteException
  {
    setAttribute("strasse",strasse);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setPLZ(java.lang.String)
   */
  public void setPLZ(String plz) throws RemoteException
  {
    setAttribute("plz",plz);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setOrt(java.lang.String)
   */
  public void setOrt(String ort) throws RemoteException
  {
    setAttribute("ort",ort);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setSteuernummer(java.lang.String)
   */
  public void setSteuernummer(String steuernummer) throws RemoteException
  {
    setAttribute("steuernummer",steuernummer);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setKontenrahmen(de.willuhn.jameica.fibu.rmi.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException
  {
    setAttribute("kontenrahmen_id",kontenrahmen);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getFinanzamt()
   */
  public Finanzamt getFinanzamt() throws RemoteException
  {
    return (Finanzamt) getAttribute("finanzamt_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setFinanzamt(de.willuhn.jameica.fibu.rmi.Finanzamt)
   */
  public void setFinanzamt(Finanzamt finanzamt) throws RemoteException
  {
    setAttribute("finanzamt_id",finanzamt);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getGeschaeftsjahrVon()
   */
  public Date getGeschaeftsjahrVon() throws RemoteException
  {
    Date d = (Date) getAttribute("gj_von");

    if (d == null)
    {
      // Wir erstellen automatisch ein neues, wenn keins existiert.
      Logger.info("no geschaeftsjahr start given, using current year");
      d = new Date();
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    cal.set(Calendar.MONTH,Calendar.JANUARY);
    cal.set(Calendar.DAY_OF_MONTH,1);
    cal.set(Calendar.HOUR_OF_DAY,0);
    cal.set(Calendar.MINUTE,0);
    cal.set(Calendar.SECOND,1);
    d = cal.getTime();
    setGeschaeftsjahrVon(d);
    return d;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setGeschaeftsjahrVon(java.util.Date)
   */
  public void setGeschaeftsjahrVon(Date von) throws RemoteException
  {
    setAttribute("gj_von",von);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getGeschaeftsjahrBis()
   */
  public Date getGeschaeftsjahrBis() throws RemoteException
  {
    Date d = (Date) getAttribute("gj_bis");

    if (d == null)
    {
      // Wir erstellen automatisch ein neues, wenn keins existiert.
      Logger.info("no geschaeftsjahr start given, using current year");
      d = new Date();
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.set(Calendar.MONTH,Calendar.DECEMBER);
    cal.set(Calendar.DAY_OF_MONTH,31);
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);
    d = cal.getTime();
    setGeschaeftsjahrBis(d);
    return d;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setGeschaeftsjahrBis(java.util.Date)
   */
  public void setGeschaeftsjahrBis(Date bis) throws RemoteException
  {
    setAttribute("gj_bis",bis);
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    try {
      String firma = getFirma();
      if (firma == null || "".equals(firma))
        throw new ApplicationException("Bitte geben Sie die Firma ein.");
  
      String steuernummer = getSteuernummer();
      if (steuernummer == null || "".equals(steuernummer))
        throw new ApplicationException("Bitte geben Sie die Steuernummer ein.");
  
      if (getFinanzamt() == null)
        throw new ApplicationException("Bitte wählen Sie ein Finanzamt aus.");

      if (getKontenrahmen() == null)
        throw new ApplicationException("Bitte wählen Sie einen Kontenrahmen aus.");

      // Das rufen wir nur auf, damit die Daten automatisch gefuellt werden,
      // falls sie noch fehlen.
      getGeschaeftsjahrVon();
      getGeschaeftsjahrBis();
    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Prüfung der Pflichtfelder.",e);
    }
    super.insertCheck();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    insertCheck();
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#getWaehrung()
   */
  public String getWaehrung() throws RemoteException
  {
    String s = (String) getAttribute("waehrung");
    if (s != null && s.length() > 0)
      return s;
    return Settings.WAEHRUNG;
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#setWaehrung(java.lang.String)
   */
  public void setWaehrung(String waehrung) throws RemoteException
  {
    setAttribute("waehrung",waehrung);
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Mandant#checkGeschaeftsJahr(java.util.Date)
   */
  public boolean checkGeschaeftsJahr(Date d) throws RemoteException
  {
    return getGeschaeftsjahrVon().before(d) && getGeschaeftsjahrBis().after(d);
  }
}


/*********************************************************************
 * $Log: MandantImpl.java,v $
 * Revision 1.15  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.14  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.13  2005/08/09 23:53:34  willuhn
 * @N massive refactoring
 *
 * Revision 1.12  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.11  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.9  2003/12/12 21:11:27  willuhn
 * @N ObjectMetaCache
 *
 * Revision 1.8  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.7  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 * Revision 1.6  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.4  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
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