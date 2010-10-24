/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/rmi/CustomSerializer.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/10/24 22:29:37 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

/**
 * Kann implementiert werden, wenn die XML-Serialisierung vom GenericObject-Standard
 * abweichen soll.
 */
public interface CustomSerializer
{
  /**
   * Liefert die abweichende Liste der zu serialisierenden Attribute.
   * @return Liste der zu serialisierenden Attribute.
   * @throws RemoteException
   */
  public String[] getCustomAttributeNames() throws RemoteException;
}



/**********************************************************************
 * $Log: CustomSerializer.java,v $
 * Revision 1.1  2010/10/24 22:29:37  willuhn
 * @C Brutto-Betrag bei Buchungen mit exportieren
 *
 **********************************************************************/