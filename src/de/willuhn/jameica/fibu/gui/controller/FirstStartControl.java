/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FirstStartControl.java,v $
 * $Revision: 1.10 $
 * $Date: 2006/06/27 23:30:47 $
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
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.Welcome;
import de.willuhn.jameica.fibu.gui.part.GeschaeftsjahrList;
import de.willuhn.jameica.fibu.gui.part.MandantList;
import de.willuhn.jameica.fibu.gui.views.FirstStart1CreateDatabase;
import de.willuhn.jameica.fibu.gui.views.FirstStart2CreateFinanzamt;
import de.willuhn.jameica.fibu.gui.views.FirstStart3CreateMandant;
import de.willuhn.jameica.fibu.gui.views.FirstStart4CreateGeschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.DBSupport;
import de.willuhn.jameica.fibu.rmi.Finanzamt;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.NavigationItem;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.PasswordInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.ProgressBar;
import de.willuhn.jameica.plugin.PluginContainer;
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

  private FinanzamtControl faControl      = null;
  private MandantControl maControl        = null;
  private GeschaeftsjahrControl gjControl = null;
  
  private MandantList maList           = null;
  private GeschaeftsjahrList gjList    = null;
  
  private Finanzamt fa                 = null;
  private Mandant ma                   = null;
  private Geschaeftsjahr gj            = null;

  private ProgressBar monitor          = null;
  
  private DBSupport[] dbTypes          = null;
  private DBSupport dbType             = null;
  
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
    this.pages.add(new FirstStart4());
    this.pages.add(new FirstStart5());
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
   * Liefert eine Liste mit verfuegbaren Mandanten.
   * @return Liste der Mandanten.
   * @throws RemoteException
   */
  public MandantList getMandantList() throws RemoteException
  {
    if (this.maList == null)
      this.maList = new MandantList(null);
    return this.maList;
  }
  
  /**
   * Liefert eine Liste der Geschaeftsjahre des Mandanten.
   * @return Liste der Geschaeftsjahre des Mandanten.
   * @throws RemoteException
   */
  public GeschaeftsjahrList getGeschaeftsjahrList() throws RemoteException
  {
    if (this.gjList == null)
      this.gjList = new GeschaeftsjahrList(this.ma,null);
    return this.gjList;
  }
  
  /**
   * Liefert den Controller fuer das Finanzamt.
   * @return Controller.
   */
  public FinanzamtControl getFinanzamtControl()
  {
    if (this.faControl == null)
      this.faControl = new FinanzamtControl(null);
    return this.faControl;
  }

  /**
   * Liefert den Controller fuer den Mandanten.
   * @return Controller.
   */
  public MandantControl getMandantControl()
  {
    if (this.maControl == null)
      this.maControl = new MandantControl(null);
    return this.maControl;
  }

  /**
   * Liefert den Controller fuer das Geschaeftsjahr.
   * @return Controller.
   */
  public GeschaeftsjahrControl getGeschaeftsjahrControl()
  {
    if (this.gjControl == null)
      this.gjControl = new GeschaeftsjahrControl(null);
    return this.gjControl;
  }
  
  /**
   * Liefert das Finanzamt.
   * @return das Finanzamt.
   * @throws RemoteException
   */
  public Finanzamt getFinanzamt() throws RemoteException
  {
    if (this.fa == null)
      this.fa = getFinanzamtControl().getFinanzamt();
    return this.fa;
  }
  
  /**
   * Liefert den Mandanten.
   * @return der Mandant.
   * @throws RemoteException
   */
  public Mandant getMandant() throws RemoteException
  {
    if (this.ma == null)
      this.ma = getMandantControl().getMandant();
    return this.ma;
  }
  
  /**
   * Liefert das Geschaeftsjahr.
   * @return das Geschaeftsjahr.
   * @throws RemoteException
   */
  public Geschaeftsjahr getGeschaeftsjahr() throws RemoteException
  {
    if (this.gj == null)
      this.gj = getGeschaeftsjahrControl().getGeschaeftsjahr();
    return this.gj;
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
   * @throws RemoteException
   */
  public PasswordInput getPassword2() throws RemoteException
  {
    if (this.inputPassword2 == null)
    {
      this.inputPassword2 = new PasswordInput(((DBSupport)getDBType().getValue()).getPassword());
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
   * Action, die fuer Seite 1 des Wizards ausgefuehrt wird.
   */
  private class FirstStart1 implements Action
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      GUI.startView(FirstStart1CreateDatabase.class,FirstStartControl.this);
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
        if (!service.isStarted())
          service.start();
        service = Application.getServiceFactory().lookup(Fibu.class,"engine");
        if (!service.isStarted())
          service.start();

        // Mal schauen, ob wir vielleicht schon ein Geschaeftsjahr haben, dann
        // koennen wir die Erstellung des Finanzamtes und Mandanten ueberspringen
        DBIterator gjList = Settings.getDBService().createList(Geschaeftsjahr.class);
        DBIterator maList = Settings.getDBService().createList(Mandant.class);
        DBIterator faList = Settings.getDBService().createList(Finanzamt.class);
        if (gjList.size() > 0)
        {
          Geschaeftsjahr j = (Geschaeftsjahr) gjList.next();
          ma = j.getMandant();
          fa = ma.getFinanzamt();
          wizardIndex+=2;
          GUI.startView(FirstStart4CreateGeschaeftsjahr.class,FirstStartControl.this);
        }
        else if(maList.size() > 0)
        {
          // Wenn schon ein Mandant existiert, muss auch ein Finanzamt
          // existieren. Also ueberspringen wir Auswahl/Erstellung des Finanzamtes
          ma = (Mandant) maList.next();
          fa = ma.getFinanzamt();
          wizardIndex++;
          GUI.startView(FirstStart3CreateMandant.class,FirstStartControl.this);
        }
        else if(faList.size() > 0)
        {
          // Es existiert schon ein Finanzamt. Also koennen wir gleich zu
          // den Mandant-Details wechseln
          wizardIndex++;
          GUI.startView(FirstStart3CreateMandant.class,FirstStartControl.this);
        }
        else
        {
          GUI.startView(FirstStart2CreateFinanzamt.class,FirstStartControl.this);
        }
      }
      catch (ApplicationException ae)
      {
        throw ae;
      }
      catch (Exception e)
      {
        Logger.error("error while checking database",e);
        throw new ApplicationException(i18n.tr("Fehler beim Erstellen der Datenbank. {0}",e.getLocalizedMessage()));
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
      try
      {
        if (getFinanzamtControl().handleStore())
          GUI.startView(FirstStart3CreateMandant.class,FirstStartControl.this);
        else
          wizardIndex--;
      }
      catch (Exception e)
      {
        Logger.error("error while creating finanzamt",e);
        throw new ApplicationException(i18n.tr("Fehler beim Erstellen des Finanzamtes. {0}",e.getLocalizedMessage()));
      }
    }
  }
  

  /**
   * Action, die fuer Seite 4 des Wizards ausgefuehrt wird.
   */
  private class FirstStart4 implements Action
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      try
      {
        DBIterator maList = Settings.getDBService().createList(Mandant.class);
        if (maList.size() > 0)
        {
          // Es existiert ein Mandant, also muss der User einen auswaehlen
          ma = (Mandant) getMandantList().getSelection();
          if (ma == null)
            throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Mandanten aus"));
          GUI.startView(FirstStart4CreateGeschaeftsjahr.class,FirstStartControl.this);
        }
        else
        {
          getMandantControl().getMandant().setFinanzamt(fa);
          if (getMandantControl().handleStore())
          {
            // Speichern des neuen Mandant erfolgreich.
            GUI.startView(FirstStart4CreateGeschaeftsjahr.class,FirstStartControl.this);
          }
          else
          {
            wizardIndex--;
          }
        }
      }
      catch (ApplicationException ae)
      {
        throw ae;
      }
      catch (Exception e)
      {
        Logger.error("error while creating mandant",e);
        throw new ApplicationException(i18n.tr("Fehler beim Erstellen des Mandanten. {0}",e.getLocalizedMessage()));
      }
    }
  }

  /**
   * Action, die fuer Seite 5 des Wizards ausgefuehrt wird.
   */
  private class FirstStart5 implements Action
  {
    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      try
      {
        DBIterator gjList = Settings.getDBService().createList(Geschaeftsjahr.class);
        if (gjList.size() > 0)
        {
          // Es existiert ein Geschaeftsjahr, also muss der User eins auswaehlen
          gj = (Geschaeftsjahr) getGeschaeftsjahrList().getSelection();
          if (gj == null)
            throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Geschäftsjahr aus"));
          Settings.setActiveGeschaeftsjahr(gj);
          handleStart();
        }
        else
        {
          getGeschaeftsjahrControl().getGeschaeftsjahr().setMandant(ma);
          if (getGeschaeftsjahrControl().handleStore())
          {
            Settings.setActiveGeschaeftsjahr(getGeschaeftsjahrControl().getGeschaeftsjahr());
            handleStart();
          }
          else
          {
            wizardIndex--;
          }
        }
      }
      catch (Exception e)
      {
        Logger.error("error while creating gj",e);
        throw new ApplicationException(i18n.tr("Fehler beim Erstellen des Geschäftsjahres. {0}",e.getLocalizedMessage()));
      }
    }
  }

  /**
   * Beendet den Wizard und startet die Welcome-Seite.
   * @throws Exception
   */
  private void handleStart() throws Exception
  {
    PluginContainer pc = Application.getPluginLoader().getPluginContainer(Fibu.class);
    NavigationItem navi = pc.getManifest().getNavigation();
    navi.setEnabled(true,true);

    new Welcome().handleAction(null);
  }
}


/*********************************************************************
 * $Log: FirstStartControl.java,v $
 * Revision 1.10  2006/06/27 23:30:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2006/06/20 21:05:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2006/06/20 18:09:46  willuhn
 * @N Wizard seems to work now
 *
 * Revision 1.7  2006/06/19 22:41:47  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2006/06/19 22:23:47  willuhn
 * @N Wizard
 *
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