/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/BuchungNeu.java,v $
 * $Revision: 1.1 $
 * $Date: 2003/11/20 03:48:44 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.views;

import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.willuhn.jameica.Application;
import de.willuhn.jameica.GUI;
import de.willuhn.jameica.I18N;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.objects.Buchung;
import de.willuhn.jameica.rmi.DBObject;
import de.willuhn.jameica.views.AbstractView;
import de.willuhn.jameica.views.parts.LabelGroup;
import de.willuhn.jameica.views.parts.SelectInput;
import de.willuhn.jameica.views.parts.TextInput;

/**
 * @author willuhn
 */
public class BuchungNeu extends AbstractView
{

  public BuchungNeu(Object o)
  {
    super(o);
  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#bind()
   */
  public void bind()
  {

    setHeadline(I18N.tr("Neue Buchung erfassen"));

    LabelGroup kontoGroup = createLabelGroup(I18N.tr("Konto"));

    // Laden der Buchung oder Erstellen einer neuen
    Buchung buchung = (Buchung) getCurrentObject();
    if (buchung == null)
    {
      try {
        buchung = (Buchung) Application.getDefaultDatabase().createObject(Buchung.class,null);
      }
      catch (RemoteException e)
      {
        GUI.setActionText(I18N.tr("Neue Buchung konnte nicht erzeugt werden."));
      }
    }

    
    try {
      Date buchungsdatum = buchung.getDatum();
      kontoGroup.addLabelPair(I18N.tr("Datum"),new TextInput(Fibu.DATEFORMAT.format(buchungsdatum == null ? new Date() : buchungsdatum)));
      kontoGroup.addLabelPair(I18N.tr("Konto"),new SelectInput((DBObject) buchung.getKonto()));
    }
    catch (RemoteException e)
    {
      GUI.setActionText(I18N.tr("Fehler beim Lesen der Buchungsdaten."));
    }


  }

  /**
   * @see de.willuhn.jameica.views.AbstractView#unbind()
   */
  public void unbind()
  {
    MessageBox warn = new MessageBox(GUI.shell,SWT.ICON_WARNING);
    warn.setMessage("Sicher?");
    warn.open();
  }
}

/*********************************************************************
 * $Log: BuchungNeu.java,v $
 * Revision 1.1  2003/11/20 03:48:44  willuhn
 * @N first dialogues
 *
 **********************************************************************/