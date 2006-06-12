/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FirstStartControl.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/06/12 23:05:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.FirstStart;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.PasswordInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.ProgressBar;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ClassFinder;
import de.willuhn.util.I18N;

/**
 * Controller fuer die Einrichtung der Datenbank und des Mandanten.
 */
public class FirstStartControl extends AbstractControl
{
  private I18N i18n = null;
  
  private SelectInput inputDbType      = null;
  private TextInput inputUsername      = null;
  private TextInput inputDbname        = null;
  private PasswordInput inputPassword  = null;
  private PasswordInput inputPassword2 = null;
  private TextInput inputHostname      = null;
  private IntegerInput inputPort       = null;
  
  private ProgressBar monitor          = null;
  
  private DBSupport dbType             = null;
  
  /**
   * @param view
   */
  public FirstStartControl(AbstractView view)
  {
    super(view);
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }
  
  /**
   * Liefert eine Combo-Box zur Auswahl der Datenbank.
   * @return Combo-Box.
   * @throws RemoteException
   */
  public SelectInput getDBType() throws RemoteException
  {
    if (this.inputDbType == null)
    {
      ClassFinder finder = Application.getClassLoader().getClassFinder();
      Class[] dbs = finder.findImplementors(DBSupport.class);
      
      ArrayList list = new ArrayList();
      for (int i=0;i<dbs.length;++i)
      {
        Logger.info("trying to init dbsupport " + dbs[i].getName());
        try
        {
          list.add(dbs[i].newInstance());
        }
        catch (Throwable t)
        {
          Logger.error("unable to init dbsupport " + dbs[i].getName() + ", skipping",t);
        }
      }
      DBSupport[] databases = (DBSupport[]) list.toArray(new DBSupport[list.size()]);
      inputDbType = new SelectInput(PseudoIterator.fromArray(databases),this.dbType);
      
      
      Listener l = new Listener() {
        public void handleEvent(Event event)
        {
          try
          {
            DBSupport s = (DBSupport) inputDbType.getValue();
            if(s.needsUsername())  getUsername().enable();
            else                   getUsername().disable();

            if(s.needsDatabaseName()) getDBName().enable();
            else                      getDBName().disable();

            if(s.needsHostname()) getHostname().enable();
            else                  getHostname().disable();

            if(s.needsPassword()) getPassword().enable();
            else                  getPassword().disable();

            if(s.needsTcpPort()) getPort().enable();
            else                 getPort().disable();

          }
          catch(RemoteException e)
          {
            Logger.error("unable to apply database configuration",e);
            GUI.getView().setErrorText(i18n.tr("Fehler beim Übernehmen der Datenbank-Konfiguration"));
          }
        }
      };
      
      inputDbType.addListener(l);
      l.handleEvent(null);
    }
    return inputDbType;
  }

