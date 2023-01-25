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
import de.willuhn.jameica.fibu.gui.action.BuchungDelete;
import de.willuhn.jameica.fibu.gui.action.BuchungDuplicate;
import de.willuhn.jameica.fibu.gui.action.BuchungReversal;
import de.willuhn.jameica.fibu.gui.controller.BuchungControl;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Erzeugt eine neue Buchung oder bearbeitet eine existierende.
 * @author willuhn
 */
public class BuchungNeu extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {

    // Headline malen
		GUI.getView().setTitle(i18n.tr("Buchung bearbeiten"));

    final BuchungControl control = new BuchungControl(this);
    
    // Gruppe Konto erzeugen
    SimpleContainer group = new SimpleContainer(getParent());
    group.addLabelPair(i18n.tr("Vorlage"),         control.getBuchungstemplate());
    
    group.addHeadline(i18n.tr("Eigenschaften"));
    group.addLabelPair(i18n.tr("Datum"),           control.getDatum());
    group.addLabelPair(i18n.tr("Text"),            control.getText());
    group.addLabelPair(i18n.tr("Soll-Konto"),      control.getSollKontoAuswahl());
    group.addLabelPair(i18n.tr("Haben-Konto"),     control.getHabenKontoAuswahl());
    group.addLabelPair(i18n.tr("Beleg-Nr."),       control.getBelegnummer());
    group.addLabelPair(i18n.tr("Brutto-Betrag"),   control.getBetrag());
    group.addLabelPair(i18n.tr("Steuersatz"),      control.getSteuer());
    group.addLabelPair(i18n.tr("Notiz"),           control.getKommentar());
    
    final Buchung b = control.getBuchung();
    Anlagevermoegen av = b.getAnlagevermoegen();
    if (av != null)
      group.addLabelPair(i18n.tr("Zugehöriges Anlagegut"), control.getAnlageVermoegenLink());
    else if (b.isNewObject())
      group.addCheckbox(control.getAnlageVermoegen(),i18n.tr("In Anlagevermögen übernehmen"));

    // wir machen das Datums-Feld zu dem mit dem Focus.
    control.getDatum().focus();

    boolean closed = Settings.getActiveGeschaeftsjahr().isClosed();
    if (closed)
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Buchung kann nicht mehr geändert werden, da das Geschäftsjahr abgeschlossen ist"),StatusBarMessage.TYPE_ERROR));


    final Button delete = new Button(i18n.tr("Löschen"), new BuchungDelete(), b,false,"user-trash-full.png");
    delete.setEnabled(!closed);

    final Button store = new Button(i18n.tr("Speichern"),x -> control.handleStore(false),null,false,"document-save.png");
    store.setEnabled(!closed);
    
    final Button duplicate = new Button(i18n.tr("Duplizieren..."), new Action() {
      
      @Override
      public void handleAction(Object context) throws ApplicationException
      {
        try
        {
          boolean ok = true;
          if (Application.getCallback().askUser(i18n.tr("Buchung vorher speichern?")))
            ok = control.handleStore(false);
          
          if (ok)
            new BuchungDuplicate().handleAction(b);
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (Exception e)
        {
          Logger.error("unable to duplicate booking",e);
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Duplizieren fehlgeschlagen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
        }
      }
    },null,false,"edit-copy.png");
    duplicate.setEnabled(!closed);

    final Button flip = new Button(i18n.tr("Soll/Haben tauschen"), x -> control.handleFlipAccounts(),null,false,"view-refresh.png");
    flip.setEnabled(!closed);

    final Button reversal = new Button(i18n.tr("Storno..."), new Action() {
      
      @Override
      public void handleAction(Object context) throws ApplicationException
      {
        try
        {
          boolean ok = true;
          if (Application.getCallback().askUser(i18n.tr("Buchung vorher speichern?")))
            ok = control.handleStore(false);
          
          if (ok)
            new BuchungReversal().handleAction(b);
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (Exception e)
        {
          Logger.error("unable to reverse booking",e);
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Erstellen der Storno-Buchung fehlgeschlagen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
        }
      }
    },null,false,"edit-undo.png");
    reversal.setEnabled(!closed);

    final Button storeNew = new Button(i18n.tr("Speichern + Neu"),x -> control.handleStore(true),null,true,"go-next.png");
    storeNew.setEnabled(!closed);
    
    // und noch die Abschicken-Knoepfe
    ButtonArea buttonArea = new ButtonArea();
    buttonArea.addButton(delete);
    buttonArea.addButton(flip);
    buttonArea.addButton(reversal);
    buttonArea.addButton(duplicate);
    buttonArea.addButton(store);
    buttonArea.addButton(storeNew);
    
    buttonArea.paint(getParent());
    
    
  }
}
