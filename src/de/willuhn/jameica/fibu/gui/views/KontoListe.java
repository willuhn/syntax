/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.KontoNeu;
import de.willuhn.jameica.fibu.gui.part.KontoList;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Zeigt eine Liste aller Konten an.
 * @author willuhn
 */
public class KontoListe extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    // Checken, ob ein Kontenrahmen uebergeben wurde
    Kontenrahmen kr = null;
    Object context = this.getCurrentObject();
    if (context != null && (context instanceof Kontenrahmen))
      kr = (Kontenrahmen) context;
    else
    {
      try
      {
        kr = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
      }
      catch (Exception e)
      {
        Logger.error("error while reading kr",e);
      }
    }
    
    if (kr == null)
      throw new ApplicationException(i18n.tr("Kein Kontenrahmen ausgewählt"));
    
    GUI.getView().setTitle(i18n.tr("Liste der Konten. Kontenrahmen: {0}",kr.getName()));
    Part p = new KontoList(kr.getMandant(),kr.getKonten(),new KontoNeu());
    p.paint(getParent());
    
    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Neues Konto"), new KontoNeu(),null,true,"list-add.png");
    buttons.paint(getParent());

  }
}
