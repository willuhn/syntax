/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoListe.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/12/05 17:11:58 $
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
import de.willuhn.jameica.fibu.controller.KontoControl;
import de.willuhn.jameica.fibu.objects.Konto;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.Headline;
import de.willuhn.jameica.views.parts.Table;

/**
 * @author willuhn
 */
public class KontoListe extends AbstractView
{

  public KontoListe(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {
    new Headline(getParent(),I18N.tr("Liste der Konten des aktiven Mandanten."));

    try {
      Konto konto = (Konto) Application.getDefaultDatabase().createObject(Konto.class,null);
      KontoControl controller = new KontoControl(konto);

      DBIterator list = Application.getDefaultDatabase().createList(konto.getClass());

      Table table = new Table(list,controller);
      table.addColumn(I18N.tr("Kontonummer"),"kontonummer");
      table.addColumn(I18N.tr("Name"),"name");
      table.addColumn(I18N.tr("Kontoart"),"kontoart");
      table.addColumn(I18N.tr("Kontenrahmen"),"kontenrahmen_id");
      table.addColumn(I18N.tr("Steuer"),"steuer_id");
      
      table.paint(getParent());
    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading konto list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Konten."));
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
 * $Log: KontoListe.java,v $
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/