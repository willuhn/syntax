/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantListe.java,v $
 * $Revision: 1.5 $
 * $Date: 2003/12/11 21:00:34 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.gui.controller.MandantControl;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;
import de.willuhn.jameica.rmi.DBIterator;

/**
 * @author willuhn
 */
public class MandantListe extends AbstractView
{

  public MandantListe(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    new Headline(getParent(),I18N.tr("Liste der Mandanten."));

    try {
      Mandant mandant = (Mandant) Application.getDefaultDatabase().createObject(Mandant.class,null);
      MandantControl controller = new MandantControl(mandant);

      DBIterator list = Application.getDefaultDatabase().createList(mandant.getClass());
      list.setOrder("order by firma desc");

      Table table = new Table(list,controller);
      table.addColumn(I18N.tr("Name 1"),"name1");
      table.addColumn(I18N.tr("Name 2"),"name2");
      table.addColumn(I18N.tr("Firma"),"firma");
      table.addColumn(I18N.tr("Ort"),"ort");
      table.addColumn(I18N.tr("Steuernummer"),"steuernummer");
      table.addColumn(I18N.tr("Kontenrahmen"),"kontenrahmen_id");
      
      table.paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton(I18N.tr("Neuer Mandant"),controller);

    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading mandant list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Mandanten."));
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
 * $Log: MandantListe.java,v $
 * Revision 1.5  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.4  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.3  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/