/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungListe.java,v $
 * $Revision: 1.3 $
 * $Date: 2003/11/22 20:43:07 $
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

      Table table = new Table(list,controller);
      table.addColumn("Datum","datum");
      table.addColumn("Kontonummer","konto_id"); // TODO: foreign keys noch einbauen
      table.addColumn("Text","text");
      table.addColumn("Beleg","belegnummer");
      table.addColumn("Betrag","betrag");
      
      table.paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton("Neue Buchung",controller);

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