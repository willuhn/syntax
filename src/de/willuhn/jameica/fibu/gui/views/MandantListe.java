/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantListe.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/24 23:02:11 $
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
import de.willuhn.jameica.fibu.controller.MandantControl;
import de.willuhn.jameica.fibu.objects.Mandant;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.Table;

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
    Headline headline = new Headline(getParent(),I18N.tr("Liste der Mandanten."));

    try {
      Mandant mandant = (Mandant) Application.getDefaultDatabase().createObject(Mandant.class,null);
      MandantControl controller = new MandantControl(mandant);

      DBIterator list = Application.getDefaultDatabase().createList(mandant.getClass());
      list.addFilter("1 order by firma desc");

      Table table = new Table(list,controller);
      table.addColumn("Name 1","name1");
      table.addColumn("Name 2","name2");
      table.addColumn("Firma","firma");
      table.addColumn("Ort","ort");
      table.addColumn("Steuernummer","steuernummer");
      table.addColumn("Kontenrahmen","kontenrahmen_id");
      
      table.paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton("Neuer Mandant",controller);

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
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/