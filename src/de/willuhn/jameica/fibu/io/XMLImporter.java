/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/io/XMLImporter.java,v $
 * $Revision: 1.2 $
 * $Date: 2010/08/27 11:21:31 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.io;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.datasource.serialize.ObjectFactory;
import de.willuhn.datasource.serialize.Reader;
import de.willuhn.datasource.serialize.XmlReader;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.messaging.ObjectImportedMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Importer fuer das Hibiscus-eigene XML-Format.
 */
public class XMLImporter implements Importer
{

  protected final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.fibu.io.Importer#doImport(java.lang.Object, de.willuhn.jameica.fibu.io.IOFormat, java.io.InputStream, de.willuhn.util.ProgressMonitor)
   */
  public void doImport(Object context, IOFormat format, InputStream is, ProgressMonitor monitor) throws RemoteException, ApplicationException
  {

    if (is == null)
      throw new ApplicationException(i18n.tr("Keine zu importierende Datei ausgewählt"));
    
    if (format == null)
      throw new ApplicationException(i18n.tr("Kein Datei-Format ausgewählt"));

    final ClassLoader loader = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getClassLoader();
    Reader reader = null;
    try
    {
      reader = new XmlReader(is, new ObjectFactory() {
        public GenericObject create(String type, String id, Map values) throws Exception
        {
          AbstractDBObject object = (AbstractDBObject) Settings.getDBService().createObject(loader.loadClass(type),null);
          // object.setID(id); // Keine ID angeben, da wir die Daten neu anlegen wollen
          Iterator i = values.keySet().iterator();
          while (i.hasNext())
          {
            String name = (String) i.next();
            object.setAttribute(name,values.get(name));
          }
          return object;
        }
      
      });
      
      if (monitor != null)
        monitor.setStatusText(i18n.tr("Lese Datei ein"));

      int created = 0;
      int error   = 0;

      DBObject object = null;
      while ((object = (DBObject) reader.read()) != null)
      {
        if (monitor != null)
        {
          monitor.log(i18n.tr("Datensatz {0}", "" + (created+1)));
          if (created > 0 && created % 10 == 0) // nur geschaetzt
            monitor.addPercentComplete(1);
        }

        try
        {
          object.store();
          created++;
          try
          {
            Application.getMessagingFactory().sendMessage(new ObjectImportedMessage(object));
          }
          catch (Exception ex)
          {
            Logger.error("error while sending import message",ex);
          }
        }
        catch (ApplicationException ae)
        {
          monitor.log("  " + ae.getMessage());
          error++;
        }
        catch (Exception e)
        {
          Logger.error("unable to import line",e);
          monitor.log("  " + i18n.tr("Fehler beim Import des Datensatzes: {0}",e.getMessage()));
          error++;
        }
      }
      monitor.setStatusText(i18n.tr("{0} Datensätze erfolgreich importiert, {1} fehlerhafte übersprungen", new String[]{""+created,""+error}));
      monitor.setPercentComplete(100);
    }
    catch (OperationCanceledException oce)
    {
      Logger.warn("operation cancelled");
      throw new ApplicationException(i18n.tr("Import abgebrochen"));
    }
    catch (Exception e)
    {
      Logger.error("error while reading file",e);
      throw new ApplicationException(i18n.tr("Fehler beim Import der XML-Datei"));
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException e)
        {
          Logger.error("error while closing inputstream",e);
        }
      }
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.io.IO#getName()
   */
  public String getName()
  {
    return i18n.tr("SynTAX-Format");
  }

  /**
   * @see de.willuhn.jameica.fibu.io.IO#getIOFormats(java.lang.Class)
   */
  public IOFormat[] getIOFormats(Class objectType)
  {
    if (!GenericObject.class.isAssignableFrom(objectType))
      return null; // Import fuer alles moeglich, was von GenericObject abgeleitet ist
    
    IOFormat f = new IOFormat() {
      public String getName()
      {
        return XMLImporter.this.getName();
      }

      /**
       * @see de.willuhn.jameica.fibu.io.IOFormat#getFileExtensions()
       */
      public String[] getFileExtensions()
      {
        return new String[] {"*.xml"};
      }
    };
    return new IOFormat[] { f };
  }
}

/*******************************************************************************
 * $Log: XMLImporter.java,v $
 * Revision 1.2  2010/08/27 11:21:31  willuhn
 * @C Java-Version auf 1.6 geaendert - wegen Hibiscus
 *
 * Revision 1.1  2010/08/27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 ******************************************************************************/