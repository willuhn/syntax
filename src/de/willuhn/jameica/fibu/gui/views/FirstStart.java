/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/FirstStart.java,v $
 * $Revision: 1.6 $
 * $Date: 2006/06/19 22:23:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.controller.FirstStartControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.parts.FormTextPart;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * View mit dem Wizard fuer den ersten Start.
 */
public class FirstStart extends AbstractView implements Extension
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final FirstStartControl control = new FirstStartControl(this);

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("SynTAX: Installation"));
    
    LabelGroup group = new LabelGroup(getParent(),i18n.tr("Willkommen"));
    group.addText("\n" + i18n.tr("Sie starten SynTAX zum ersten Mal. Dieser Assistent wird Sie bei " +
        "der Einrichtung der Datenbank sowie Ihrer Stammdaten unterstützen."),true);
    
    FormTextPart t = new FormTextPart();
    t.setText("<form><p></p>" +
        "<li>Einrichtung der Datenbank</li>" +
        "<li>Festlegen des Finanzamtes</li>" +
        "<li>Anlegen eines Mandanten</li>" +
        "<li>Erstellen eines Geschäftsjahres</li>" +
        "<p></p></form>");

    group.addPart(t);
    ButtonArea buttons = group.createButtonArea(1);

    buttons.addButton(i18n.tr("Weiter >>"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleForward();
      }
    });
    
    
  }

  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    AbstractView view = (AbstractView) extendable;
    this.setParent(view.getParent());
    try
    {
      this.bind();
    }
    catch (Exception e)
    {
      Logger.error("unable to extend view",e);
    }
  }

}


/*********************************************************************
 * $Log: FirstStart.java,v $
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