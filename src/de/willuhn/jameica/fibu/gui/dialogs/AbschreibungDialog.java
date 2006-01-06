/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/dialogs/AbschreibungDialog.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/01/06 00:05:51 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.dialogs;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.AbschreibungsBuchung;
import de.willuhn.jameica.fibu.rmi.Anlagevermoegen;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Ein Dialog zur Eingabe von Abschreibungsdaten.
 */
public class AbschreibungDialog extends AbstractDialog
{

	private I18N i18n;
  private Anlagevermoegen av = null;
  private AbschreibungsBuchung buchung = null;
  
  private DecimalInput betrag   = null;
  private TextInput bezeichnung = null;
  private LabelInput hinweis    = null;
  private KontoInput konto      = null;
  
  /**
   * ct.
   * @param av der Anlage-Gegenstand.
   * @param position
   */
  public AbschreibungDialog(Anlagevermoegen av, int position)
  {
    super(position);

    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
		this.setTitle(i18n.tr("Ausserplanmäßige Abschreibung"));
    this.av = av;
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    LabelGroup group = new LabelGroup(parent,i18n.tr("Eigenschaften der Abschreibungsbuchung"));
    group.addLabelPair(i18n.tr("Abschreibungskonto"), getKonto());
    group.addLabelPair(i18n.tr("Abschreibungsbetrag"), getBetrag());
    group.addLabelPair(i18n.tr("Buchungstext"), getBezeichnung());
    group.addLabelPair("", getHinweis());
    
    ButtonArea b = new ButtonArea(parent,2);
    b.addButton(i18n.tr("Übernehmen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        try
        {
          Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
          
          //////////////////////////////////////////////////////////////
          // Pruefung des Betrags
          double betrag = 0.0d;
          try
          {
            betrag = ((Double)getBetrag().getValue()).doubleValue();
          }
          catch (Exception e)
          {
            throw new ApplicationException(i18n.tr("Bitte geben Sie einen gültigen Betrag ein"));
          }
          
          if (betrag > av.getRestwert(jahr))
            throw new ApplicationException(i18n.tr("Betrag darf Restwert nicht überschreiten"));
          //////////////////////////////////////////////////////////////
          
          //////////////////////////////////////////////////////////////
          // Pruefung des Textes
          String text = (String) getBezeichnung().getValue();
          if (text == null || text.length() == 0)
            throw new ApplicationException(i18n.tr("Bitte geben Sie einen Buchungstext ein"));
          //////////////////////////////////////////////////////////////

          //////////////////////////////////////////////////////////////
          // Pruefung des Kontos
          Konto k = (Konto) getKonto().getValue();
          if (k == null)
            throw new ApplicationException(i18n.tr("Bitte geben Sie einen Abschreibungskonto ein"));
          
          Kontoart ka = k.getKontoArt();
          if (ka.getKontoArt() != Kontoart.KONTOART_AUFWAND)
            throw new ApplicationException(i18n.tr("Ausgewähltes Konto ist kein Aufwandskonto"));
          //////////////////////////////////////////////////////////////

          
          buchung = (AbschreibungsBuchung) Settings.getDBService().createObject(AbschreibungsBuchung.class,null);

          Date d = new Date();
          if (d.after(jahr.getEnde()))
          {
            // Wir setzen das Datum an den Anfang des letzten Tages damit immer noch
            // _vor_ dem Ende des Geschaeftsjahres liegt
            Calendar cal = Calendar.getInstance();
            cal.setTime(jahr.getEnde());
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,1);
            d = cal.getTime();
          }
          
          buchung.setDatum(d);
          buchung.setGeschaeftsjahr(jahr);
          buchung.setSollKonto(k);
          buchung.setHabenKonto(av.getKonto());
          buchung.setBelegnummer(buchung.getBelegnummer());
          buchung.setText(text);
          buchung.setBetrag(betrag);
        }
        catch (ApplicationException ae)
        {
          getHinweis().setValue(ae.getMessage());
          return;
        }
        catch (RemoteException e)
        {
          Logger.error("error while creating abschreibung",e);
          throw new ApplicationException(i18n.tr("Fehler beim Erzeugen der Abschreibungsbuchung"));
        }
        close();
      }
    });
    b.addButton(i18n.tr("Abbrechen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        throw new OperationCanceledException();
      }
    });
    
			
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer den abzuschreibenden Betrag.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  private DecimalInput getBetrag() throws RemoteException
  {
    if (this.betrag != null)
      return this.betrag;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    Mandant m = jahr.getMandant();
    double restwert = av.getRestwert(jahr);
    this.betrag = new DecimalInput(restwert, Fibu.DECIMALFORMAT);
    this.betrag.setComment(i18n.tr("in [0}, Restwert: {1} {0}", new String[]{m.getWaehrung(),Fibu.DECIMALFORMAT.format(restwert)}));
    return this.betrag;
  }
  
  /**
   * Liefert ein Text-Eingabe-Feld fuer die Bezeichnung.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  private TextInput getBezeichnung() throws RemoteException
  {
    if (this.bezeichnung != null)
      return this.bezeichnung;
    this.bezeichnung = new TextInput(i18n.tr("Ausserplanmäßige Abschreibung für {0}", av.getName()));
    return this.bezeichnung;
  }
  
  /**
   * Liefert ein Eingabefeld fuer das Konto.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  private KontoInput getKonto() throws RemoteException
  {
    if (konto != null)
      return konto;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator konten = jahr.getKontenrahmen().getKonten();
    konten.addFilter("(kontoart_id = " + Kontoart.KONTOART_AUFWAND + ")");
    konto = new KontoInput(konten,av.getAbschreibungskonto());
    return konto;
  }

  /**
   * Liefert einen Hinweis-Text.
   * @return Hinweis-Text.
   */
  private LabelInput getHinweis()
  {
    if (this.hinweis != null)
      return this.hinweis;
    this.hinweis = new LabelInput("");
    this.hinweis.setColor(Color.ERROR);
    return this.hinweis;
  }

  /**
   * Liefert die vorgefertigte Abschreibungsbuchung.
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return buchung;
  }

}


/**********************************************************************
 * $Log: AbschreibungDialog.java,v $
 * Revision 1.3  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.2  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.1  2006/01/04 00:53:48  willuhn
 * @B bug 166 Ausserplanmaessige Abschreibungen
 *
 **********************************************************************/