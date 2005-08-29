/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/GeschaeftsjahrList.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/29 12:17:28 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.part;

import java.rmi.RemoteException;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Liste mit Geschaeftsjahren eines Mandanten.
 */
public class GeschaeftsjahrList extends TablePart
{

  /**
   * ct.
   * @param m der Mandant.
   * @param action
   * @throws RemoteException
   */
  public GeschaeftsjahrList(Mandant m, Action action) throws RemoteException
  {
    super(init(m), action);
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Mandant"), "mandant_id");
    addColumn(i18n.tr("Kontenrahmen"), "kontenrahmen_id");
    addColumn(i18n.tr("Beginn"), "beginn", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Ende"), "ende", new DateFormatter(Fibu.DATEFORMAT));
  }
  
  /**
   * Initialisiert die Liste.
   * @param m
   * @return Liste.
   * @throws RemoteException
   */
  private static GenericIterator init(Mandant m) throws RemoteException
  {
    if (m == null)
      return PseudoIterator.fromArray(new Mandant[0]);
    return m.getGeschaeftsjahre();
  }
}


/*********************************************************************
 * $Log: GeschaeftsjahrList.java,v $
 * Revision 1.1  2005/08/29 12:17:28  willuhn
 * @N Geschaeftsjahr
 *
 **********************************************************************/