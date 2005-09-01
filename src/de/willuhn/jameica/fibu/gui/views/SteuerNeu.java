/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/SteuerNeu.java,v $
 * $Revision: 1.14 $
 * $Date: 2005/09/01 23:07:17 $
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
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.SteuerDelete;
import de.willuhn.jameica.fibu.gui.controller.SteuerControl;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Legt einen neuen Steuersatz an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class SteuerNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Steuersatz bearbeiten"));

    final SteuerControl control = new SteuerControl(this);

    Container steuerGroup = new LabelGroup(getParent(),i18n.tr("Steuersatz"));

    steuerGroup.addLabelPair(i18n.tr("Name")      , 				control.getName());
    steuerGroup.addLabelPair(i18n.tr("Steuersatz"), 				control.getSatz());
    steuerGroup.addLabelPair(i18n.tr("Steuer-Sammelkonto"), control.getKontoAuswahl());

    ButtonArea buttonArea = steuerGroup.createButtonArea(3);
    buttonArea.addButton(i18n.tr("Zur�ck"), new Back());
    
    Kontenrahmen soll = control.getSteuer().getSteuerKonto().getKontenrahmen();
    Kontenrahmen ist  = Settings.getActiveGeschaeftsjahr().getKontenrahmen();
    
    boolean canStore = soll.equals(ist);
    
    if (!canStore)
      GUI.getView().setErrorText(i18n.tr("Steuer-Konto ist nicht Teil des aktiven Kontenrahmen und darf daher nicht ge�ndert werden."));
    
    Button delete = new Button(i18n.tr("L�schen"), new SteuerDelete(),getCurrentObject());
    delete.setEnabled(canStore);
    buttonArea.addButton(delete);
    
    Button store = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);
    store.setEnabled(canStore);
    buttonArea.addButton(store);
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: SteuerNeu.java,v $
 * Revision 1.14  2005/09/01 23:07:17  willuhn
 * @B bugfixing
 *
 * Revision 1.13  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.12  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.11  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.3  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/