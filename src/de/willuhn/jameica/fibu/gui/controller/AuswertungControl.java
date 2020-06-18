/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.controller;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.io.report.Report;
import de.willuhn.jameica.fibu.io.report.ReportData;
import de.willuhn.jameica.fibu.io.report.ReportRegistry;
import de.willuhn.jameica.fibu.messaging.ReportMessage;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Controller fuer die Auswertungen.
 */
public class AuswertungControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(Report.class);
  
  private SelectInput auswertungen  = null;
  private SelectInput jahr          = null;
  private DateInput start           = null;
  private DateInput end             = null;
  private LabelInput notiz          = null;
  
  private KontoInput startKonto     = null;
  private KontoInput endKonto       = null;
  
  private Button startButton        = null;
  
  private CheckboxInput leereKonten = null;
  private CheckboxInput open        = null;
  
  /**
   * @param view
   */
  public AuswertungControl(AbstractView view)
  {
    super(view);
  }
  
  /**
   * Liefert eine Liste der verfuegbaren Auswertungen.
   * @return Liste der Auswertungen.
   * @throws RemoteException
   */
  public SelectInput getAuswertungen() throws RemoteException
  {
    if (auswertungen != null)
      return auswertungen;
    
    final List<Report> reports = ReportRegistry.getReports();
    Report selected = null;
    final String name = StringUtils.trimToNull(settings.getString("report",null));
    if (name != null)
    {
      for (Report r:reports)
      {
        if (r.getClass().getName().equals(name))
        {
          selected = r;
          break;
        }
      }
    }
    
    final Listener l = new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        try
        {
          Report e = (Report) auswertungen.getValue();
          ReportData data = e == null ? null : e.createPreset();
          getJahr().setEnabled(data != null && data.isNeedGeschaeftsjahr());
          getStartKonto().setEnabled(data != null && data.isNeedKonto());
          getEndKonto().setEnabled(data != null && data.isNeedKonto());
          getStart().setEnabled(data != null && data.isNeedDatum());
          getEnd().setEnabled(data != null && data.isNeedDatum());
          getStartButton().setEnabled(data != null);
          getLeereKonten().setEnabled(data != null && data.isNeedLeereKonten());
          getOpenAfterCreation().setEnabled(data != null);
          
          settings.setAttribute("report",e != null ? e.getClass().getName() : (String) null);
        }
        catch (Exception e)
        {
          Logger.error("unable to update view",e);
        }
      }
    };
    auswertungen = new SelectInput(reports,selected);
    auswertungen.setName(i18n.tr("Art der Auswertung"));
    auswertungen.setPleaseChoose(i18n.tr("Bitte wählen..."));
    auswertungen.setAttribute("name");
    auswertungen.setComment("");
    auswertungen.addListener(l);
    
    // Einmal initial per Hand ausloesen
    l.handleEvent(null);
    
    return auswertungen;
  }
  
  /**
   * Liefert ein Notiz-Label.
   * @return Notiz-Label.
   * @throws RemoteException
   */
  public LabelInput getNotiz() throws RemoteException
  {
    if (this.notiz != null)
      return this.notiz;

    String comment = "";
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    if (!current.isClosed())
      comment = i18n.tr("Das aktuelle Geschäftsjahr ist noch nicht abgeschlossen. Abschreibungen wurden noch nicht gebucht.");
    this.notiz = new LabelInput(comment);
    this.notiz.setColor(Color.COMMENT);
    this.notiz.setName("");
    return this.notiz;
  }
  
  /**
   * Liefert das Jahr fuer die Auswertung.
   * @return Jahr.
   * @throws RemoteException
   */
  public SelectInput getJahr() throws RemoteException
  {
    if (this.jahr != null)
      return this.jahr;
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    Mandant m = current.getMandant();
    
    this.jahr = new SelectInput(m.getGeschaeftsjahre(),current);
    this.jahr.setName(i18n.tr("Geschäftsjahr"));
    this.jahr.setComment("");
    this.jahr.setEnabled(false);
    this.jahr.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        try
        {
          Geschaeftsjahr j = (Geschaeftsjahr) getJahr().getValue();
          if (j == null)
            return;
          
          getStart().setValue(j.getBeginn());
          getEnd().setValue(j.getEnde());
        }
        catch (Exception e)
        {
          Logger.error("error while updating dates",e);
          // Muessen wir dem User nicht anzeigen, die Werte kann er auch allein eintragen
        }
      }
    });
    return this.jahr;
  }
  
  /**
   * Liefert ein Auswahlfeld fuer den Beginn des Geschaeftsjahres.
   * @return Auswahl-Feld.
   * @throws RemoteException
   */
  public DateInput getStart() throws RemoteException
  {
    if (this.start != null)
      return this.start;

    this.start = new DateInput(this.getStoredDate("startdatum",Settings.getActiveGeschaeftsjahr().getBeginn()),Settings.CUSTOM_DATEFORMAT);
    this.start.setName(i18n.tr("Start-Datum"));
    this.start.setText(i18n.tr("Wählen Sie bitte den Beginn des Auswertungszeitraumes aus"));
    this.start.setTitle(i18n.tr("Beginn des Auswertungszeitraumes"));
    this.start.setEnabled(false);
    this.start.setComment("");
    this.start.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        try
        {
          Date d = (Date) getStart().getValue();
          setStoredDate("startdatum",d);
        }
        catch (Exception e)
        {
          Logger.error("unable to store date",e);
        }
      }
    });
    return this.start;
  }
  
  /**
   * Liefert ein Auswahl-Feld fuer das Ende des Geschaeftsjahres.
   * @return Auswahl-Feld.
   * @throws RemoteException
   */
  public DateInput getEnd() throws RemoteException
  {
    if (this.end != null)
      return this.end;
    
    this.end = new DateInput(this.getStoredDate("enddatum",Settings.getActiveGeschaeftsjahr().getEnde()),Settings.CUSTOM_DATEFORMAT);
    this.end.setText(i18n.tr("Wählen Sie bitte das Ende des Auswertungszeitraumes aus"));
    this.end.setName(i18n.tr("End-Datum"));
    this.end.setTitle(i18n.tr("Ende des Auswertungszeitraumes"));
    this.end.setEnabled(false);
    this.end.setComment("");
    this.end.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        try
        {
          Date d = (Date) getEnd().getValue();
          setStoredDate("enddatum",d);
        }
        catch (Exception e)
        {
          Logger.error("unable to store date",e);
        }
      }
    });
    return this.end;
  }
  
  /**
   * Speichert das Datum.
   * @param name der Name des Parameters.
   * @param d das Datum.
   */
  private void setStoredDate(String name, Date d)
  {
    if (d != null)
    {
      try
      {
        // Wenn ein Datum ausgewaehlt wurde, pruefen wir, dass es sich innerhalb des Geschaeftsjahres befindet
        Geschaeftsjahr jahr = (Geschaeftsjahr) getJahr().getValue();
        if (jahr != null && !jahr.check(d))
        {
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Das Datum {0} befindet sich ausserhalb des Geschäftsjahres",Settings.CUSTOM_DATEFORMAT.format(d)),StatusBarMessage.TYPE_ERROR));
          return;
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to check year",e);
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Prüfen des Geschäftsjahres: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
        return;
      }
    }
    settings.setAttribute(name,d != null ? Settings.CUSTOM_DATEFORMAT.format(d) : (String) null);
  }
  
  /**
   * Liefert das gespeicherte Datum.
   * @param name Name des Parameters in den Einstellungen.
   * @param def das Default-Datum, wenn keines oder kein gueltiges gespeichert wurde.
   * @return das Datum oder der Default-Wert (kann NULL sein), wenn keines oder kein gueltiges gespeichert wurde.
   */
  private Date getStoredDate(String name, Date def)
  {
    String s = StringUtils.trimToNull(settings.getString(name,null));
    if (s == null)
      return def;
    
    try
    {
      final Date d = Settings.CUSTOM_DATEFORMAT.parse(s);
      if (d == null)
        return def;
      
      // Jetzt noch checken, ob es sich im aktuellen Geschaeftsjahr befindet
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      if (jahr == null)
        return d; // Kein Geschaeftsjahr ausgewaehlt, dann koennen wir es nicht ueberpruefen.
      
      if (jahr.check(d))
        return d;
      
      // Befindet sich ausserhalb des Geschaeftsjahres
      return def;
    }
    catch (Exception e)
    {
      // Ignore
    }
    return def;
  }
  
  /**
   * Liefert das Start-Konto, bei dem die Auswertung beginnen soll.
   * @return Start-Konto.
   * @throws RemoteException
   */
  public KontoInput getStartKonto() throws RemoteException
  {
    if (this.startKonto != null)
      return this.startKonto;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.setOrder("order by kontonummer");
    
    this.startKonto = new KontoInput(this.getStoredKonto("startkonto",(Konto) list.next()));
    this.startKonto.setName(i18n.tr("von"));
    this.startKonto.setEnabled(false);
    this.startKonto.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        try
        {
          setStoredKonto("startkonto",getStartKonto().getKonto());
        }
        catch (RemoteException re)
        {
          Logger.error("unable to store account",re);
        }
      }
    });
    return this.startKonto;
  }
  
  /**
   * Liefert das End-Konto, bei dem die Auswertung enden soll.
   * @return End-Konto.
   * @throws RemoteException
   */
  public KontoInput getEndKonto() throws RemoteException
  {
    if (this.endKonto != null)
      return this.endKonto;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.setOrder("order by kontonummer desc");
    
    this.endKonto = new KontoInput(this.getStoredKonto("endkonto",(Konto) list.next()));
    this.endKonto.setName(i18n.tr("bis"));
    this.endKonto.setEnabled(false);
    this.endKonto.addListener(new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        try
        {
          setStoredKonto("endkonto",getEndKonto().getKonto());
        }
        catch (RemoteException re)
        {
          Logger.error("unable to store account",re);
        }
      }
    });
    return this.endKonto;
  }
  
  /**
   * Speichert das Konto.
   * @param name der Name des Parameters.
   * @param k das Konto.
   */
  private void setStoredKonto(String name, Konto k)
  {
    try
    {
      if (k != null)
      {
        // Pruefen, ob es sich im Kontenrahmen des aktuellen Geschaeftsjahres befindet.
        Geschaeftsjahr jahr = (Geschaeftsjahr) getJahr().getValue();
        if (jahr != null)
        {
          DBIterator<Konto> konten = jahr.getKontenrahmen().getKonten();
          boolean found = false;
          while (konten.hasNext())
          {
            Konto check = konten.next();
            if (check.getID().equals(k.getID()))
            {
              found = true;
              break;
            }
          }
          
          if (!found)
          {
            Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Das Konto {0} befindet sich nicht im aktuellen Kontenrahmen",k.getKontonummer()),StatusBarMessage.TYPE_ERROR));
            return;
          }
        }
        
      }
      settings.setAttribute(name,k != null ? k.getID() : (String) null);
    }
    catch (RemoteException re)
    {
      Logger.error("unable to store account",re);
    }
  }
  
  /**
   * Liefert das gespeicherte Konto.
   * @param name der Name des Parameters.
   * @param def das Default-Konto, wenn keines oder kein gueltiges gespeichert war.
   * @return das gespeicherte Konto oder der Default-Wert.
   */
  private Konto getStoredKonto(String name, Konto def)
  {
    String id = StringUtils.trimToNull(settings.getString(name,null));
    if (id == null)
      return def;
    
    try
    {
      // Im aktuellen Kontenrahmen suchen
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      DBIterator<Konto> list = jahr.getKontenrahmen().getKonten();
      while (list.hasNext())
      {
        Konto k = list.next();
        if (k.getID().equals(id))
          return k;
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to load stored account",e);
    }
    return def;
  }
  
  
  /**
   * Liefert den Start-Button.
   * @return der Start-Button.
   */
  public Button getStartButton()
  {
    if (this.startButton != null)
      return this.startButton;
    
    this.startButton = new Button(i18n.tr("Auswertung erstellen..."),new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        handleExecute();
      }
    },null,true,"x-office-spreadsheet.png");
    this.startButton.setEnabled(false);
    return this.startButton;
  }
  
  /**
   * Liefert eine Checkbox, mit der der User festlegen kann, ob die
   * Auswertung nach der Erstellung geoeffnet werden soll.
   * @return Checkbox.
   */
  public CheckboxInput getOpenAfterCreation()
  {
    if (this.open == null)
    {
      this.open = new CheckboxInput(settings.getBoolean("open",true));
      this.open.setName(i18n.tr("Auswertung nach der Erstellung öffnen"));
      this.open.setEnabled(false);
      this.open.addListener(new Listener()
      {
        public void handleEvent(Event event)
        {
          settings.setAttribute("open",((Boolean)open.getValue()).booleanValue());
        }
      });
    }
    return this.open;
  }

  
  /**
   * Liefert eine Checkbox, mit der der User festlegen kann, ob
   * auch Konten ohne Buchungen mit ausgegeben werden sollen.
   * @return Checkbox.
   */
  public CheckboxInput getLeereKonten()
  {
    if (this.leereKonten == null)
    {
      this.leereKonten = new CheckboxInput(settings.getBoolean("leerekonten",false));
      this.leereKonten.setName(i18n.tr("Auch Konten ohne Buchungen mit ausgeben"));
      this.leereKonten.setEnabled(false);
      this.leereKonten.addListener(new Listener()
      {
        public void handleEvent(Event event)
        {
          settings.setAttribute("leerekonten",((Boolean)leereKonten.getValue()).booleanValue());
        }
      });
    }
    return this.leereKonten;
  }

  /**
   * Fuehrt die ausgewaehlte Auswertung aus.
   */
  public void handleExecute()
  {
    try
    {
      final Report e = (Report) getAuswertungen().getValue();
      if (e == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Auswertung aus."));

      Geschaeftsjahr jahr = (Geschaeftsjahr) getJahr().getValue();
      if (jahr == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Geschäftsjahr aus."));
      
      Date start = (Date) getStart().getValue();
      Date end   = (Date) getEnd().getValue();
      if (start != null) start = DateUtil.startOfDay(start);
      if (end != null)   end =   DateUtil.endOfDay(end);
      
      if (start != null && !jahr.check(start))
        throw new ApplicationException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(start)));

      if (end != null && !jahr.check(end))
        throw new ApplicationException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(end)));
      
      // wir machen hier nicht "getValue()" sondern "getKonto()" damit wir auch
      // dann einen aktuellen Wert haben, wenn der User direkt aus dem Eingabefeld
      // heraus ENTER drueckt - dann wird das "handleExecute()" naemlich ausgeloest,
      // *bevor* der Listener in KontoInput sein Value aktualisieren konnte. 
      Konto ks = getStartKonto().getKonto();
      Konto ke = getEndKonto().getKonto();
      
      if (ks != null && ke != null)
      {
        // Checken, dass das End-Konto groesser als das Start-Konto ist
        try
        {
          int from = Integer.parseInt(ks.getKontonummer());
          int to   = Integer.parseInt(ke.getKontonummer());
          if (to < from)
            throw new ApplicationException(i18n.tr("\"von\"- und \"bis\"-Konto vertauscht?"));
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (Exception ex)
        {
          Logger.error("invalid account number",ex);
        }
      }
      

      getStartButton().setEnabled(false);
      
      final ReportData data = e.createPreset();
      data.setGeschaeftsjahr(jahr);
      data.setStartDatum(start);
      data.setEndDatum(end);
      data.setStartKonto(ks);
      data.setEndKonto(ke);
      data.setLeereKonten(((Boolean)getLeereKonten().getValue()).booleanValue());

      String dir = settings.getString("lastdir",System.getProperty("user.home"));
      FileDialog fd = new FileDialog(GUI.getShell(),SWT.SAVE);
      fd.setOverwrite(true);
      fd.setText(i18n.tr("Bitte wählen Sie eine Datei aus, in der die Auswertung gespeichert werden sollen."));
      fd.setFileName(data.getTarget());
      
      if (dir != null)
        fd.setFilterPath(dir);

      String s = fd.open();
        
      if (s == null || s.length() == 0)
        throw new OperationCanceledException(i18n.tr("Auswertung abgebrochen"));
        
      final File file = new File(s);

      // Wir merken uns noch das Verzeichnis vom letzten mal
      settings.setAttribute("lastdir",file.getParent());
      data.setTarget(file.getAbsolutePath());
      
      Application.getController().start(new BackgroundTask()
      {
        public void run(ProgressMonitor monitor) throws ApplicationException
        {
          try
          {
            e.doReport(data,monitor);
            GUI.getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                if (((Boolean)getOpenAfterCreation().getValue()).booleanValue())
                  Application.getMessagingFactory().sendMessage(new ReportMessage(i18n.tr("Auswertung erstellt"),file));
              }
            });
          }
          finally
          {
            GUI.getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                getStartButton().setEnabled(true);
              }
            });
          }
        }
      
        /**
         * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
         */
        public boolean isInterrupted()
        {
          return false;
        }
      
        /**
         * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
         */
        public void interrupt()
        {
        }
      });
    }
    catch (ApplicationException ae)
    {
      getStartButton().setEnabled(true);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
    }
    catch (OperationCanceledException oce)
    {
      getStartButton().setEnabled(true);
      Logger.debug(oce.getMessage());
    }
    catch (Exception e)
    {
      getStartButton().setEnabled(true);
      Logger.error("unable to create report",e);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Erstellen der Auswertung: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
    }
    
  }
}
