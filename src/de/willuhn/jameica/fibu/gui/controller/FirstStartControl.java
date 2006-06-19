/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FirstStartControl.java,v $
 * $Revision: 1.5 $
 * $Date: 2006/06/19 16:25:42 $
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

import de.willuhn.datasource.Service;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.FirstStart;
import de.willuhn.jameica.fibu.gui.action.FirstStart1CreateDatabase;
import de.willuhn.jameica.fibu.gui.action.FirstStart2CreateFinanzamt;
import de.willuhn.jameica.fibu.gui.action.FirstStart3CreateMandant;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
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
  
  private DBSupport[] dbTypes          = null;
  private DBSupport dbType             = null;
  
  private boolean isForward            = true;
  private ArrayList pages              = new ArrayList();
  private int wizardIndex              = 0;
  
  /**
   * @param view
   */
  public FirstStartControl(AbstractView view)
  {
    super(view);
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    this.pages.add(new FirstStart1());
    this.pages.add(new FirstStart2());
    this.pages.add(new FirstStart3());
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
      if (this.dbTypes == null)
      {
        ClassFinder finder = Application.getClassLoader().getClassFinder();
        Class[] dbs = null;
        
        try
        {
          dbs = finder.findImplementors(DBSupport.class);
        }
        catch (ClassNotFoundException cnf)
        {
          throw new RemoteException("unable to find implementors for " + DBSupport.class.getName(),cnf);
        }
        
        ArrayList list = new ArrayList();
        for (int i=0;i<dbs.length;++i)
        {
          Logger.debug("trying to init dbsupport " + dbs[i].getName());
          try
          {
            list.add(dbs[i].newInstance());
          }
          catch (Throwable t)
          {
            Logger.error("unable to init dbsupport " + dbs[i].getName() + ", skipping",t);
          }
        }
        this.dbTypes = (DBSupport[]) list.toArray(new DBSupport[list.size()]);
      }
      inputDbType = new SelectInput(PseudoIterator.fromArray(this.dbTypes),this.dbType);
      
      
      Listener l = new Listener() {
        public void handleEvent(Event event)
        {
          try
          {
            dbType = (DBSupport) inputDbType.getValue();
            if (dbType == null)
              return;

            getUsername().setEnabled(dbType.needsUsername());
            getDBName().setEnabled(dbType.needsDatabaseName());
            getHostname().setEnabled(dbType.needsHostname());
            getPassword().setEnabled(dbType.needsPassword());
            getPassword2().setEnabled(dbType.needsPassword());
            getPort().setEnabled(dbType.needsTcpPort());
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
   * Geht einen Schritt zurueck.
   */
  public void handleBack()
  {
    if (this.wizardIndex <= 0)
    {
      Logger.info("begin of wizard reached");
      return;
    }
    
    this.wizardIndex--;
    Action action = (Action)this.pages.get(this.wizardIndex);
    Logger.info("launch " + action.getClass().getName());
    try
    {
      isForward = false;
      action.handleAction(null);
    }
    catch (ApplicationException ae)
    {
      GUI.getView().setErrorText(ae.getLocalizedMessage());
    }
    catch (Throwable t)
    {
      Logger.error("unable to execute action",t);
      GUI.getView().setErrorText(i18n.tr("Fehler: {0}",t.getLocalizedMessage()));
    }
  }
  
  /**
   * Geht einen Schritt vor.
   */
  public void handleForward()
  {
    if (this.wizardIndex > (this.pages.size() - 1))
    {
      Logger.info("end of wizard reached");
      return;
    }
    
    Action action = (Action)this.pages.get(this.wizardIndex);
    Logger.info("launch " + action.getClass().getName());
    try
    {
      isForward = true;
      action.handleAction(null);
      this.wizardIndex++;
    }
    catch (ApplicationException ae)
    {
      GUI.getView().setErrorText(ae.getLocalizedMessage());
    }
    catch (Throwable t)
    {
      Logger.error("unable to execute action",t);
      GUI.getView().setErrorText(i18n.tr("Fehler: {0}",t.getLocalizedMessage()));
    }
  }
  
  /**
   * Wird aufgerufen, bevor jede Seite des Wizard geladen wird.
   */
  public void handleBind()
  {
    this.inputDbname    = null;
    this.inputDbType    = null;
    this.inputHostname  = null;
    this.inputPassword  = null;
    this.inputPassword2 = null;
    this.inputPort      = null;
    this.inputUsername  = null;
  }
  
  /**
   * Wird aufgerufen, wenn jede Seite des Wizward verlassen wird.
   */
  public void handleUnbind()
  {
    
  }
  
  
  /**
   * Action, die fuer Seite 1 des Wizards ausgefuehrt wird.
   */
  private class FirstStart1 implements Action
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      if (isForward)
      {
        new FirstStart1CreateDatabase().handleAction(FirstStartControl.this);
      }
      else
      {
        try
        {
          dbType = (DBSupport) getDBType().getValue();
          new FirstStart().handleAction(FirstStartControl.this);
        }
        catch (RemoteException re)
        {
          Logger.error("error while applying database",re);
          throw new ApplicationException(i18n.tr("Fehler beim Übernehmen der Datenbank-Einstellungen. {0}",re.getLocalizedMessage()));
        }
      }
    }
  }


  /**
   * Action, die fuer Seite 2 des Wizards ausgefuehrt wird.
   */
  private class FirstStart2 implements Action
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      if (isForward)
      {
        try
        {
          dbType = (DBSupport) getDBType().getValue();

          // Das Vergleichen der Passworte machen wir gleich
          if (dbType.needsPassword())
          {
            String pw1 = (String) getPassword().getValue();
            String pw2 = (String) getPassword2().getValue();
            if (pw1 == null || pw1.length() == 0)
              throw new ApplicationException(i18n.tr("Bitte geben Sie ein Passwort für die Datenbank an"));
            if (!pw1.equals(pw2))
              throw new ApplicationException(i18n.tr("Die beiden Passwörter stimmen nicht überein"));
          }
          if (dbType.needsUsername())     dbType.setUsername((String) getUsername().getValue());
          if (dbType.needsPassword())     dbType.setPassword((String) getPassword().getValue());
          if (dbType.needsHostname())     dbType.setHostname((String) getHostname().getValue());
          if (dbType.needsDatabaseName()) dbType.setDatabaseName((String) getDBName().getValue());

          Integer p = (Integer) getPort().getValue();
          if (p != null && dbType.needsTcpPort())
            dbType.setTcpPort(p.intValue());

          dbType.create(getProgressMonitor());
          dbType.store();
          
          // Jetzt koennen wir den DBService starten
          Service service = Application.getServiceFactory().lookup(Fibu.class,"database");
          service.start();
          service = Application.getServiceFactory().lookup(Fibu.class,"engine");
          service.start();
          
          new FirstStart2CreateFinanzamt().handleAction(FirstStartControl.this);
        }
        catch (Exception e)
        {
          Logger.error("error while checking database",e);
          throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Datenbank. {0}",e.getLocalizedMessage()));
        }
      }
      else
      {
        try
        {
          dbType = (DBSupport) getDBType().getValue();
          new FirstStart1CreateDatabase().handleAction(FirstStartControl.this);
        }
        catch (RemoteException re)
        {
          Logger.error("error while applying database",re);
          throw new ApplicationException(i18n.tr("Fehler beim Übernehmen der Datenbank-Einstellungen. {0}",re.getLocalizedMessage()));
        }
      }
    }
  }
  

  /**
   * Action, die fuer Seite 3 des Wizards ausgefuehrt wird.
   */
  private class FirstStart3 implements Action
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      if (isForward)
      {
        try
        {
          new FirstStart3CreateMandant().handleAction(FirstStartControl.this);
        }
        catch (Exception e)
        {
          Logger.error("error while checking database",e);
          throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Datenbank. {0}",e.getLocalizedMessage()));
        }
      }
      else
      {
        new FirstStart2CreateFinanzamt().handleAction(FirstStartControl.this);
      }
    }
  }
  
}


/*********************************************************************
 * $Log: FirstStartControl.java,v $
 * Revision 1.5  2006/06/19 16:25:42  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
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