/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/ext/hibiscus/UmsatzListMenu.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/06/03 17:43:41 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.ext.hibiscus;

import java.util.Date;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.extension.Extension;
import de.willuhn.jameica.gui.extension.ExtensionRegistry;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.hbci.messaging.ObjectChangedMessage;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.hbci.rmi.UmsatzTyp;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Erweitert das Kontextmenu der Umsatzliste.
 * BUGZILLA 140 http://www.willuhn.de/bugzilla/show_bug.cgi?id=140
 */
public class UmsatzListMenu implements Extension
{
  private I18N i18n = null;
  
  /**
   * ct.
   */
  public UmsatzListMenu()
  {
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }

  /**
   * @see de.willuhn.jameica.gui.extension.Extension#extend(de.willuhn.jameica.gui.extension.Extendable)
   */
  public void extend(Extendable extendable)
  {
    if (extendable == null || !(extendable instanceof ContextMenu))
    {
      Logger.warn("invalid extendable (" + extendable.getClass().getName() + ", skipping extension");
      return;
    }
    
    ContextMenu menu = (ContextMenu) extendable;
    menu.addItem(ContextMenuItem.SEPARATOR);
    
    menu.addItem(new MyContextMenuItem(i18n.tr("in SynTAX übernehmen..."), new Action() {
    
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null)
          return;

        Umsatz[] umsaetze = null;
        if (context instanceof Umsatz)
          umsaetze = new Umsatz[]{(Umsatz)context};
        else if (context instanceof Umsatz[])
          umsaetze = (Umsatz[]) context;
        
        if (umsaetze == null || umsaetze.length == 0)
          return;

        // Wenn wir mehr als 1 Buchung haben, fuehren wir das
        // im Hintergrund aus. 
        Worker worker = new Worker(umsaetze);
        if (umsaetze.length > 1)
          Application.getController().start(worker);
        else
          worker.run(null);
      }
    }));
  }

  /**
   * Erzeugt eine einzelne Buchung.
   * Sie wird jedoch noch nicht gespeichert.
   * @param u die zu erzeugende Buchung.
   * @param true, wenn wir mehr als eine Buchung haben und im Automatik-Modus laufen.
   * In dem Fall wird die Erstellung der Buchung mit einer ApplicationException
   * abgebrochen, wenn keine Umsatz-Kategorie vorhanden ist oder dieser keine
   * Buchungsvorlage zugeordnet ist.
   * "Keine Buchungsvorlage zugeordnet" geworfen.
   * @return die erzeugte Buchung.
   * @throws Exception
   */
  private Buchung createBuchung(Umsatz u, boolean auto) throws Exception
  {
    // Checken, ob der Umsatz eine Kategorie hat
    UmsatzTyp typ = u.getUmsatzTyp();
    
    if (typ == null && auto)
      throw new ApplicationException(i18n.tr("Keine Kategorie zugeordnet"));
    
    de.willuhn.jameica.fibu.rmi.Buchungstemplate template = null;
    if (typ != null)
    {
      // Vorlage suchen
      DBIterator i = Settings.getDBService().createList(de.willuhn.jameica.fibu.rmi.Buchungstemplate.class);
      i.addFilter("hb_umsatztyp_id = ?",new Object[]{typ.getID()});
      if (i.hasNext())
        template = (de.willuhn.jameica.fibu.rmi.Buchungstemplate) i.next();
    }
    
    if (template == null && auto)
      throw new ApplicationException(i18n.tr("Kategorie \"{0}\" ist keiner Buchungsvorlage zugeordnet",typ.getName()));
    
    final Buchung buchung = (Buchung) Settings.getDBService().createObject(Buchung.class,null);
    buchung.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
    buchung.setHibiscusUmsatzID(u.getID());
    buchung.setBelegnummer(buchung.getBelegnummer()); // Das erzeugt eine neue Belegnummer
    
    // Wir nehmen erstmal die Daten der Vorlage.
    if (template != null)
    {
      Konto soll = template.getSollKonto();
      if (auto && soll == null)
        throw new ApplicationException(i18n.tr("Buchungsvorlage \"{0}\" enthält kein Soll-Konto",template.getName()));
      Konto haben = template.getHabenKonto();
      if (auto && haben == null)
        throw new ApplicationException(i18n.tr("Buchungsvorlage \"{0}\" enthält kein Haben-Konto",template.getName()));

      buchung.setBetrag(template.getBetrag());
      buchung.setDatum(new Date());
      buchung.setSollKonto(soll);
      buchung.setHabenKonto(haben);
      buchung.setSteuer(template.getSteuer());
      buchung.setText(template.getText());
    }

    // Wenn der Umsatz Werte mitbringt, ersetzen wir die gegen die von der Vorlage
    double brutto = u.getBetrag();
    if (brutto != 0.0 && !Double.isNaN(brutto))
    {
      // Die API erwartet Netto-Betraege, wir haben hier aber den Brutto-Betrag
      Math m = new Math();
      buchung.setBetrag(m.netto(brutto,buchung.getSteuer()));
    }
    
    Date date = u.getDatum();
    if (date != null)
      buchung.setDatum(date);
    
    // Wir werfen alle Verwendungszwecke zusammen
    String zweck = (String) u.getAttribute("mergedzweck");
    if (zweck != null && zweck.length() > 0)
    {
      // Noch abschneiden, falls er zu lang ist
      if (zweck.length() > 255)
        zweck = zweck.substring(0,255);
      buchung.setText(zweck);
    }
    return buchung;
  }

  /**
   * Hilfsklasse, um den Menupunkt zu deaktivieren, wenn die Buchung bereits zugeordnet ist.
   */
  private class MyContextMenuItem extends CheckedContextMenuItem
  {
    /**
     * ct.
     * @param text
     * @param a
     */
    public MyContextMenuItem(String text, Action a)
    {
      super(text, a);
    }

    /**
     * @see de.willuhn.jameica.gui.parts.CheckedContextMenuItem#isEnabledFor(java.lang.Object)
     */
    public boolean isEnabledFor(Object o)
    {
      if (o == null)
        return false;

      // Wenn wir eine ganze Liste von Buchungen haben, pruefen
      // wir nicht jede einzeln, ob sie schon in SynTAX vorhanden
      // ist. Die werden dann beim Import (weiter unten) einfach ausgesiebt.
      if (o instanceof Umsatz[])
        return super.isEnabledFor(o);
      
      if (!(o instanceof Umsatz))
        return false;

      boolean found = false;
      try
      {
        found = isAssigned((Umsatz) o);
      }
      catch (ApplicationException ae)
      {
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(), StatusBarMessage.TYPE_ERROR));
      }
      catch (Exception e)
      {
        Logger.error("unable to detect if buchung is allready assigned",e);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Prüfen, ob die Buchung bereits in SynTAX zugeordnet ist"), StatusBarMessage.TYPE_ERROR));
      }
      return !found && super.isEnabledFor(o);
    }
    
  }
  
  /**
   * Prueft, ob der Umsatz bereits einer Hibiscus-Buchung zugeordnet ist.
   * @param u der zu pruefende Umsatz.
   * @return true, wenn es bereits eine Buchung gibt.
   * @throws Exception
   */
  private boolean isAssigned(Umsatz u) throws Exception
  {
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    if (jahr == null)
      throw new ApplicationException(i18n.tr("Kein aktives Geschäftsjahr ausgewählt"));
    
    DBIterator list = jahr.getHauptBuchungen();
    list.addFilter("hb_umsatz_id = ?",new Object[]{u.getID()});
    return list.hasNext();
  }

  
  /**
   * Damit koennen wir lange Vorgaenge ggf. im Hintergrund laufen lassen
   */
  private class Worker implements BackgroundTask
  {
    private boolean cancel = false;
    private Umsatz[] list = null;

    /**
     * ct.
     * @param list
     */
    private Worker(Umsatz[] list)
    {
      this.list = list;
    }
    
    /**
     * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
     */
    public void interrupt()
    {
      this.cancel = true;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
     */
    public boolean isInterrupted()
    {
      return this.cancel;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
     */
    public void run(ProgressMonitor monitor) throws ApplicationException
    {
      try
      {
        if (monitor != null)
          monitor.setStatusText(i18n.tr("Buche {0} Umsätze",""+list.length));

        double factor = 100d / (double) list.length;
        
        int created = 0;
        int error   = 0;
        int skipped = 0;

        for (int i=0;i<list.length;++i)
        {
          if (monitor != null)
          {
            monitor.setPercentComplete((int)((i+1) * factor));
            monitor.log("  " + i18n.tr("Erstelle Buchung {0}",Integer.toString(i+1)));
          }

          Buchung buchung = null;
          try
          {
            // Checken, ob der Umsatz schon einer Buchung zugeordnet ist
            if (isAssigned(list[i]))
            {
              skipped++;
              continue;
            }
            
            buchung = createBuchung(list[i],list.length > 1);
            buchung.store();
            created++;
            
            // Mit der Benachrichtigung wird dann gleich die Buchungsnummer in der Liste
            // angezeigt. Vorher muessen wir der anderen Extension aber noch die neue
            // Buchung mitteilen
            List<Extension> extensions = ExtensionRegistry.getExtensions("de.willuhn.jameica.hbci.gui.parts.UmsatzList");
            if (extensions != null)
            {
              for (Extension e:extensions)
              {
                if (e instanceof UmsatzListPart)
                {
                  ((UmsatzListPart)e).add(buchung);
                  break;
                }
              }
            }
            Application.getMessagingFactory().sendMessage(new ObjectChangedMessage(list[i]));
          }
          catch (ApplicationException ae)
          {
            // Wenn wir nur eine Buchung hatten und eine
            // ApplicationException, dann fehlen noch Eingaben
            // Da wir nur eine Buchung haben, oeffnen wir
            // die Erfassungsmaske.
            if (list.length == 1)
            {
              Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
              new BuchungNeu().handleAction(buchung);
              return;
            }
            
            if (monitor != null)
              monitor.log("    " + ae.getMessage());
            error++;
          }
          catch (Exception e)
          {
            Logger.error("unable to import umsatz",e);
            if (monitor != null)
              monitor.log("    " + i18n.tr("Fehler: {0}",e.getMessage()));
            error++;
          }
        }
        
        String text = i18n.tr("Umsatz importiert");
        if (list.length > 1)
          text = i18n.tr("{0} Umsätze importiert, {1} fehlerhaft, {2} bereits vorhanden", new String[]{Integer.toString(created),Integer.toString(error),Integer.toString(skipped)});
        
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(text,StatusBarMessage.TYPE_SUCCESS));
        if (monitor != null)
        {
          monitor.setStatusText(text);
          monitor.setStatus(ProgressMonitor.STATUS_DONE);
        }
        
      }
      catch (Exception e)
      {
        Logger.error("error while importing objects",e);
        throw new ApplicationException(i18n.tr("Fehler beim Import der Umsätze"));
      }
    }
  }

}


