/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/Attic/AbstractExportAction.java,v $
 * $Revision: 1.5.2.1 $
 * $Date: 2008/08/04 22:33:16 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.io.Export;
import de.willuhn.jameica.fibu.io.VelocityExporter;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Klasse fuer die Exports.
 */
public abstract class AbstractExportAction implements ExportAction
{

  protected static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private static Settings settings = new Settings(AbstractExportAction.class);
  
  protected final static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

  
  private Date start       = null;
  private Date end         = null;
  private Konto startKonto = null;
  private Konto endKonto   = null;
  
  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    try
    {
      File file = getFile();
      Export export = new Export();
      export.addObject("start",getStart());
      export.addObject("end",getEnd());
      export.addObject("startkonto",getStartKonto());
      export.addObject("endkonto",getEndKonto());
      export.setTarget(new FileOutputStream(file));
      export.setTitle(getName());
      fill(export,context);

      VelocityExporter.export(export);

      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Auswertung erstellt"),StatusBarMessage.TYPE_SUCCESS));
      new Program().handleAction(file);
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (OperationCanceledException oce)
    {
      Logger.info(oce.getMessage());
    }
    catch (Exception e)
    {
      Logger.error("error while creating report",e);
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Auswertung: {0}",e.getMessage()));
    }
  }
  
  /**
   * Liefert die Zieldatei fuer die Auswertung.
   * @return die Zieldatei.
   * @throws OperationCanceledException
   * @throws ApplicationException
   */
  private File getFile() throws OperationCanceledException, ApplicationException
  {
    FileDialog fd = new FileDialog(GUI.getShell(),SWT.SAVE);
    fd.setText(i18n.tr("Bitte wählen Sie eine Datei aus, in der die Auswertung werden sollen."));
    fd.setFileName(getFilename());

    String path = settings.getString("lastdir",System.getProperty("user.home"));
    if (path != null && path.length() > 0)
      fd.setFilterPath(path);

    String s = fd.open();
      
    if (s == null || s.length() == 0)
      throw new OperationCanceledException(i18n.tr("Auswertung abgebrochen"));
      
    File file = new File(s);

    // Wir merken uns noch das Verzeichnis vom letzten mal
    settings.setAttribute("lastdir",file.getParent());

    if (file.exists())
    {
      try
      {
        String q = i18n.tr("Datei existiert bereits. Überschreiben?");
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
      catch (Exception e)
      {
        Logger.error("error while saving export file",e);
        throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Auswertung: {0}",e.getMessage()));
      }
    }
    
    return file;
  }
  
  /**
   * Liefert einen Vorschlag fuer den Dateinamen.
   * @return Vorschlag fuer den Dateinamen.
   */
  protected abstract String getFilename();

  /**
   * Befuellt den Export mit den Nutzdaten.
   * @param export der Export.
   * @param context der Context.
   * @throws ApplicationException
   * @throws RemoteException
   * @throws OperationCanceledException
   */
  protected abstract void fill(Export export, Object context) throws ApplicationException, RemoteException, OperationCanceledException;
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getEnd()
   */
  public Date getEnd()
  {
    return this.end;
  }

  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getStart()
   */
  public Date getStart()
  {
    return this.start;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#setEnd(java.util.Date)
   */
  public void setEnd(Date d)
  {
    this.end = d;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#setStart(java.util.Date)
   */
  public void setStart(Date d)
  {
    this.start = d;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getEndKonto()
   */
  public Konto getEndKonto()
  {
    return this.endKonto;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#getStartKonto()
   */
  public Konto getStartKonto()
  {
    return this.startKonto;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#setEndKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setEndKonto(Konto konto)
  {
    this.endKonto = konto;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.gui.action.ExportAction#setStartKonto(de.willuhn.jameica.fibu.rmi.Konto)
   */
  public void setStartKonto(Konto konto)
  {
    this.startKonto = konto;
  }
}


/*********************************************************************
 * $Log: AbstractExportAction.java,v $
 * Revision 1.5.2.1  2008/08/04 22:33:16  willuhn
 * @N UST-Voranmeldung aufgehuebscht ;)
 * @C Redesign Exporter
 *
 * Revision 1.5  2007/03/06 15:22:36  willuhn
 * @C Anlagevermoegen in Auswertungen ignorieren, wenn Anfangsbestand bereits 0
 * @B Formatierungsfehler bei Betraegen ("-0,00")
 * @C Afa-Buchungen werden nun auch als GWG gebucht, wenn Betrag zwar groesser als GWG-Grenze aber Afa-Konto=GWG-Afa-Konto (laut Einstellungen)
 *
 * Revision 1.4  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.3  2005/10/17 22:59:38  willuhn
 * @B bug 135
 *
 * Revision 1.2  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 * Revision 1.1  2005/10/04 23:36:13  willuhn
 * *** empty log message ***
 *
 **********************************************************************/