  /**
   * Eingabe-Feld fuer den Usernamen.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public TextInput getUsername() throws RemoteException
  {
    if (this.inputUsername == null)
    {
      this.inputUsername = new TextInput(((DBSupport)getDBType().getValue()).getUsername());
      this.inputUsername.setComment(i18n.tr("Username des Datenbank-Benutzers"));
    }
    return this.inputUsername;
  }

  /**
   * Eingabe-Feld fuer den Namen der Datenbank.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public TextInput getDBName() throws RemoteException
  {
    if (this.inputDbname == null)
    {
      this.inputDbname = new TextInput(((DBSupport)getDBType().getValue()).getDatabaseName());
      this.inputDbname.setComment(i18n.tr("Name der Datenbank"));
    }
    return this.inputDbname;
  }

  /**
   * Eingabe-Feld fuer das Passwort.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public PasswordInput getPassword() throws RemoteException
  {
    if (this.inputPassword == null)
    {
      this.inputPassword = new PasswordInput(((DBSupport)getDBType().getValue()).getPassword());
      this.inputPassword.setComment(i18n.tr("Passwort des Datenbank-Benutzers"));
    }
    return this.inputPassword;
  }

  /**
   * Eingabe-Feld fuer das zweite Passwort zur Kontrolle.
   * @return Eingabe-Feld.
   */
  public PasswordInput getPassword2()
  {
    if (this.inputPassword2 == null)
    {
      this.inputPassword2 = new PasswordInput("");
      this.inputPassword2.setComment(i18n.tr("Geben Sie hier das Passwort nochmal zur Kontrolle ein"));
    }
    return this.inputPassword2;
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer den Hostnamen der Datenbank.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public TextInput getHostname() throws RemoteException
  {
    if (this.inputHostname == null)
    {
      this.inputHostname = new TextInput(((DBSupport)getDBType().getValue()).getHostname());
      this.inputHostname.setComment(i18n.tr("Hostname des Datenbank-Servers"));
    }
    return this.inputHostname;
  }
  
  /**
   * Eingabe-Feld fuer den TCP-Port der Datenbank. 
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public IntegerInput getPort() throws RemoteException
  {
    if (this.inputPort == null)
    {
      this.inputPort = new IntegerInput(((DBSupport)getDBType().getValue()).getTcpPort());
      this.inputPort.setComment(i18n.tr("TCP-Port des Datenbank-Servers"));
    }
    return this.inputPort;
  }
  
  /**
   * Zeigt einen Fortschrittsbalken fuer die Erstellung der Datenbank an.
   * @return Fortschrittsbalken.
   */
  public ProgressBar getProgressMonitor()
  {
    if (this.monitor == null)
    {
      this.monitor = new ProgressBar();
      this.monitor.showLogs(false);
    }
    return this.monitor;
  }

  /**
   * Wechselt zur Startseite des Wizards.
   *
   */
  public void handleFirstStart()
  {
    try
    {
      handleApply();
      new FirstStart().handleAction(this);
    }
    catch (ApplicationException ae)
    {
      GUI.getView().setErrorText(ae.getMessage());
    }
    catch (RemoteException re)
    {
      Logger.error("error while starting wizard",re);
      GUI.getView().setErrorText(i18n.tr("Fehler beim Starten des Wizards"));
    }
  }
  
  /**
   * Erstellt die ausgewaehlte Datenbank.
   */
  public void handle1CreateDatabase()
  {
    try
    {
      handleApply();
      this.dbType.test();
    }
    catch (ApplicationException ae)
    {
      GUI.getView().setErrorText(ae.getMessage());
      return;
    }
    catch (RemoteException re)
    {
      Logger.error("error while checking database",re);
      GUI.getView().setErrorText(i18n.tr("Fehler beim Testen der Datenbank"));
      return;
    }

    
    
    
  }

  /**
   * Uebernimmt die Daten des Formulars.
   * @throws RemoteException
   */
  public void handleApply() throws RemoteException
  {
    this.dbType = (DBSupport) getDBType().getValue();
    this.dbType.setUsername((String) getUsername().getValue());
    this.dbType.setPassword((String) getPassword().getValue());
    this.dbType.setHostname((String) getHostname().getValue());
    this.dbType.setDatabaseName((String) getDBName().getValue());

    Integer p = (Integer) getPort().getValue();
    if (p != null)
      this.dbType.setTcpPort(p.intValue());

//    if (dbname == null || dbname.length() == 0)
//      throw new ApplicationException(i18n.tr("Bitte geben Sie den Namen der Datenbank an"));
//    if (username == null || username.length() == 0)
//      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Benutzernamen an"));
//    if (hostname == null || hostname.length() == 0)
//      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Hostnamen für die Datenbank an"));
//    if (password == null || password.length() == 0)
//      throw new ApplicationException(i18n.tr("Bitte geben Sie ein Passwort für die Datenbank an"));
//    if (!password.equals(password2))
//      throw new ApplicationException(i18n.tr("Die beiden Passwörter stimmen nicht überein"));
//    if (port <= 0 || port > 65535)
//      throw new ApplicationException(i18n.tr("Bitte geben Sie einen gültigen TCP-Port ein"));

  }
  
  /**
   * Resettet das Formular.
   */
  public void handleReset()
  {
    this.inputDbname    = null;
    this.inputDbType    = null;
    this.inputHostname  = null;
    this.inputPassword  = null;
    this.inputPassword2 = null;
    this.inputPort      = null;
    this.inputUsername  = null;
  }
}


/*********************************************************************
 * $Log: FirstStartControl.java,v $
 * Revision 1.3  2006/06/12 23:05:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/12 15:41:18  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/