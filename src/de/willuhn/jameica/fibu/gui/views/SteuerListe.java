/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/SteuerListe.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/01 20:29:00 $
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
import de.willuhn.jameica.fibu.controller.SteuerControl;
import de.willuhn.jameica.fibu.objects.Steuer;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.Table;

/**
 * @author willuhn
 */
public class SteuerListe extends AbstractView
{

  public SteuerListe(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    Headline headline = new Headline(getParent(),I18N.tr("Liste der Steuersätze."));

    try {
      Steuer steuer = (Steuer) Application.getDefaultDatabase().createObject(Steuer.class,null);
      SteuerControl controller = new SteuerControl(steuer);

      DBIterator list = Application.getDefaultDatabase().createList(steuer.getClass());
      list.setOrder("order by name desc");

      Table table = new Table(list,controller);
      table.addColumn(I18N.tr("Name"),"name");
      table.addColumn(I18N.tr("Steuersatz"),"satz");
      
      table.paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton(I18N.tr("Neuer Steuersatz"),controller);

    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading steuer list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Steuersätze."));
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
 * $Log: SteuerListe.java,v $
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/