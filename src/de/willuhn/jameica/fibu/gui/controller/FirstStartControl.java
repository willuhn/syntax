/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/FirstStartControl.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/06/12 14:08:29 $
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
import java.sql.Connection;
import java.sql.DriverManager;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.FirstStart;
import de.willuhn.jameica.fibu.gui.action.FirstStart2CreateDatabase;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.PasswordInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
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
  
  private DBSupport dbType = null;
  private String username  = "syntax";
  private String password  = null;
  private String password2 = null;
  private String dbname    = "syntax";
  private String hostname  = "database.hostname";
  private int port         = 3306;
  
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
      DBSupport[] list = new DBSupport[2];
      list[0] = new McKoi();
      list[1] = new MySQL();
      inputDbType = new SelectInput(PseudoIterator.fromArray(list),this.dbType);
      inputDbType.addListener(new Listener() {
        public void handleEvent(Event event)
        {
          try
          {
            DBSupport s = (DBSupport) inputDbType.getValue();
            s.handleAction(null);
          }
          catch(ApplicationException ae)
          {
            GUI.getView().setErrorText(ae.getMessage());
          }
        }
      });
      try
      {
        DBSupport s = this.dbType != null ? this.dbType : list[0];
        s.handleAction(null);
      }
      catch (ApplicationException ae)
      {
        GUI.getView().setErrorText(ae.getMessage());
      }
    }
    return inputDbType;
  }

  /**
   * Eingabe-Feld fuer den Usernamen.
   * @return Eingabe-Feld.
   */
  public TextInput getUsername()
  {
    if (this.inputUsername == null)
    {
      this.inputUsername = new TextInput(this.username);
      this.inputUsername.setComment(i18n.tr("Username des Datenbank-Benutzers"));
    }
    return this.inputUsername;
  }

  /**
   * Eingabe-Feld fuer den Namen der Datenbank.
   * @return Eingabe-Feld.
   */
  public TextInput getDBName()
  {
    if (this.inputDbname == null)
    {
      this.inputDbname = new TextInput(this.dbname);
      this.inputDbname.setComment(i18n.tr("Name der Datenbank"));
    }
    return this.inputDbname;
  }

  /**
   * Eingabe-Feld fuer das Passwort.
   * @return Eingabe-Feld.
   */
  public PasswordInput getPassword()
  {
    if (this.inputPassword == null)
    {
      this.inputPassword = new PasswordInput(this.password);
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
      this.inputPassword2 = new PasswordInput(this.password2);
      this.inputPassword2.setComment(i18n.tr("Geben Sie hier das Passwort nochmal zur Kontrolle ein"));
    }
    return this.inputPassword2;
  }
  
  /**
   * Liefert ein Eingabe-Feld fuer den Hostnamen der Datenbank.
   * @return Eingabe-Feld.
   */
  public TextInput getHostname()
  {
    if (this.inputHostname == null)
    {
      this.inputHostname = new TextInput(this.hostname);
      this.inputHostname.setComment(i18n.tr("Hostname des Datenbank-Servers"));
    }
    return this.inputHostname;
  }
  
  /**
   * Eingabe-Feld fuer den TCP-Port der Datenbank. 
   * @return Eingabe-Feld.
   */
  public IntegerInput getPort()
  {
    if (this.inputPort == null)
    {
      this.inputPort = new IntegerInput(this.port);
      this.inputPort.setComment(i18n.tr("TCP-Port des Datenbank-Servers"));
    }
    return this.inputPort;
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
   * Testet die ausgewaehlte Datenbank auf Verfuegbarkeit und leitet
   * auf die naechste Seite weiter.
   */
  public void handle1ChooseDatabase()
  {
    try
    {
      handleApply();
      this.dbType.test();
      new FirstStart2CreateDatabase().handleAction(this);
    }
    catch (ApplicationException ae)
    {
      GUI.getView().setErrorText(ae.getMessage());
    }
    catch (RemoteException re)
    {
      Logger.error("error while checking database",re);
      GUI.getView().setErrorText(i18n.tr("Fehler beim Testen der Datenbank"));
    }
  }
  
  /**
   * Erstellt die ausgewaehlte Datenbank.
   */
  public void handleCreateDatabase()
  {
    
  }

  /**
   * Uebernimmt die Daten des Formulars.
   * @throws RemoteException
   */
  public void handleApply() throws RemoteException
  {
    this.username  =  (String) getUsername().getValue();
    this.dbname    =  (String) getDBName().getValue();
    this.hostname  =  (String) getHostname().getValue();
    this.password  =  (String) getPassword().getValue();
    this.password2 =  (String) getPassword2().getValue();
    Integer p = (Integer) getPort().getValue();
    if (p != null)
      this.port = p.intValue();

    this.dbType = (DBSupport) getDBType().getValue();
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
  
  
  
  /**
   * Hilfs-Interface zur Auswahl der Datenbank.
   */
  private interface DBSupport extends GenericObject, Action
  {
    /**
     * Testet die ausgewaehlte Datenbank.
     * @throws ApplicationException
     */
    public void test() throws ApplicationException;
  }
  
  /**
   * Hilfsklasse fuer MySQL-Support.
   */
  private class MySQL implements DBSupport
  {
    /**
     * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) throws RemoteException
    {
      return "MySQL";
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getAttributeNames()
     */
    public String[] getAttributeNames() throws RemoteException
    {
      return new String[]{"name"};
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getID()
     */
    public String getID() throws RemoteException
    {
      return this.getClass().getName();
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
     */
    public String getPrimaryAttribute() throws RemoteException
    {
      return "name";
    }

    /**
     * @see de.willuhn.datasource.GenericObject#equals(de.willuhn.datasource.GenericObject)
     */
    public boolean equals(GenericObject arg0) throws RemoteException
    {
      if (arg0 == null)
        return false;
      return getID().equals(arg0.getID());
    }

    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      getDBName().enable();
      getUsername().enable();
      getHostname().enable();
      getPort().enable();
      getPassword().enable();
      getPassword2().enable();
    }

    /**
     * @see de.willuhn.jameica.fibu.gui.controller.FirstStartControl.DBSupport#test()
     */
    public void test() throws ApplicationException
    {
      
      if (dbname == null || dbname.length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie den Namen der Datenbank an"));
      if (username == null || username.length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Benutzernamen an"));
      if (hostname == null || hostname.length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen Hostnamen für die Datenbank an"));
      if (password == null || password.length() == 0)
        throw new ApplicationException(i18n.tr("Bitte geben Sie ein Passwort für die Datenbank an"));
      if (!password.equals(password2))
        throw new ApplicationException(i18n.tr("Die beiden Passwörter stimmen nicht überein"));
      if (port <= 0 || port > 65535)
        throw new ApplicationException(i18n.tr("Bitte geben Sie einen gültigen TCP-Port ein"));
      
      try
      {
        Class.forName("com.mysql.jdbc.Driver");
      }
      catch (Throwable t)
      {
        Logger.error("unable to load jdbc driver",t);
        throw new ApplicationException(i18n.tr("Fehler beim Laden des JDBC-Treibers"));
      }

      Connection conn = null;
      try
      {
        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port +
                           "/" + dbname + "?dumpQueriesOnException=true&amp;useUnicode=true&amp;characterEncoding=ISO8859_1";
        Logger.info("using jdbc url: " + jdbcUrl);
        conn = DriverManager.getConnection(jdbcUrl,username,password);
        conn.close();
        GUI.getView().setSuccessText(i18n.tr("Datenbankverbindung erfolgreich getestet"));
      }
      catch (Throwable t)
      {
        Logger.error("unable to connect to database",t);

        Throwable tOrig = t.getCause();
        
        String msg = t.getLocalizedMessage();
        if (tOrig != null & tOrig != t)
          msg += ". " + tOrig.getLocalizedMessage();
        throw new ApplicationException(msg);
      }
    }
    
  }

  /**
   * Hilfsklasse fuer McKoi-Support.
   */
  private class McKoi implements DBSupport
  {
    /**
     * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) throws RemoteException
    {
      return "Embedded Datenbank (McKoi)";
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getAttributeNames()
     */
    public String[] getAttributeNames() throws RemoteException
    {
      return new String[]{"name"};
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getID()
     */
    public String getID() throws RemoteException
    {
      return this.getClass().getName();
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
     */
    public String getPrimaryAttribute() throws RemoteException
    {
      return "name";
    }

    /**
     * @see de.willuhn.datasource.GenericObject#equals(de.willuhn.datasource.GenericObject)
     */
    public boolean equals(GenericObject arg0) throws RemoteException
    {
      if (arg0 == null)
        return false;
      return getID().equals(arg0.getID());
    }

    /**
     * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
     */
    public void handleAction(Object context) throws ApplicationException
    {
      getDBName().disable();
      getUsername().disable();
      getHostname().disable();
      getPort().disable();
      getPassword().disable();
      getPassword2().disable();
    }

    /**
     * @see de.willuhn.jameica.fibu.gui.controller.FirstStartControl.DBSupport#test()
     */
    public void test() throws ApplicationException
    {
      // Hier gibts nichts zu testen
    }
    
  }

}


/*********************************************************************
 * $Log: FirstStartControl.java,v $
 * Revision 1.1  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 **********************************************************************/