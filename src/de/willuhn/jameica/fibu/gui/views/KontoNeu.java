/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/KontoNeu.java,v $
 * $Revision: 1.15 $
 * $Date: 2006/01/02 01:54:07 $
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
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.action.KontoDelete;
import de.willuhn.jameica.fibu.gui.controller.KontoControl;
import de.willuhn.jameica.fibu.gui.part.BuchungList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Bearbeitet ein Konto.
 * @author willuhn
 */
public class KontoNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    final KontoControl control = new KontoControl(this);

    String kr = i18n.tr("unbekannt");
    try
    {
      kr = Settings.getActiveGeschaeftsjahr().getKontenrahmen().getName();
    }
    catch (Exception e)
    {
      Logger.error("error while reading kr",e);
    }

    GUI.getView().setTitle(i18n.tr("Konto bearbeiten. Kontenrahmen: {0}",kr));

    if (!control.getKonto().isUserKonto())
      GUI.getView().setErrorText(i18n.tr("Konto ist ein System-Konto und darf daher nicht geändert werden"));

    Container group = new LabelGroup(getParent(),i18n.tr("Eigenschaften des Kontos"));

    group.addLabelPair(i18n.tr("Name")            , control.getName());
    group.addLabelPair(i18n.tr("Kontonummer")     , control.getKontonummer());
    group.addLabelPair(i18n.tr("Kontoart")        , control.getKontoart());
    group.addLabelPair(i18n.tr("Steuerkonto-Typ") , control.getKontotyp());
    group.addLabelPair(i18n.tr("Steuersatz")      , control.getSteuer());
    group.addLabelPair(i18n.tr("Kontenrahmen")    , control.getKontenrahmen());
    group.addLabelPair(i18n.tr("Saldo")           , control.getSaldo());
    
    ButtonArea buttons = group.createButtonArea(3);
    buttons.addButton(i18n.tr("Zurück"), new Back());
    Button delete = new Button(i18n.tr("Löschen"), new KontoDelete(),getCurrentObject());
    delete.setEnabled(control.getKonto().isUserKonto());
    buttons.addButton(delete);
    
    Button store = new Button(i18n.tr("Speichern"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore();
      }
    },null,true);
    store.setEnabled(control.getKonto().isUserKonto());
    buttons.addButton(store);

    new Headline(getParent(),i18n.tr("Buchungen auf diesem Konto"));
    new BuchungList(control.getKonto(),new BuchungNeu()).paint(getParent());

  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: KontoNeu.java,v $
 * Revision 1.15  2006/01/02 01:54:07  willuhn
 * @N Benutzerdefinierte Konten
 *
 * Revision 1.14  2005/09/01 16:34:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2005/08/29 22:26:19  willuhn
 * @N Jahresabschluss
 *
 * Revision 1.12  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.10  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.9  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.5  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.3  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:52  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 **********************************************************************/