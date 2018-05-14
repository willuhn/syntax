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
    Part p = new KontoList(kr.getKonten(),new KontoNeu());
    p.paint(getParent());
    
    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Neues Konto"), new KontoNeu(),null,true,"list-add.png");
    buttons.paint(getParent());

  }
}

/*********************************************************************
 * $Log: KontoListe.java,v $
 * Revision 1.24  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.23  2011-03-21 11:17:26  willuhn
 * @N BUGZILLA 1004
 *
 * Revision 1.22  2010-06-02 00:02:58  willuhn
 * @N Mehr Icons
 *
 * Revision 1.21  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.20  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/