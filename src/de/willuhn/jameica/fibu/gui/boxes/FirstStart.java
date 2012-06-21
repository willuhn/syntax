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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.controller.FirstStartControl;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.boxes.AbstractBox;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Font;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Platform;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Wizard fuer den ersten Start.
 */
public class FirstStart extends AbstractBox
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
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

    // Wir unterscheiden hier beim Layout nach Windows/OSX und Rest.
    // Unter Windows und OSX sieht es ohne Rahmen und ohne Hintergrund besser aus
    org.eclipse.swt.graphics.Color bg = null;
    int border = SWT.NONE;
    
    int os = Application.getPlatform().getOS();
    if (os != Platform.OS_WINDOWS && os != Platform.OS_WINDOWS_64 && os != Platform.OS_MAC)
    {
      bg = GUI.getDisplay().getSystemColor(SWT.COLOR_WHITE);
      border = SWT.BORDER;
    }
    
    // 2-spaltige Anzeige. Links das Icon, rechts Text und Buttons
    Composite comp = new Composite(parent,border);
    comp.setBackground(bg);
    comp.setBackgroundMode(SWT.INHERIT_FORCE);
    comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    comp.setLayout(new GridLayout(2,false));
    
    // Linke Spalte mit dem Icon
    {
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
      gd.verticalSpan = 3;
      Label icon = new Label(comp,SWT.NONE);
      icon.setBackground(bg);
      icon.setLayoutData(gd);
      icon.setImage(SWTUtil.getImage("package-x-generic-medium.png"));
    }
    
    // Ueberschrift
    {
      Label title = new Label(comp,SWT.NONE);
      title.setBackground(bg);
      title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      title.setFont(Font.H2.getSWTFont());
      title.setText(i18n.tr("SynTAX: Installation"));
    }
    
    // Text
    {
      Label desc = new Label(comp,SWT.WRAP);
      desc.setBackground(bg);
      desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      desc.setText(i18n.tr("Sie starten SynTAX zum ersten Mal.\n\nDieser Assistent wird Sie bei " +
                           "der Einrichtung der Datenbank sowie Ihrer Stammdaten unterstützen."));
    }
      
    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Weiter..."),new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    },null,true,"go-next.png");
    buttons.paint(comp);
  }

  /**
   * @see de.willuhn.jameica.gui.boxes.AbstractBox#getHeight()
   */
  public int getHeight()
  {
    return 180;
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