/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtListe.java,v $
 * $Revision: 1.6 $
 * $Date: 2003/12/19 01:43:43 $
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
import de.willuhn.jameica.fibu.gui.controller.FinanzamtControl;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.views.AbstractView;
import de.willuhn.jameica.gui.views.parts.*;
import de.willuhn.jameica.rmi.DBIterator;

/**
 * @author willuhn
 */
public class FinanzamtListe extends AbstractView
{

  public FinanzamtListe(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    new Headline(getParent(),I18N.tr("Liste der Finanzämter."));

    try {
      Finanzamt fa = (Finanzamt) Settings.getDatabase().createObject(Finanzamt.class,null);
      FinanzamtControl controller = new FinanzamtControl(fa);

      DBIterator list = Settings.getDatabase().createList(fa.getClass());
      list.setOrder("order by name desc");

      Table table = new Table(list,controller);
      table.addColumn(I18N.tr("Name"),"name");
      table.addColumn(I18N.tr("Strasse"),"strasse");
      table.addColumn(I18N.tr("Postfach"),"postfach");
      table.addColumn(I18N.tr("PLZ"),"plz");
      table.addColumn(I18N.tr("Ort"),"ort");
      
      table.paint(getParent());

      ButtonArea buttons = new ButtonArea(getParent(),1);
      buttons.addCreateButton(I18N.tr("Finanzamt hinzufügen"),controller);

    }
    catch (Exception e)
    {
      if (Application.DEBUG)
        e.printStackTrace();
      Application.getLog().error("error while loading finanzamt list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Finanzämter."));
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
 * $Log: FinanzamtListe.java,v $
 * Revision 1.6  2003/12/19 01:43:43  willuhn
 * @C small fixes
 *
 * Revision 1.5  2003/12/15 19:08:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.2  2003/11/30 16:23:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/