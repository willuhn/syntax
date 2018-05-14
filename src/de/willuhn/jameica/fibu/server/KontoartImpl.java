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

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.jameica.fibu.rmi.Kontoart;

/**
 * @author willuhn
 */
public class KontoartImpl extends AbstractDBObject implements Kontoart
{

  /**
   * Erzeugt eine neue Kontoart.
   * @throws RemoteException
   */
  public KontoartImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.db.AbstractDBObject#getTableName()
   */
  protected String getTableName()
  {
    return "kontoart";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontoart#getName()
   */
  public String getName() throws RemoteException
  {
    return (String) getAttribute("name");
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontoart#getKontoArt()
   */
  public int getKontoArt() throws RemoteException
  {
    try {
      return Integer.parseInt(getID());
    }
    catch (NumberFormatException ne)
    {
      return KONTOART_UNGUELTIG;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.Kontoart#isSteuerpflichtig()
   */
  public boolean isSteuerpflichtig() throws RemoteException
  {
		return (getKontoArt() == Kontoart.KONTOART_ERLOES ||
						getKontoArt() == Kontoart.KONTOART_AUFWAND ||
						getKontoArt() == Kontoart.KONTOART_ANLAGE);
  }

}

/*********************************************************************
 * $Log: KontoartImpl.java,v $
 * Revision 1.9  2005/09/02 17:35:07  willuhn
 * @N Kontotyp
 * @N Betriebsergebnis
 *
 * Revision 1.8  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.7  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.6  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
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
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/