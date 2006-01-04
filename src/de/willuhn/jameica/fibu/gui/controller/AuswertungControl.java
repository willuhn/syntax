/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AuswertungControl.java,v $
 * $Revision: 1.3 $
 * $Date: 2006/01/04 17:59:11 $
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
import java.util.Collections;
import java.util.Date;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.ExportAction;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.CalendarDialog;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ClassFinder;
import de.willuhn.util.I18N;

/**
 * Controller fuer die Auswertungen.
 */
public class AuswertungControl extends AbstractControl
{
  private I18N i18n           = null;
  private Input auswertungen  = null;
  private Input jahr          = null;
  private Input start         = null;
  private Input end           = null;
  
  /**
   * @param view
   */
  public AuswertungControl(AbstractView view)
  {
    super(view);
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  }
  
  /**
   * Liefert eine Liste der verfuegbaren Auswertungen.
   * @return Liste der Auswertungen.
   * @throws RemoteException
   */
  public Input getAuswertungen() throws RemoteException
  {
    if (auswertungen != null)
      return auswertungen;
    
    ClassFinder cf = Application.getClassLoader().getClassFinder();
    Class[] impls = new Class[0];
    try
    {
      impls = cf.findImplementors(ExportAction.class);
    }
    catch (ClassNotFoundException e)
    {
      Logger.warn("no exports found");
      auswertungen = new LabelInput(i18n.tr("Keine Auswertungen verfügbar"));
      return auswertungen;
    }

    ArrayList list = new ArrayList();
    
    Class c = null;
    for (int i=0;i<impls.length;++i)
    {
      c = impls[i];
      try
      {
        list.add(new ExportObject((ExportAction)c.newInstance()));
      }
      catch (Exception e)
      {
        Logger.error("error while loading class " + c,e);
      }
    }
    Collections.sort(list);
    GenericIterator gi = PseudoIterator.fromArray((ExportObject[]) list.toArray(new ExportObject[list.size()]));
    auswertungen = new SelectInput(gi,null);
    auswertungen.setComment("");
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
    
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    Date begin = current.getBeginn();
    CalendarDialog d = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    d.setDate(begin);
    d.setTitle(i18n.tr("Beginn des Geschäftsjahres"));
    d.setText(i18n.tr("Wählen Sie bitte den Beginn des Geschäftsjahres aus"));
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        try
        {
          Date d = (Date) event.data;
          getStart().setValue(d);
          ((DialogInput)getStart()).setText(Fibu.DATEFORMAT.format(d));
        }
        catch (Exception e)
        {
          Logger.error("unable to set start date",e);
          GUI.getStatusBar().setErrorText(i18n.tr("Fehler bei der Auswahl des Datums"));
        }
        
      }
    });
    this.start = new DialogInput(Fibu.DATEFORMAT.format(begin),d);
    this.start.setValue(begin);
    this.start.setComment("");
    ((DialogInput)this.start).disableClientControl();
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
    
    Geschaeftsjahr current = Settings.getActiveGeschaeftsjahr();
    Date e = current.getEnde();
    CalendarDialog d = new CalendarDialog(CalendarDialog.POSITION_MOUSE);
    d.setDate(e);
    d.setTitle(i18n.tr("Ende des Geschäftsjahres"));
    d.setText(i18n.tr("Wählen Sie bitte das Ende des Geschäftsjahres aus"));
    d.addCloseListener(new Listener() {
      public void handleEvent(Event event)
      {
        if (event == null || event.data == null)
          return;
        try
        {
          Date d = (Date) event.data;
          getEnd().setValue(d);
          ((DialogInput)getEnd()).setText(Fibu.DATEFORMAT.format(d));
        }
        catch (Exception e)
        {
          Logger.error("unable to set end date",e);
          GUI.getStatusBar().setErrorText(i18n.tr("Fehler bei der Auswahl des Datums"));
        }
        
      }
    });
    this.end = new DialogInput(Fibu.DATEFORMAT.format(e),d);
    this.end.setValue(e);
    this.end.setComment("");
    ((DialogInput)this.end).disableClientControl();
    return this.end;
  }
  
  /**
   * Fuehrt die ausgewaehlte Auswertung aus.
   */
  public void handleExecute()
  {
    try
    {
      ExportObject o = (ExportObject) getAuswertungen().getValue();
      ExportAction action = o.action;
      action.setStart((Date)getStart().getValue());
      action.setEnd((Date)getEnd().getValue());
      action.handleAction(getJahr().getValue());
    }
    catch (ApplicationException ae)
    {
      GUI.getStatusBar().setErrorText(ae.getMessage());
    }
    catch (RemoteException e)
    {
      Logger.error("unable to create report",e);
      GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Erstellen der Auswertung"));
    }
    
  }
  
  /**
   * Hilfsklasse.
   */
  private class ExportObject implements GenericObject, Comparable
  {
    private ExportAction action = null;
    
    /**
     * @param action
     */
    private ExportObject(ExportAction action)
    {
      this.action = action;
    }

    /**
     * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) throws RemoteException
    {
      return action.getName();
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
      return action.getClass().getName();
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
      return this.action.getClass().equals(((ExportObject)arg0).action.getClass());
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
      if (o == null || !(o instanceof ExportObject))
        return 1;
      
      try
      {
        ExportObject other = (ExportObject) o;
        String myName    = (String) getAttribute(getPrimaryAttribute());
        String otherName = (String) other.getAttribute(other.getPrimaryAttribute());
        return myName.compareTo(otherName);
      }
      catch (Exception e)
      {
        Logger.error("unable to compare objects",e);
        return 0;
      }
      
    }
    
  }

}


/*********************************************************************
 * $Log: AuswertungControl.java,v $
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