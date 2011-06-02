/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/boxes/FirstStart.java,v $
 * $Revision: 1.2 $
 * $Date: 2011/06/02 12:22:28 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.boxes;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.controller.FirstStartControl;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.boxes.AbstractBox;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Wizard fuer den ersten Start.
 */
public class FirstStart extends AbstractBox
{
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#isActive()
   */
  public boolean isActive()
  {
    // Diese Box kann nur beim ersten Start ausgewaehlt/angezeigt werden.
    return Settings.isFirstStart();
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#getDefaultEnabled()
   */
  public boolean getDefaultEnabled()
  {
    // Diese Box kann nur beim ersten Start ausgewaehlt/angezeigt werden.
    return Settings.isFirstStart();
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#getDefaultIndex()
   */
  public int getDefaultIndex()
  {
    return 0;
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#getName()
   */
  public String getName()
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    return "SynTAX: " + i18n.tr("Installation");
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#isEnabled()
   */
  public boolean isEnabled()
  {
    // Diese Box kann nur beim ersten Start ausgewaehlt/angezeigt werden.
    return Settings.isFirstStart();
  }
  
  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    final FirstStartControl control = new FirstStartControl(null);

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("SynTAX: Installation"));
    
    Container c  = new SimpleContainer(parent);
    c.addHeadline(i18n.tr("Willkommen"));
    c.addText("\n" + i18n.tr("Sie starten SynTAX zum ersten Mal. Dieser Assistent wird Sie bei " +
        "der Einrichtung der Datenbank sowie Ihrer Stammdaten unterstützen."),true);
    
    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Weiter >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    });
    c.addButtonArea(buttons);
  }

}


/*********************************************************************
 * $Log: FirstStart.java,v $
 * Revision 1.2  2011/06/02 12:22:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006-06-29 23:09:28  willuhn
 * @C keine eigene Startseite mehr, jetzt alles ueber Jameica-Boxsystem geregelt
 *
 * Revision 1.10  2006/06/29 15:11:31  willuhn
 * @N Setup-Wizard fertig
 * @N Auswahl des Geschaeftsjahres
 *
 * Revision 1.9  2006/06/27 23:30:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2006/06/20 23:27:17  willuhn
 * @C Anzeige des aktuellen Geschaeftsjahres
 * @C Oeffnen/Schliessen eines Geschaeftsjahres
 *
 * Revision 1.7  2006/06/19 22:41:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
 * Revision 1.5  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.3  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 * Revision 1.1  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 **********************************************************************/