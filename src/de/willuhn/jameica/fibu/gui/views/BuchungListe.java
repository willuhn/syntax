/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungListe.java,v $
 * $Revision: 1.8 $
 * $Date: 2003/11/30 16:23:11 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.views;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.controller.BuchungControl;
import de.willuhn.jameica.fibu.objects.Buchung;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.Table;

/**
 * @author willuhn
 */
public class BuchungListe extends AbstractView
{

  /**
   * Erzeugt einen neuen Dialog des Typs "Buchungsliste".
   * @param o
   */
  public BuchungListe(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    Headline headline = new Headline(getParent(),I18N.tr("Buchungsliste."));

    try {
      Buchung buchung = (Buchung) Application.getDefaultDatabase().createObject(Buchung.class,null);
      BuchungControl controller = new BuchungControl(buchung);

      DBIterator list = Application.getDefaultDatabase().createList(buchung.getClass());
      list.setOrder("order by id desc");

      Table table = new Table(list,controller);
      table.addColumn(I18N.tr("Datum"),"datum");
      table.addColumn(I18N.tr("Konto"),"konto_id");
      table.addColumn(I18N.tr("Geldkonto"),"geldkonto_id");
      table.addColumn(I18N.tr("Text"),"text");
      table.addColumn(I18N.tr("Beleg"),"belegnummer");
      table.addColumn(I18N.tr("Betrag"),"betrag");
      
      table.paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton(I18N.tr("Neue Buchung"),controller);

    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading buchung list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Buchungen."));
      e.printStackTrace();
    }
  }


  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
  }
}

/*********************************************************************
 * $Log: BuchungListe.java,v $
 * Revision 1.8  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.6  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 * Revision 1.5  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 * Revision 1.4  2003/11/24 16:26:16  willuhn
 * @N AbstractDBObject is now able to resolve foreign keys
 *
 * Revision 1.3  2003/11/22 20:43:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/21 02:47:50  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 **********************************************************************/