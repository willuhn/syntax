/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/MandantImpl.java,v $
 * $Revision: 1.9 $
 * $Date: 2003/12/12 21:11:27 $
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

import de.willuhn.jameica.Application;
import de.willuhn.jameica.ApplicationException;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.*;
import de.willuhn.jameica.server.AbstractDBObject;

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
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "mandant";
  }

  /**
   * @see de.willuhn.jameica.rmi.DBObject#getPrimaryField()
   */
  public String getPrimaryField() throws RemoteException
  {
    return "firma";
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getKontenrahmen()
   */
  public Kontenrahmen getKontenrahmen() throws RemoteException
  {
    return (Kontenrahmen) getField("kontenrahmen_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getName1()
   */
  public String getName1() throws RemoteException
  {
    return (String) getField("name1");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getName2()
   */
  public String getName2() throws RemoteException
  {
    return (String) getField("name2");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getFirma()
   */
  public String getFirma() throws RemoteException
  {
    return (String) getField("firma");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getStrasse()
   */
  public String getStrasse() throws RemoteException
  {
    return (String) getField("strasse");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getPLZ()
   */
  public String getPLZ() throws RemoteException
  {
    return (String) getField("plz");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getOrt()
   */
  public String getOrt() throws RemoteException
  {
    return (String) getField("ort");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getSteuernummer()
   */
  public String getSteuernummer() throws RemoteException
  {
    return (String) getField("steuernummer");
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#getForeignObject(java.lang.String)
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
   * @see de.willuhn.jameica.fibu.objects.Mandant#setName1(java.lang.String)
   */
  public void setName1(String name1) throws RemoteException
  {
    setField("name1",name1);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setName2(java.lang.String)
   */
  public void setName2(String name2) throws RemoteException
  {
    setField("name2",name2);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setFirma(java.lang.String)
   */
  public void setFirma(String firma) throws RemoteException
  {
    setField("firma",firma);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setStrasse(java.lang.String)
   */
  public void setStrasse(String strasse) throws RemoteException
  {
    setField("strasse",strasse);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setPLZ(java.lang.String)
   */
  public void setPLZ(String plz) throws RemoteException
  {
    setField("plz",plz);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setOrt(java.lang.String)
   */
  public void setOrt(String ort) throws RemoteException
  {
    setField("ort",ort);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setSteuernummer(java.lang.String)
   */
  public void setSteuernummer(String steuernummer) throws RemoteException
  {
    setField("steuernummer",steuernummer);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setKontenrahmen(de.willuhn.jameica.fibu.objects.Kontenrahmen)
   */
  public void setKontenrahmen(Kontenrahmen kontenrahmen) throws RemoteException
  {
    if (kontenrahmen == null) return;
    setField("kontenrahmen_id",new Integer(kontenrahmen.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#isActive()
   */
  public boolean isActive() throws RemoteException
  {
    try {
      return this.getID().equals(Settings.getActiveMandant().getID());
    }
    catch (Exception e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      return false;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getFinanzamt()
   */
  public Finanzamt getFinanzamt() throws RemoteException
  {
    return (Finanzamt) getField("finanzamt_id");
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setFinanzamt(de.willuhn.jameica.fibu.objects.Finanzamt)
   */
  public void setFinanzamt(Finanzamt finanzamt) throws RemoteException
  {
    if (finanzamt == null) return;
    setField("finanzamt_id",new Integer(finanzamt.getID()));
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#getGeschaeftsjahr()
   */
  public int getGeschaeftsjahr() throws RemoteException
  {
    Integer i = (Integer) getField("geschaeftsjahr");
    try {
      return i.intValue();
    }
    catch (NumberFormatException e) {}
    catch (NullPointerException e2) {}
    // mhh, noch kein's definiert. Also nehmen wir das aktuelle.
    Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());
    return cal.get(Calendar.YEAR);
  }

  /**
   * @see de.willuhn.jameica.fibu.objects.Mandant#setGeschaeftsjahr(int)
   */
  public void setGeschaeftsjahr(int jahr) throws RemoteException
  {
    setField("geschaeftsjahr",new Integer(jahr));
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#deleteCheck()
   */
  public void deleteCheck() throws ApplicationException
  {
    try {
      if (isActive())
        throw new ApplicationException("Mandant ist aktiv und kann daher nicht gelöscht werden.\n" +          "Aktivieren Sie hierzu in den Einstellungen einen anderen Mandanten.");
    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Lösch-Prüfung des Mandanten.");
    }
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#insertCheck()
   */
  public void insertCheck() throws ApplicationException
  {
    // insertCheck() ist erstmal das gleiche wie updateCheck() ;)
    updateCheck();
  }

  /**
   * @see de.willuhn.jameica.rmi.AbstractDBObject#updateCheck()
   */
  public void updateCheck() throws ApplicationException
  {
    try {
      String firma = getFirma();
      if (firma == null || "".equals(firma)) {
        throw new ApplicationException("Bitte geben Sie die Firma ein.");
      }
  
      String steuernummer = getSteuernummer();
      if (steuernummer == null || "".equals(steuernummer)) {
        throw new ApplicationException("Bitte geben Sie die Steuernummer ein.");
      }
  
      if (getFinanzamt() == null) {
        throw new ApplicationException("Bitte wählen Sie ein Finanzamt aus.");
      }

      if (getKontenrahmen() == null) {
        throw new ApplicationException("Bitte wählen Sie einen Kontenrahmen aus.");
      }

      int year = getGeschaeftsjahr();
      if (year < Fibu.YEAR_MIN || year > Fibu.YEAR_MAX)
        throw new ApplicationException("Geschäftsjahr nicht innerhalb des gültigen Bereiches.");

    }
    catch (RemoteException e)
    {
      throw new ApplicationException("Fehler bei der Prüfung der Pflichtfelder.",e);
    }
  }


}


/*********************************************************************
 * $Log: MandantImpl.java,v $
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