/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/Exporter.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/08/27 11:19:40 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.io.OutputStream;
import java.rmi.RemoteException;

import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Basis-Interface aller Exporter.
 * Alle Klassen, die dieses Interface implementieren, werden automatisch
 * von SynTAX erkannt und dem Benutzer als Export-Moeglichkeit angeboten
 * insofern sie einen parameterlosen Konstruktor mit dem Modifier "public"
 * besitzen (Java-Bean-Konvention).
 */
public interface Exporter extends IO
{
  /**
   * Exportiert die genannten Objekte in den angegebenen OutputStream.
   * @param objects die zu exportierenden Objekte.
   * @param format das vom User ausgewaehlte Export-Format.
   * @param os der Ziel-Ausgabe-Stream.
   * Der Exporter muss den OutputStream selbst schliessen!
   * @param monitor ein Monitor, an den der Exporter Ausgaben ueber seinen
   * Bearbeitungszustand ausgeben kann.
   * @throws RemoteException
   * @throws ApplicationException 
   */
  public void doExport(Object[] objects, IOFormat format, OutputStream os, ProgressMonitor monitor) throws RemoteException, ApplicationException;
}


/**********************************************************************
 * $Log: Exporter.java,v $
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 **********************************************************************/