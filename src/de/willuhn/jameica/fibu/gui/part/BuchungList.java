/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/part/BuchungList.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/08 22:54:15 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.part;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 */
public class BuchungList extends TablePart
{

  /**
   * @param list
   * @param action
   */
  public BuchungList(GenericIterator list, Action action)
  {
    super(list, action);
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    addColumn(i18n.tr("Datum"),"datum", new DateFormatter(Fibu.DATEFORMAT));
    addColumn(i18n.tr("Konto"),"konto_id");
    addColumn(i18n.tr("Geldkonto"),"geldkonto_id");
    addColumn(i18n.tr("Text"),"buchungstext");
    addColumn(i18n.tr("Beleg"),"belegnummer");
    addColumn(i18n.tr("Netto-Betrag"),"betrag",new CurrencyFormatter(Settings.getCurrency(), Fibu.DECIMALFORMAT));
  }

}


/*********************************************************************
 * $Log: BuchungList.java,v $
 * Revision 1.1  2005/08/08 22:54:15  willuhn
 * @N massive refactoring
 *
 **********************************************************************/