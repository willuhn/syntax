/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/ExportAction.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/05/29 17:30:26 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.io.File;
import java.util.Date;

import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

/**
 * Basis-Interface fuer alle Actions, die Exports machen.
 */
public interface ExportAction extends Action
{

  /**
   * Liefert einen sprechenden Namen des Exports.
   * @return Sprechender Name des Exports.
   */
  public String getName();
  
  /**
   * Liefert einen Speichern-Unter Dialog fuer den User und zeigt den uebergebenen
   * Dateinamen als Vorschlag an.
   * @param suggestion Vorschlag.
   * @return Die Datei, in die gespeichert werden soll.
   * @throws OperationCanceledException wenn der User den Vorgang abbricht
   * @throws ApplicationException wenn ein anderer Fehler aufgetreten ist.
   */
  public File storeTo(String suggestion) throws OperationCanceledException, ApplicationException;
  
  /**
   * Liefert ein eventuell zusaetzlich angegebenes Start-Datum.
   * @return Start-Datum oder null.
   */
  public Date getStart();
  
  /**
   * Liefert ein eventuell zusaetzlich angegebenes End-Datum.
   * @return End-Datum oder null.
   */
  public Date getEnd();
  
  /**
   * Definiert ein optionales zusaetzliches Start-Datum.
   * @param d Start-Datum.
   */
  public void setStart(Date d);
  
  /**
   * Definiert ein optionales zusaetzliches End-Datum.
   * @param d End-Datum.
   */
  public void setEnd(Date d);
  
  /**
   * Liefert das Konto, mit dem die Auswertung beginnen soll.
   * @return das Konto oder null.
   */
  public Konto getStartKonto();
  
  /**
   * Liefert das Konto, mit dem die Auswertung enden soll.
   * @return das Konto oder null.
   */
  public Konto getEndKonto();
  
  /**
   * Speichert das Konto, mit dem die Auswertung beginnen soll.
   * @param konto das Konto.
   */
  public void setStartKonto(Konto konto);
  
  /**
   * Speichert das Konto, mit dem die Auswertung enden soll.
   * @param konto das Konto.
   */
  public void setEndKonto(Konto konto);
  

}


/*********************************************************************
 * $Log: ExportAction.java,v $
 * Revision 1.3  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.2  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.1  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 **********************************************************************/