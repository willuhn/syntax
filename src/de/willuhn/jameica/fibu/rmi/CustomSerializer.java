/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
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