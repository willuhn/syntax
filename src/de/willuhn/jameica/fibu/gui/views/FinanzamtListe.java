/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/FinanzamtListe.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/25 00:22:16 $
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
import de.willuhn.jameica.fibu.controller.FinanzamtControl;
import de.willuhn.jameica.fibu.objects.Finanzamt;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.ButtonArea;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.Table;

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
    Headline headline = new Headline(getParent(),I18N.tr("Liste der Finanzämter."));

    try {
      Finanzamt fa = (Finanzamt) Application.getDefaultDatabase().createObject(Finanzamt.class,null);
      FinanzamtControl controller = new FinanzamtControl(fa);

      DBIterator list = Application.getDefaultDatabase().createList(fa.getClass());
      list.addFilter("1 order by name desc");

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
      Application.getLog().error("error while loading finanzamt list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Finanzämter."));
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
 * $Log: FinanzamtListe.java,v $
 * Revision 1.1  2003/11/25 00:22:16  willuhn
 * @N added Finanzamt
 *
 **********************************************************************/