/*********************************************************************
 * $Log: UmsatzListMenu.java,v $
 * Revision 1.7  2010/06/03 17:43:41  willuhn
 * @N Aussagekraeftigere Meldungen, wenn Kategorie oder Vorlage fehlt oder Vorlage unvollstaendig ist
 *
 * Revision 1.6  2010/06/03 17:18:14  willuhn
 * @N Bei mehr als einer Buchung im Hintergrund ausfuehren. Wenn man das erst ab 20 macht, hat man anschliessend keinen schoenen Ueberblick, bei welchen es geklemmt hat
 *
 * Revision 1.5  2010/06/03 17:07:14  willuhn
 * @N Erste Version der vollautomatischen Uebernahme von Umsatzen in Hibiscus!
 *
 * Revision 1.4  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.3.2.1  2009/01/09 11:19:41  willuhn
 * @B NPE wenn noch kein Geschaeftsjahr existiert
 *
 * Revision 1.3  2007/03/23 10:15:35  willuhn
 * @B classcastexception
 *
 * Revision 1.2  2006/10/12 21:51:10  willuhn
 * @Uebernahme der Buchungen aus Hibiscus.
 *
 * Revision 1.1  2006/10/09 23:48:41  willuhn
 * @B bug 140
 *
 **********************************************************************/