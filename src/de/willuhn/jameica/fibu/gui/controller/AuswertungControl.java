/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/controller/AuswertungControl.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/10/06 22:50:32 $
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

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.ExportAction;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;
import de.willuhn.util.I18N;

/**
 * Controller fuer die Auswertungen.
 */
public class AuswertungControl extends AbstractControl
{
  private I18N i18n = null;
  private Input auswertungen = null;
  
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
    GenericIterator gi = PseudoIterator.fromArray((ExportObject[]) list.toArray(new ExportObject[list.size()]));
    auswertungen = new SelectInput(gi,null);
    return auswertungen;
  }
  
  /**
   * Fuehrt die ausgewaehlte Auswertung aus.
   */
  public void handleExecute()
  {
    
  }
  
  /**
   * Hilfsklasse.
   */
  private class ExportObject implements GenericObject
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
    
  }

}


/*********************************************************************
 * $Log: AuswertungControl.java,v $
 * Revision 1.1  2005/10/06 22:50:32  willuhn
 * @N auswertungen
 *
 **********************************************************************/