/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/SteuerListe.java,v $
 * $Revision: 1.5 $
 * $Date: 2003/12/15 19:08:04 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.controller.SteuerControl;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;
import de.willuhn.jameica.rmi.DBIterator;

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
    new Headline(getParent(),I18N.tr("Liste der Steuersätze."));

    try {
      Steuer steuer = (Steuer) Settings.getDatabase().createObject(Steuer.class,null);
      SteuerControl controller = new SteuerControl(steuer);

      DBIterator list = Settings.getDatabase().createList(steuer.getClass());
      list.setOrder("order by name desc");

      Table table = new Table(list,controller);
      table.addColumn(I18N.tr("Name"),"name");
      table.addColumn(I18N.tr("Steuersatz"),"satz");
      table.addColumn(I18N.tr("Steuer-Sammelkonto"),"steuerkonto_id");
      
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
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/