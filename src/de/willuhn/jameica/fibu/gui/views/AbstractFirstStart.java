/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/Attic/AbstractFirstStart.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 14:08:29 $
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
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Klasse der Wizard-Views.
 */
public abstract class AbstractFirstStart extends AbstractView
{
  I18N i18n = null;
  private FirstStartControl control = null;
  
  /**
   * ct.
   */
  public AbstractFirstStart()
  {
    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }
  
  /**
   * Liefert den Controller.
   * @return der Controller.
   */
  final FirstStartControl getController()
  {
    if (this.control != null)
      return this.control;
    
    Object o = getCurrentObject();
    if (o != null && (o instanceof FirstStartControl))
      this.control = (FirstStartControl) o;
    else
      this.control = new FirstStartControl(this);
    return this.control;
  }
  
  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    getController().handleReset();
  }
}


/*********************************************************************
 * $Log: AbstractFirstStart.java,v $
 * Revision 1.1  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/