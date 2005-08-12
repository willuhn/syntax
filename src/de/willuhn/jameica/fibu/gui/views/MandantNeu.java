/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/views/MandantNeu.java,v $
 * $Revision: 1.14 $
 * $Date: 2005/08/12 00:10:59 $
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
import de.willuhn.jameica.fibu.gui.action.MandantDelete;
import de.willuhn.jameica.fibu.gui.controller.MandantControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Legt einen neuen Mandanten an oder bearbeitet einen existierenden.
 * @author willuhn
 */
public class MandantNeu extends AbstractView
{

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    GUI.getView().setTitle(i18n.tr("Mandant bearbeiten"));

    final MandantControl control = new MandantControl(this);

    Container contactGroup = new LabelGroup(getParent(),i18n.tr("Kontaktdaten"));

    contactGroup.addLabelPair(i18n.tr("Name 1")  , control.getName1());
    contactGroup.addLabelPair(i18n.tr("Name 2")  , control.getName2());
    contactGroup.addLabelPair(i18n.tr("Firma")   , control.getFirma());
    contactGroup.addLabelPair(i18n.tr("Strasse") , control.getStrasse());
    contactGroup.addLabelPair(i18n.tr("PLZ")     , control.getPLZ());
    contactGroup.addLabelPair(i18n.tr("Ort")     , control.getOrt());

    Container finanzGroup = new LabelGroup(getParent(),i18n.tr("Buchhalterische Daten"));

		finanzGroup.addLabelPair(i18n.tr("Kontenrahmen"), control.getKontenrahmenAuswahl());
    finanzGroup.addLabelPair(i18n.tr("Finanzamt"),		control.getFinanzamtAuswahl());
    finanzGroup.addLabelPair(i18n.tr("Steuernummer"),	control.getSteuernummer());
    finanzGroup.addLabelPair(i18n.tr("Währungsbezeichnung"), control.getWaehrung());
    finanzGroup.addLabelPair(i18n.tr("Beginn des Geschäftsjahres"),control.getGJStart());
    finanzGroup.addLabelPair(i18n.tr("Ende des Geschäftsjahres"),control.getGJEnd());

    ButtonArea buttonArea = new ButtonArea(getParent(),control.storeAllowed() ? 3 : 2);
    buttonArea.addButton(i18n.tr("Zurück"), new Back(), null, !control.storeAllowed());
    buttonArea.addButton(i18n.tr("Löschen"), new MandantDelete(), getCurrentObject());
    if (control.storeAllowed())
    {
      buttonArea.addButton(i18n.tr("Speichern"), new Action()
      {
        public void handleAction(Object context) throws ApplicationException
        {
          control.handleStore();
        }
      },null,control.storeAllowed());
    }
    
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
  }
}

/*********************************************************************
 * $Log: MandantNeu.java,v $
 * Revision 1.14  2005/08/12 00:10:59  willuhn
 * @B bugfixing
 *
 * Revision 1.13  2005/08/10 17:48:02  willuhn
 * @C refactoring
 *
 * Revision 1.12  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/02/20 20:44:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/25 19:44:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.6  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2003/12/11 21:00:34  willuhn
 * @C refactoring
 *
 * Revision 1.4  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.3  2003/11/27 00:21:05  willuhn
 * @N Checks via insertCheck(), deleteCheck() updateCheck() in Business-Logik verlagert
 *
 * Revision 1.2  2003/11/25 00:22:17  willuhn
 * @N added Finanzamt
 *
 * Revision 1.1  2003/11/24 23:02:11  willuhn
 * @N added settings
 *
 **********************************************************************/