/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungListe.java,v $
 * $Revision: 1.2 $
 * $Date: 2003/11/21 02:47:50 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.objects.Buchung;
import de.willuhn.jameica.rmi.DBIterator;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.Headline;

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


    int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |
          SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
    final Table table = new Table(getParent(), style);
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
    gridData.horizontalSpan = 3;
    table.setLayoutData(gridData);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    final TableColumn columnName1 = new TableColumn(table, SWT.NONE);
    columnName1.setWidth(100);
    columnName1.setText(I18N.tr("Belegnummer"));

    final TableColumn columnName2 = new TableColumn(table, SWT.NONE);
    columnName2.setWidth(100);
    columnName2.setText(I18N.tr("Datum"));

    final TableColumn columnName3 = new TableColumn(table, SWT.NONE);
    columnName3.setWidth(200);
    columnName3.setText(I18N.tr("Text"));

    final TableColumn columnOrt = new TableColumn(table, SWT.NONE);
    columnOrt.setWidth(50);
    columnOrt.setText(I18N.tr("Betrag"));

    try {
      DBIterator list = Application.getDefaultDatabase().createList(Buchung.class);
      while (list.hasNext())
      {
        final TableItem item = new TableItem(table, SWT.NONE);
        final Buchung buchung = (Buchung) list.next();
        item.setData(buchung.getID());
        item.setText(0, ""+buchung.getBelegnummer());
        item.setText(1, Fibu.DATEFORMAT.format(buchung.getDatum()));
        item.setText(2, buchung.getText());
        item.setText(3, ""+buchung.getBetrag()); // TODO
      }
    }
    catch (Exception e)
    {
      Application.getLog().error("error while loading buchung list");
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Buchungen."));
      e.printStackTrace();
    }

    table.addListener(SWT.MouseDoubleClick,
      new Listener(){
        public void handleEvent(Event e){
          TableItem item = table.getItem( new Point(e.x,e.y));
            if (item == null) return;
            String id = (String) item.getData();
            if (id == null) return;
            //controller.handleSubmit(id);
        }
      });

//    // Neue Absenderadresse
//    Label blank = SWTFactory.getLabel(comp, "", SWTFactory.gdFill2());
//    Button newAbs = SWTFactory.getButton(comp, I18N.tr("Neuer Absender"), SWTFactory.gdRight(), SWT.PUSH);
//    newAbs.addMouseListener(new MouseAdapter() {
//      public void mouseUp(MouseEvent e) {
//        controller.handleSubmit(null);
//      }
//    });
//    //  Blank Label
//    SWTFactory.getLabel(comp, "", SWTFactory.gdFill2());
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
 * Revision 1.2  2003/11/21 02:47:50  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/11/21 02:10:57  willuhn
 * @N buchung dialog works now
 *
 **********************************************************************/