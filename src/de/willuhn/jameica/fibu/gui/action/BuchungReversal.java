/**********************************************************************
 *
 * Copyright (c) 2020 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Erstellt eine Storno-Buchung.
 */
public class BuchungReversal implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  @Override
  public void handleAction(Object context) throws ApplicationException
  {
    if (!(context instanceof Buchung))
      throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Buchung aus"));
    
    try
    {
      Buchung b = (Buchung) context;
      Buchung copy = Settings.getDBService().createObject(Buchung.class,null);
      copy.setBetrag(b.getBetrag());
      copy.setBruttoBetrag(b.getBruttoBetrag());
      copy.setDatum(b.getDatum());
      copy.setGeschaeftsjahr(b.getGeschaeftsjahr());
      copy.setHabenKonto(b.getSollKonto());
      copy.setKommentar(b.getKommentar());
      copy.setSollKonto(b.getHabenKonto());
      copy.setSteuer(b.getSteuer());
      copy.setText(i18n.tr("Storno von Buchung Nr. {0}: {1}",Integer.toString(b.getBelegnummer()),b.getText()));
      
      new BuchungNeu().handleAction(copy);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Storno-Buchung Nr. {0} erstellt für Buchung Nr. {1}",Integer.toString(copy.getBelegnummer()), Integer.toString(b.getBelegnummer())),StatusBarMessage.TYPE_SUCCESS));
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("unable to reverse booking",e);
      throw new ApplicationException(i18n.tr("Erstellen derStorno-Buchung fehlgeschlagen: {0}",e.getMessage()));
    }
  }

}


