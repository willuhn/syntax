/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AuswertungControl.java,v $
 * $Revision: 1.5 $
 * $Date: 2009/07/03 10:52:18 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.controller;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.ExportData;
import de.willuhn.jameica.fibu.io.ExportRegistry;
import de.willuhn.jameica.fibu.messaging.ExportMessage;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.system.OperationCanceledException;
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
  private final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(Export.class);
  
  private SelectInput auswertungen = null;
  private Input jahr               = null;
  private DateInput start          = null;
  private DateInput end            = null;
  
  private KontoInput startKonto    = null;
  private KontoInput endKonto      = null;
  
  private Button startButton       = null;
  
  private CheckboxInput open       = null;
  
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
    
    auswertungen = new SelectInput(ExportRegistry.getExporters(),null);
    auswertungen.setPleaseChoose(i18n.tr("Bitte wählen..."));
    auswertungen.setAttribute("name");
    auswertungen.setComment("");
    auswertungen.addListener(new Listener()
    {
      public void handleEvent(Event event)
      {
        try
        {
          Export e = (Export) auswertungen.getValue();
          ExportData data = e == null ? null : e.createPreset();
          getJahr().setEnabled(data == null || data.isNeedGeschaeftsjahr());
          getStartKonto().setEnabled(data == null || data.isNeedKonto());
          getEndKonto().setEnabled(data == null || data.isNeedKonto());
          getStart().setEnabled(data == null || data.isNeedDatum());
          getEnd().setEnabled(data == null || data.isNeedDatum());
        }
        catch (Exception e)
        {
          Logger.error("unable to update view",e);
        }
      }
    });
    return auswertungen;
  }
  
  /**
   * Liefert das Jahr fuer die Auswertung.
   * @return Jahr.
   * @throws RemoteException
   */
  public Input getJahr() throws RemoteException
  {
    if (this.jahr != null)
      return this.jahr;
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    Mandant m = current.getMandant();
    
    this.jahr = new SelectInput(m.getGeschaeftsjahre(),current);
    this.jahr.setComment("");
    this.jahr.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        try
        {
          Geschaeftsjahr j = (Geschaeftsjahr) event.data;
          Date begin = j.getBeginn();
          Date end = j.getEnde();
          getStart().setValue(begin);
          getEnd().setValue(end);
          ((DialogInput)getStart()).setText(Fibu.DATEFORMAT.format(begin));
          ((DialogInput)getEnd()).setText(Fibu.DATEFORMAT.format(end));
        }
        catch (Exception e)
        {
          Logger.error("error while choosing jahr",e);
          GUI.getStatusBar().setErrorText(i18n.tr("Fehler bei der Auswahl des Geschäftsjahres"));
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
  public Input getStart() throws RemoteException
  {
    if (this.start != null)
      return this.start;
    
    this.start = new DateInput(Settings.getActiveGeschaeftsjahr().getBeginn(),Fibu.DATEFORMAT);
    this.start.setText(i18n.tr("Wählen Sie bitte den Beginn des Geschäftsjahres aus"));
    this.start.setTitle(i18n.tr("Beginn des Geschäftsjahres"));
    this.start.setComment("");
    return this.start;
  }
  
  /**
   * Liefert ein Auswahl-Feld fuer das Ende des Geschaeftsjahres.
   * @return Auswahl-Feld.
   * @throws RemoteException
   */
  public Input getEnd() throws RemoteException
  {
    if (this.end != null)
      return this.end;
    
    this.end = new DateInput(Settings.getActiveGeschaeftsjahr().getEnde(),Fibu.DATEFORMAT);
    this.end.setText(i18n.tr("Wählen Sie bitte das Ende des Geschäftsjahres aus"));
    this.end.setTitle(i18n.tr("Ende des Geschäftsjahres"));
    this.end.setComment("");
    return this.end;
  }
  
  /**
   * Liefert das Start-Konto, bei dem die Auswertung beginnen soll.
   * @return Start-Konto.
   * @throws RemoteException
   */
  public Input getStartKonto() throws RemoteException
  {
    if (this.startKonto != null)
      return this.startKonto;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.setOrder("order by kontonummer");
    
    this.startKonto = new KontoInput((Konto) list.next());
    return this.startKonto;
  }
  
  /**
   * Liefert das End-Konto, bei dem die Auswertung enden soll.
   * @return End-Konto.
   * @throws RemoteException
   */
  public Input getEndKonto() throws RemoteException
  {
    if (this.endKonto != null)
      return this.endKonto;

    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    DBIterator list = jahr.getKontenrahmen().getKonten();
    list.setOrder("order by kontonummer desc");
    
    this.endKonto = new KontoInput((Konto) list.next());
    return this.endKonto;
  }
  
  /**
   * Liefert den Start-Button.
   * @return der Start-Button.
   */
  public Button getStartButton()
  {
    if (this.startButton == null)
    {
      this.startButton = new Button(i18n.tr("Erstellen..."),new Action()
      {
        public void handleAction(Object context) throws ApplicationException
        {
          handleExecute();
        }
      },null,true);
    }
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
   * Fuehrt die ausgewaehlte Auswertung aus.
   */
  public void handleExecute()
  {
    try
    {
      final Export e = (Export) getAuswertungen().getValue();
      if (e == null)
        throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Auswertung aus."));

      getStartButton().setEnabled(false);
      final ExportData data = e.createPreset();
      data.setGeschaeftsjahr((Geschaeftsjahr)getJahr().getValue());
      data.setStartKonto((Konto)getStartKonto().getValue());
      data.setEndKonto((Konto)getEndKonto().getValue());
      data.setStartDatum((Date)getStart().getValue());
      data.setEndDatum((Date)getEnd().getValue());

      String dir = settings.getString("lastdir",System.getProperty("user.home"));
      FileDialog fd = new FileDialog(GUI.getShell(),SWT.SAVE);
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

      if (file.exists())
      {
        try
        {
          String q = i18n.tr("Die Datei {0} existiert bereits. Überschreiben?",file.getAbsolutePath());
          if (!Application.getCallback().askUser(q))
            throw new OperationCanceledException("Abgebrochen, User möchte Datei nicht überschreiben");
        }
        catch (OperationCanceledException oce)
        {
          throw oce;
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (Exception ex)
        {
          Logger.error("error while saving export file",ex);
          throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Auswertung: {0}",ex.getMessage()));
        }
      }
      data.setTarget(file.getAbsolutePath());
      
      Application.getController().start(new BackgroundTask()
      {
        public void run(ProgressMonitor monitor) throws ApplicationException
        {
          try
          {
            e.doExport(data,monitor);
          }
          finally
          {
            GUI.getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                getStartButton().setEnabled(true);
                if (((Boolean)getOpenAfterCreation().getValue()).booleanValue())
                  Application.getMessagingFactory().sendMessage(new ExportMessage(i18n.tr("Auswertung erstellt"),file));
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


/*********************************************************************
 * $Log: AuswertungControl.java,v $
 * Revision 1.5  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.4.2.3  2009/06/25 15:21:18  willuhn
 * @N weiterer Code fuer IDEA-Export
 *
 * Revision 1.4.2.2  2009/06/24 10:35:55  willuhn
 * @N Jameica 1.7 Kompatibilitaet
 * @N Neue Auswertungen funktionieren - werden jetzt im Hintergrund ausgefuehrt
 *
 * Revision 1.4.2.1  2009/06/23 16:53:22  willuhn
 * @N Velocity-Export komplett ueberarbeitet
 *
 * Revision 1.4  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.3  2006/01/04 17:59:11  willuhn
 * @B bug 171
 *
 * Revision 1.2  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.1  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 **********************************************************************/